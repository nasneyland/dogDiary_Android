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
import com.najin.dogdiary.calendar.CalendarSet;
import com.najin.dogdiary.home.HomeActivity;
import com.najin.dogdiary.model.DogVO;
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

public class DogUpdateActivity extends AppCompatActivity {

    //?????? ?????? ??????
    private static final int REQUEST_CODE = 1;

    ConstraintLayout dogLoadingView;
    ImageView dogInsertProfileImage;
    EditText dogNameText;
    TextView nameCheck;
    Button femaleButton;
    Button maleButton;
    TextView birthDayText;

    String dogName;
    Integer gender;
    String dogBirth;

    Boolean updateProfile = false;
    Boolean profileMode;
    Boolean checkName = true;
    Button insertButton;

    SharedPreferences user;
    DogVO dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_dog);

        dogLoadingView = (ConstraintLayout) findViewById(R.id.dogLoadingView);
        dogLoadingView.setVisibility(View.GONE);

        user = getSharedPreferences("user",MODE_PRIVATE);
        dog = HomeVO.getInstance().getDog();

        //????????? ????????? ??????
        dogInsertProfileImage = (ImageView) findViewById(R.id.dogInsertProfileImage);
        dogInsertProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
        dogInsertProfileImage.setClipToOutline(true);

        //????????? ????????? ????????? ??????
        if (!dog.getImage().equals("")) {
            String url = Common.URL + "/media/" + dog.getImage();
            Glide.with(this)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.img_profile)
                    .error(R.drawable.img_profile)
                    .fallback(R.drawable.img_profile)
                    .into(dogInsertProfileImage);
        } else {
            dogInsertProfileImage.setImageResource(R.drawable.img_profile);
        }

        dogInsertProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] oItems = {"???????????? ?????? ??????", "??????????????? ??????"};
                AlertDialog.Builder oDialog = new AlertDialog.Builder(DogUpdateActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                oDialog.setItems(oItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        checkValue();
                        if (which == 0) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            dogInsertProfileImage.setImageResource(R.drawable.img_profile);
                            updateProfile = true;
                            profileMode = false;
                        }
                    }
                }).setCancelable(true).show();
            }
        });

        //????????? ?????? ??????
        nameCheck = (TextView) findViewById(R.id.nameCheck);
        nameCheck.setVisibility(View.GONE);

        dogNameText = (EditText) findViewById(R.id.dogName);
        dogName = dog.getName();
        dogNameText.setText(dogName);
        dogNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 8) {
                    //????????? 8??? ????????? ???
                    nameCheck.setVisibility(View.VISIBLE);
                    nameCheck.setText("????????? 1~8?????? ??????????????????");
                    checkName = false;
                } else if (s.length() < 1) {
                    //???????????? ?????? ???
                    nameCheck.setVisibility(View.GONE);
                    checkName = false;
                } else {
                    //????????? ??? ?????? ??????
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

        //?????? ??????
        femaleButton = (Button) findViewById(R.id.femaleButton);
        maleButton = (Button) findViewById(R.id.maleButton);

        gender = dog.getGender();
        if (gender == 1) {
            maleButton.setBackgroundResource(R.drawable.button_border_select);
            maleButton.setTextColor(Color.parseColor("#5e4fa2"));
            maleButton.setTypeface(null, Typeface.BOLD);
            femaleButton.setBackgroundResource(R.drawable.button_border_unselect);
            femaleButton.setTextColor(Color.parseColor("gray"));
            femaleButton.setTypeface(null, Typeface.NORMAL);
        } else {
            femaleButton.setBackgroundResource(R.drawable.button_border_select);
            femaleButton.setTextColor(Color.parseColor("#5e4fa2"));
            femaleButton.setTypeface(null, Typeface.BOLD);
            maleButton.setBackgroundResource(R.drawable.button_border_unselect);
            maleButton.setTextColor(Color.parseColor("gray"));
            maleButton.setTypeface(null, Typeface.NORMAL);
        }

        femaleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                femaleButton.setBackgroundResource(R.drawable.button_border_select);
                femaleButton.setTextColor(Color.parseColor("#5e4fa2"));
                femaleButton.setTypeface(null, Typeface.BOLD);
                maleButton.setBackgroundResource(R.drawable.button_border_unselect);
                maleButton.setTextColor(Color.parseColor("gray"));
                maleButton.setTypeface(null, Typeface.NORMAL);
                gender = 2;
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
                checkValue();
            }
        });

        //?????? ??????
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
                checkValue();
            }
        };

        birthDayText = (TextView) findViewById(R.id.birthDayView);
        dogBirth = dog.getBirth();
        birthDayText.setText(dogBirth);
        birthDayText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int mYear =  Integer.parseInt(dogBirth.substring(0,4));
                int mMonth = Integer.parseInt(dogBirth.substring(5,7));
                int mDay = Integer.parseInt(dogBirth.substring(8,10));

                DatePickerDialog dialog = new DatePickerDialog(DogUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog, listener, mYear, mMonth-1, mDay);

                Calendar c = new GregorianCalendar();
                int year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                int day=c.get(Calendar.DAY_OF_MONTH);

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(year,month,day);
                dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                dialog.setTitle("??????");
                dialog.show();
            }
        });

        //???????????? ??????
        insertButton = (Button) findViewById(R.id.insertButton);
        insertButton.setEnabled(false);
        insertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dogName!=null  && !dogName.contains(" ") && dogName.matches("[0-9|a-z|A-Z|???-???|???-???|???-???| ]*")) {
                    dogLoadingView.setVisibility(View.VISIBLE);
                    insertButton.setEnabled(false);
                    updateDogInfo();
                }else {
                    nameCheck.setVisibility(View.VISIBLE);
                    nameCheck.setText("??????,??????,????????? ??????????????????");
                    checkName = false;
                }
            }
        });
    }

    //???????????? ????????? ?????? ?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    //????????? image view ??????
                    Uri uri = data.getData();
                    Glide.with(this)
                            .load(uri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.img_profile)
                            .error(R.drawable.img_profile)
                            .fallback(R.drawable.img_profile)
                            .into(dogInsertProfileImage);
                    updateProfile = true;
                    profileMode = true;
                } catch (Exception e) {

                }
            }
        }
    }

    //editText??? ?????? ??? ???????????? ????????? ???????????? ??????
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

    //?????? ????????? ????????? ??????
    private void checkValue() {
        if (checkName) {
            insertButton.setEnabled(true);
            insertButton.setBackgroundResource(R.drawable.button_member_enable);
        } else {
            insertButton.setEnabled(false);
            insertButton.setBackgroundResource(R.drawable.button_member_disable);
        }
    }

    //dog ?????? ??????
    private void updateDogInfo() {
        JSONObject dogObject = new JSONObject();
        try {
            dogObject.put("name", dogName);
            dogObject.put("gender", gender);
            dogObject.put("birth", dogBirth);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
            dogLoadingView.setVisibility(View.GONE);
            insertButton.setEnabled(true);
        }
        JsonObjectRequest dogRequest = new JsonObjectRequest(Request.Method.PUT, Common.URL + "/diary/dog/"+dog.getId(), dogObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!updateProfile) {
                            loadMemberData();
                        } else if (dog.getImage().equals("") && profileMode) {
                            //????????? ??????
                            uploadImg();
                        } else if (!dog.getImage().equals("") && profileMode) {
                            //????????? ??????
                            updateImg();
                        } else if (!dog.getImage().equals("") && !profileMode) {
                            //????????? ??????
                            deleteImg();
                        } else {
                            loadMemberData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                        dogLoadingView.setVisibility(View.GONE);
                        insertButton.setEnabled(true);
                    }
                });
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //????????? ????????? ??????
    private void deleteImg(){
        String imageUrl = Common.URL + "/diary/dog/profile/delete/" + dog.getId();
        StringRequest imageRequest = new StringRequest(
                Request.Method.DELETE,
                imageUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadMemberData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadMemberData();
                    }
                }
        );
        imageRequest.setShouldCache(false);
        AppHelper.requestQueue.add(imageRequest);
    }

    //????????? ????????? ??????
    private void updateImg(){
        String imageUrl = Common.URL + "/diary/dog/profile/delete/" + dog.getId();
        System.out.println(imageUrl);
        StringRequest imageRequest = new StringRequest(
                Request.Method.DELETE,
                imageUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        uploadImg();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadMemberData();
                    }
                }
        );
        imageRequest.setShouldCache(false);
        AppHelper.requestQueue.add(imageRequest);
    }

    //????????? ????????? ??????
    private void uploadImg(){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Common.URL+"/diary/dog/profile/"+dog.getId(),
            new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    loadMemberData();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadMemberData();
                }
            }) {

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Bitmap uploadProfile = getBitMap(dogInsertProfileImage.getDrawable());
                params.put("profile", new DataPart(dog.getId() + ".jpg", getFileDataFromDrawable(uploadProfile)));
                return params;
            }
        };
        volleyMultipartRequest.setShouldCache(false);
        AppHelper.requestQueue.add(volleyMultipartRequest);
    }

    //????????? ???????????? bitmap??????
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

    //member data ??????
    private void loadMemberData() {
        String memberUrl = Common.URL + "/diary/member/" + MemberVO.getInstance().getId();
        StringRequest memberRequest = new StringRequest(
                Request.Method.GET,
                memberUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //memberVO ?????????
                        Gson gson = new Gson();
                        MemberVO.setInstance(gson.fromJson(response, MemberVO.class));

                        loadHomeData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        memberRequest.setShouldCache(false);
        AppHelper.requestQueue.add(memberRequest);
    }

    //home data ??????
    private void loadHomeData() {
        String dogUrl = Common.URL + "/diary/home/" + dog.getId();
        StringRequest dogRequest = new StringRequest(
                Request.Method.GET,
                dogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeVO ?????????
                        Gson gson = new Gson();
                        HomeVO.setInstance(gson.fromJson(response, HomeVO.class));

                        CalendarSet.setCalendar(getApplicationContext());

                        startMainActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : ????????? ?????? ??????????????????",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        dogRequest.setShouldCache(false);
        AppHelper.requestQueue.add(dogRequest);
    }

    //?????? ???????????? ??????
    private void startMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), HomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
