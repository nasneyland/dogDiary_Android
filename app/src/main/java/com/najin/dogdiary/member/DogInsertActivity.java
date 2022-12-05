package com.najin.dogdiary.member;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;
import com.google.gson.Gson;
import com.najin.dogdiary.R;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.CalendarVO;
import com.najin.dogdiary.model.HomeVO;
import com.najin.dogdiary.model.MemberVO;
import com.najin.dogdiary.support.AppHelper;
import com.najin.dogdiary.support.Common;
import com.najin.dogdiary.support.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DogInsertActivity extends AppCompatActivity {

    //사진 전송 코드
    private static final int REQUEST_CODE = 1;

    ConstraintLayout dogLoadingView;
    ImageView dogInsertProfileImage;
    EditText dogNameText;
    TextView nameCheck;
    Button femaleButton;
    Button maleButton;
    TextView birthDayText;

    String dogId;
    String dogName;
    Integer gender;
    String dogBirth;

    Boolean checkImage = false;
    Boolean checkName = false;
    Boolean checkGender = false;
    Boolean checkBirth = false;
    Button insertButton;

    SharedPreferences user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dog);

        user = getSharedPreferences("user",MODE_PRIVATE);

        dogLoadingView = (ConstraintLayout) findViewById(R.id.dogLoadingView);
        dogLoadingView.setVisibility(View.GONE);

        //프로필 이미지 셋팅
        dogInsertProfileImage = (ImageView) findViewById(R.id.dogInsertProfileImage);
        dogInsertProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        dogInsertProfileImage.setClipToOutline(true);

        dogInsertProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] oItems = {"앨범에서 사진 선택", "기본이미지 선택"};
                AlertDialog.Builder oDialog = new AlertDialog.Builder(DogInsertActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                oDialog.setItems(oItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            dogInsertProfileImage.setImageResource(R.drawable.img_profile);
                            checkImage = false;
                        }
                    }
                }).setCancelable(true).show();
            }
        });

        //강아지 이름 셋팅
        nameCheck = (TextView) findViewById(R.id.nameCheck);
        nameCheck.setVisibility(View.GONE);

        dogNameText = (EditText) findViewById(R.id.dogName);
        dogNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 8) {
                    //이름이 8자 이상일 때
                    nameCheck.setVisibility(View.VISIBLE);
                    nameCheck.setText("이름을 1~8자로 설정해주세요");
                    checkName = false;
                } else if (s.length() < 1) {
                    //입력값이 없을 때
                    nameCheck.setVisibility(View.GONE);
                    checkName = false;
                } else {
                    //사용할 수 있는 이름
                    nameCheck.setVisibility(View.GONE);
                    dogName = s.toString();
                    checkName = true;
                }
                checkValue();
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        //성별 입력
        femaleButton = (Button) findViewById(R.id.femaleButton);
        maleButton = (Button) findViewById(R.id.maleButton);

        femaleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                femaleButton.setBackgroundResource(R.drawable.button_border_select);
                femaleButton.setTextColor(Color.parseColor("#5e4fa2"));
                femaleButton.setTypeface(null, Typeface.BOLD);
                maleButton.setBackgroundResource(R.drawable.button_border_unselect);
                maleButton.setTextColor(Color.parseColor("gray"));
                maleButton.setTypeface(null, Typeface.NORMAL);
                gender = 2;
                checkGender = true;
                checkValue();
            }
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                maleButton.setBackgroundResource(R.drawable.button_border_select);
                maleButton.setTextColor(Color.parseColor("#5e4fa2"));
                maleButton.setTypeface(null, Typeface.BOLD);
                femaleButton.setBackgroundResource(R.drawable.button_border_unselect);
                femaleButton.setTextColor(Color.parseColor("gray"));
                femaleButton.setTypeface(null, Typeface.NORMAL);
                gender = 1;
                checkGender = true;
                checkValue();
            }
        });

        //생일 입력
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear += 1;
                String month;
                String day;
                if (monthOfYear < 10) {
                    month = "0" + monthOfYear;
                } else {
                    month = "" + monthOfYear;
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                dogBirth = year+"-"+month+"-"+day;
                birthDayText.setText(dogBirth);
                checkBirth = true;
                checkValue();
            }
        };

        birthDayText = (TextView) findViewById(R.id.birthDayView);
        birthDayText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int mYear;
                int mMonth;
                int mDay;

                Calendar c = new GregorianCalendar();
                int year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                int day=c.get(Calendar.DAY_OF_MONTH);

                if (dogBirth == null){
                    mYear=year;
                    mMonth=month;
                    mDay=day;
                } else {
                    mYear = Integer.parseInt(dogBirth.substring(0, 4));
                    mMonth = Integer.parseInt(dogBirth.substring(5, 7))-1;
                    mDay = Integer.parseInt(dogBirth.substring(8, 10));
                }

                DatePickerDialog dialog = new DatePickerDialog(DogInsertActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener, mYear, mMonth, mDay);

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(year,month,day);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.setTitle("생일");
                dialog.show();
            }
        });

        //완료버튼 클릭
        insertButton = (Button) findViewById(R.id.insertButton);
        insertButton.setEnabled(false);
        insertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dogName!=null && !dogName.contains(" ")  && dogName.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝| ]*")) {
                    dogLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    sendDogInfo();
                } else {
                    nameCheck.setVisibility(View.VISIBLE);
                    nameCheck.setText("한글,영문,숫자만 입력해주세요");
                    checkName = false;
                }
            }
        });
    }

    //앨범에서 이미지 선택 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    //프로필 image view 셋팅
                    Uri uri = data.getData();
                    Glide.with(this)
                            .load(uri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.img_profile)
                            .error(R.drawable.img_profile)
                            .fallback(R.drawable.img_profile)
                            .into(dogInsertProfileImage);
                    checkImage = true;
                } catch (Exception e) {

                }
            }
        }
    }

    //editText가 아닌 곳 클릭하면 키보드 내려가게 하기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //전체 입력값 유효성 검사
    private void checkValue() {
        if (checkName && checkGender && checkBirth) {
            insertButton.setEnabled(true);
            insertButton.setBackgroundResource(R.drawable.button_member_enable);
        } else {
            insertButton.setEnabled(false);
            insertButton.setBackgroundResource(R.drawable.button_member_disable);
        }
    }

    //dog 정보 입력
    private void sendDogInfo() {
        JSONObject dog = new JSONObject();
        try {
            dog.put("member", MemberVO.getInstance().getId());
            dog.put("name", dogName);
            dog.put("gender", gender);
            dog.put("birth", dogBirth);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
            dogLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest dogRequest = new JsonObjectRequest(Request.Method.POST, Common.URL + "/diary/dog", dog,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //강아지 기본강아지로 셋팅
                            dogId = response.getString("id");
                            SharedPreferences.Editor editor = user.edit();
                            editor.putString("dog_id",dogId);
                            editor.apply();

                            if (checkImage) {
                                uploadImg();
                            } else {
                                loadMemberData(MemberVO.getInstance().getId());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                            dogLoadingView.setVisibility(View.GONE);
                            insertButton.setEnabled(true);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"서버 연결 실패 : 잠시후 다시 이용해주세요",Toast.LENGTH_SHORT).show();
                        dogLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        dogRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //프로필 이미지 입력
    private void uploadImg(){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Common.URL+"/diary/dog/profile/"+dogId,
            new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    loadMemberData(MemberVO.getInstance().getId());
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadMemberData(MemberVO.getInstance().getId());
                }
            }) {

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Bitmap uploadProfile = getBitMap(dogInsertProfileImage.getDrawable());
                params.put("profile", new DataPart(dogId + ".jpg", getFileDataFromDrawable(uploadProfile)));
                return params;
            }
        };
        volleyMultipartRequest.setShouldCache(false);
        AppHelper.requestQueue.add(volleyMultipartRequest);
    }

    //프로필 이미지를 bitmap으로
    private Bitmap getBitMap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof GlideBitmapDrawable) {
            bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof TransitionDrawable) {
            TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
            int length = transitionDrawable.getNumberOfLayers();
            for (int i = 0; i < length; ++i) {
                Drawable child = transitionDrawable.getDrawable(i);
                if (child instanceof GlideBitmapDrawable) {
                    bitmap = ((GlideBitmapDrawable) child).getBitmap();
                    break;
                } else if (child instanceof SquaringDrawable
                        && child.getCurrent() instanceof GlideBitmapDrawable) {
                    bitmap = ((GlideBitmapDrawable) child.getCurrent()).getBitmap();
                    break;
                }
            }
        } else if (drawable instanceof SquaringDrawable) {
            bitmap = ((GlideBitmapDrawable) drawable.getCurrent()).getBitmap();
        }

        return bitmap;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //member data 셋팅
    private void loadMemberData(String memberId) {
        String memberUrl = Common.URL + "/diary/member/" + memberId;
        StringRequest memberRequest = new StringRequest(
                Request.Method.GET,
                memberUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //memberVO 셋팅팅
                        Gson gson = new Gson();
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        loadHomeData();
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
    private void loadHomeData() {
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

                        CalendarVO.getInstance().destroyCalendarVO();

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
