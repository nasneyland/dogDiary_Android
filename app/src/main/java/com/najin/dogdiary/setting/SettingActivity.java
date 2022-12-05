package com.najin.dogdiary.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.najin.dogdiary.BuildConfig;
import com.najin.dogdiary.R;
import com.najin.dogdiary.calendar.CalendarSet;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.member.DogInsertActivity;
import com.najin.dogdiary.member.DogUpdateActivity;
import com.najin.dogdiary.member.LoginActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.DogVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;

public class SettingActivity extends AppCompatActivity {

    ConstraintLayout settingLoadingView;
    DogListAdapter dogAdapter;
    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        user = getSharedPreferences("user", Activity.MODE_PRIVATE);
        settingLoadingView = (ConstraintLayout) findViewById(R.id.settingLoadingView);
        settingLoadingView.setVisibility(View.GONE);

        //멍멍이 관리
        RecyclerView dogListView = (RecyclerView)findViewById(R.id.dogList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dogListView.setLayoutManager(layoutManager);

        dogAdapter = new DogListAdapter(getApplicationContext());
        for(int i = 0; i< MemberVO.getInstance().getDogList().size(); i++) {
            DogVO dog = MemberVO.getInstance().getDogList().get(i);
            dogAdapter.addItem(new DogListItem(dog.getId(),dog.getImage(), dog.getName(),user.getString("dog_id",null).equals(dog.getId())));
        }

        dogListView.setAdapter(dogAdapter);
        dogAdapter.setOnItemClickListener(new DogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DogListAdapter.ViewHolder holder, View view, int position) {
                if (MemberVO.getInstance().getDogList().size() == position) {
                    //강아지 정보 입력
                    Intent dogInsertIntent = new Intent(getApplicationContext(), DogInsertActivity.class);
                    startActivity(dogInsertIntent);
                } else {
                    DogListItem item = dogAdapter.getItem(position);
                    if (item.getSelected()){
                        final CharSequence[] oItems = {"정보 수정하기", "삭제하기"};
                        AlertDialog.Builder oDialog = new AlertDialog.Builder(SettingActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                        oDialog.setItems(oItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                settingLoadingView.setVisibility(View.VISIBLE);
                                if (which == 0) {
                                    //강아지 정보 수정
                                    Intent dogUpdateIntent = new Intent(getApplicationContext(), DogUpdateActivity.class);
                                    startActivity(dogUpdateIntent);
                                } else if (which == 1) {
                                    //강아지 정보 삭제
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                                    builder.setTitle(HomeVO.getInstance().getDog().getName()+" 삭제");
                                    builder.setMessage("등록하신 정보가 모두 사라집니다.\n정말 삭제하시겠습니까?");
                                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //db에서 강아지 정보 삭제
                                            String url = Common.URL + "/diary/dog/" + HomeVO.getInstance().getDog().getId();
                                            StringRequest request = new StringRequest(
                                                Request.Method.DELETE,
                                                url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        SharedPreferences.Editor editor = user.edit();
                                                        if (MemberVO.getInstance().getDogList().size() == 1) {
                                                            editor.remove("dog_id");
                                                            editor.commit();
                                                            HomeVO.getInstance().destroyHomeVO();
                                                            loadMemberData();
                                                        } else {
                                                            if (position == 0) {
                                                                editor.putString("dog_id",MemberVO.getInstance().getDogList().get(1).getId());
                                                                editor.apply();
                                                                CalendarSet.setCalendar(getApplicationContext());
                                                                loadHomeData(MemberVO.getInstance().getDogList().get(1).getId());
                                                            } else {
                                                                editor.putString("dog_id",MemberVO.getInstance().getDogList().get(0).getId());
                                                                editor.apply();
                                                                CalendarSet.setCalendar(getApplicationContext());
                                                                loadHomeData(MemberVO.getInstance().getDogList().get(0).getId());
                                                            }
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            );
                                            request.setShouldCache(false);
                                            AppHelper.requestQueue.add(request);
                                        }
                                    });
                                    builder.setNeutralButton("취소",null);
                                    builder.create().show();
                                }
                            }
                        }).setCancelable(true).show();

                    } else {
                        settingLoadingView.setVisibility(View.VISIBLE);
                        //강아지 기본강아지로 셋팅
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("dog_id",item.getId());
                        editor.apply();
                        CalendarSet.setCalendar(getApplicationContext());
                        loadHomeData(item.getId());
                    }
                }
            }
        });

        //어플정보
        //1.버전정보
        TextView versionText = (TextView) findViewById(R.id.versionText);
        versionText.setText("v "+ BuildConfig.VERSION_NAME);

        //2.버그 또는 문의
        LinearLayout sendBugMail = (LinearLayout) findViewById(R.id.sendBugMail);
        sendBugMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_SUBJECT, "버그 또는 문의 보내기");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"najinland@gmail.com"});
                email.putExtra(Intent.EXTRA_TEXT, String.format("App Version : %s\nDevice : %s\nAndroid(SDK) : %d(%s)\n\n내용 : ", BuildConfig.VERSION_NAME, Build.MODEL, Build.VERSION.SDK_INT, Build.VERSION.RELEASE));
                email.setType("message/rfc822");
                startActivity(email);
            }
        });


        //3.앱 평가하기
        LinearLayout appRating = (LinearLayout) findViewById(R.id.appRating);
        appRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("market://details?id=com.najin.dogdiary"));
                startActivity(intent);
            }
        });

        //4.앱 공유하기
        LinearLayout appShare = (LinearLayout) findViewById(R.id.appShare);
        appShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_TEXT, "🐶멍멍한하루🐶\n강아지의 모든 것을 담은 다이어리\n\nhttps://play.google.com/store/apps/details?id=com.najin.dogdiary");
                msg.putExtra(Intent.EXTRA_SUBJECT, "멍멍한하루");
                msg.putExtra(Intent.EXTRA_TITLE, "멍멍한하루");
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "앱을 선택해 주세요"));
            }
        });

        //5.자주 묻는 질문
        LinearLayout mostAsk = (LinearLayout) findViewById(R.id.mostAsk);
        mostAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faqActivity = new Intent(getApplicationContext(), FAQActivity.class);
                startActivity(faqActivity);
            }
        });


        //이용약관
        //1.서비스 이용약관
        LinearLayout termsAndUseView = (LinearLayout) findViewById(R.id.termsAndUseView);
        termsAndUseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsAndUseActivity = new Intent(getApplicationContext(), TermsAndUseActivity.class);
                startActivity(termsAndUseActivity);
            }
        });

        //2.개인정보 보호약관
        LinearLayout privacyView = (LinearLayout) findViewById(R.id.privacyView);
        privacyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyActivity = new Intent(getApplicationContext(), PrivacyActivity.class);
                startActivity(privacyActivity);
            }
        });

        //3.위치서비스 이용약관
        LinearLayout locationUseView = (LinearLayout) findViewById(R.id.locationUseView);
        locationUseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationUseActivity = new Intent(getApplicationContext(), LocationUseActivity.class);
                startActivity(locationUseActivity);
            }
        });


        //계정관리
        //1.회원등급
        LinearLayout userStatusView = (LinearLayout) findViewById(R.id.userStatusView);
        TextView userStatusText = (TextView) findViewById(R.id.userStatusText);
        /*
        userStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("회원등급 클릭");
            }
        });*/
        if (MemberVO.getInstance().getGrade() == 1) {
            userStatusText.setText("일반회원");
        } else {
            userStatusText.setText("VIP 회원");
        }

        //2.이메일 등록
        LinearLayout mailStatusView = (LinearLayout) findViewById(R.id.mailStatusView);
        mailStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(getApplicationContext(), MailInsertActivity.class);
                startActivity(mailIntent);
            }
        });
        TextView mailStatus = (TextView) findViewById(R.id.mailStatus);
        TextView mailText = (TextView) findViewById(R.id.mailText);
        if (MemberVO.getInstance().getMail().equals("")) {
            mailStatus.setText("이메일 등록");
            mailText.setText("");
        } else {
            mailStatus.setText("이메일 변경");
            mailText.setText(MemberVO.getInstance().getMail());
        }

        //3.로그아웃
        LinearLayout logoutView = (LinearLayout) findViewById(R.id.logoutView);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("로그아웃");
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setNegativeButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //자동로그인 정보 삭제
                        SharedPreferences.Editor editor = user.edit();
                        editor.remove("id");
                        editor.remove("dog_id");
                        editor.commit();
                        MemberVO.getInstance().destroyMemberVO();
                        HomeVO.getInstance().destroyHomeVO();
                        CalendarVO.getInstance().destroyCalendarVO();

                        //로그인화면으로 이동
                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginActivity);
                    }
                });
                builder.setNeutralButton("취소",null);
                builder.create().show();
            }
        });

        //4.회원탈퇴
        LinearLayout secessionView = (LinearLayout) findViewById(R.id.secessionView);
        secessionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("계정 삭제");
                builder.setMessage("등록하신 정보가 모두 삭제됩니다.\n정말 탈퇴하시겠습니까?");
                builder.setNegativeButton("탈퇴", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //db에서 계정정보 삭제
                        String url = Common.URL + "/diary/member/" + MemberVO.getInstance().getId();
                        StringRequest request = new StringRequest(
                                Request.Method.DELETE,
                                url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //자동로그인 정보 삭제
                                        SharedPreferences.Editor editor = user.edit();
                                        editor.remove("id");
                                        editor.remove("dog_id");
                                        editor.apply();

                                        MemberVO.getInstance().destroyMemberVO();
                                        HomeVO.getInstance().destroyHomeVO();
                                        CalendarVO.getInstance().destroyCalendarVO();

                                        //로그인화면으로 이동
                                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(loginActivity);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        request.setShouldCache(false);
                        AppHelper.requestQueue.add(request);
                    }
                });
                builder.setNeutralButton("취소",null);
                builder.create().show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingLoadingView.setVisibility(View.GONE);
    }

    //member data 셋팅
    private void loadMemberData() {
        String memberUrl = Common.URL + "/diary/member/" + MemberVO.getInstance().getId();
        StringRequest memberRequest = new StringRequest(
                Request.Method.GET,
                memberUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //memberVO 셋팅팅
                        Gson gson = new Gson();
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        startMainActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        memberRequest.setShouldCache(false);
        AppHelper.requestQueue.add(memberRequest);
    }

    //home data 셋팅
    private void loadHomeData(String dogId) {
        String dogUrl = Common.URL + "/diary/home/" + dogId;
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                dogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeVO 셋팅팅
                        Gson gson = new Gson();
                        HomeVO.setInstance(gson.fromJson(response, HomeVO.class));

                        CalendarSet.setCalendar(getApplicationContext());

                        loadMemberData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //메인 페이지로 이동
    private void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}


