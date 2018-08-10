package com.nemesis.nemesis.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemesis.nemesis.ARC;
import com.nemesis.nemesis.ActivityIdentifiers;
import com.nemesis.nemesis.Fragments.BottomFragment;
import com.nemesis.nemesis.Fragments.TopFragment;
import com.nemesis.nemesis.Http.HttpRequest;
import com.nemesis.nemesis.Pojos.DefaultResponse;
import com.nemesis.nemesis.Prefs.PrefUtils;
import com.nemesis.nemesis.R;
import com.squareup.picasso.Picasso;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.nemesis.nemesis.ActivityIdentifiers.BIO_FAILURE;
import static com.nemesis.nemesis.ActivityIdentifiers.BIO_SUCCESS;
import static com.nemesis.nemesis.ActivityIdentifiers.FINGERPRINT_SCAN_CODE;

public class CandidateAuth extends AppCompatActivity {

    @BindView(R.id.profile)
    CircleImageView profilephoto;
    @BindView(R.id.rollno)
    TextView rollno;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.instruction)
    TextView instruction;
    private String nam;
    private String enroll;

    @BindView(R.id.biometric)
    Button biometric;
    private String profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_auth);
        ButterKnife.bind(this);
        ActivityIdentifiers.setCurrentScreen(getApplicationContext(),ActivityIdentifiers.CANDIDATE_AUTH_SCREEN);

        getSupportFragmentManager().beginTransaction().add(R.id.topFrame,new TopFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.bottomframe,new BottomFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();

        Intent intent=getIntent();
        nam=intent.getStringExtra("name");
        enroll=intent.getStringExtra("enrollment");
        profile=intent.getStringExtra("profile");
        final String aadhaar=intent.getStringExtra("aadhaar");

        Picasso.with(getApplicationContext()).load("http://13.232.71.170/"+profile).noFade().into(profilephoto);
        rollno.setText("Enrollment No : "+enroll);
        name.setText(nam);

        biometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent=new Intent(getApplicationContext(),ScanningScreen.class);
                intent.putExtra(UID,aadhaar);
                startActivityForResult(intent, FINGERPRINT_SCAN_CODE);
                */
                //Use this code if FM220 device not available
                if(new Random().nextBoolean()){
                    onActivityResult(FINGERPRINT_SCAN_CODE, BIO_SUCCESS, null);
                }else{
                    onActivityResult(FINGERPRINT_SCAN_CODE, BIO_FAILURE, null);
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==FINGERPRINT_SCAN_CODE) {
            rx.Observable.create(new rx.Observable.OnSubscribe<DefaultResponse>() {
                @Override
                public void call(final Subscriber<? super DefaultResponse> subscriber) {
                    HttpRequest.ExamApiInterface examInterface = new HttpRequest(PrefUtils.getAccessToken(getApplicationContext()),"")
                            .retrofit.create(HttpRequest.ExamApiInterface.class);
                    Call<DefaultResponse> responseCall = examInterface.bioAttempt(
                                    PrefUtils.getInvigilatorId(getApplicationContext()),
                                    enroll
                    );
                    responseCall.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            if(response.code()==200){
                                subscriber.onNext(response.body());
                            }
                            else{
                                new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error : "+response.code())
                                        .setContentText(ARC.getPhrase(response.code()))
                                        .show();
                            }
                        }
                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Something Went Wrong")
                                    .setContentText("Check Your Internet Connection")
                                    .show();
                        }
                    });
                }
            })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<DefaultResponse>() {
                        @Override
                        public void call(DefaultResponse defaultResponse) {}});

            if(resultCode == BIO_SUCCESS) {

                rx.Observable.create(new rx.Observable.OnSubscribe<DefaultResponse>() {
                    @Override
                    public void call(final Subscriber<? super DefaultResponse> subscriber) {
                        HttpRequest.ExamApiInterface examInterface = new HttpRequest(PrefUtils.getAccessToken(getApplicationContext()),"")
                                .retrofit.create(HttpRequest.ExamApiInterface.class);
                        Call<DefaultResponse> responseCall = examInterface.authSuccess(
                                        PrefUtils.getInvigilatorId(getApplicationContext()),
                                        enroll
                        );
                        responseCall.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                if(response.code()==200){
                                    subscriber.onNext(response.body());
                                }
                                else{
                                    new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error : "+response.code())
                                            .setContentText(ARC.getPhrase(response.code()))
                                            .show();
                                }
                            }
                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Something Went Wrong")
                                        .setContentText("Check Your Internet Connection")
                                        .show();
                            }
                        });
                    }
                })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<DefaultResponse>() {
                            @Override
                            public void call(DefaultResponse defaultResponse) {
                                new SweetAlertDialog(CandidateAuth.this,SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Authentication Successful")
                                        .setContentText("Click OK to view KYC")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent intent=new Intent(getApplicationContext(),CandidateKyc.class);
                                                intent.putExtra("enrollment",enroll);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }
                        });

                new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Candidate Verified!!")
                        .setContentText("You are being redirected to Candidate Detail Screen")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent=new Intent(getApplicationContext(),CandidateKyc.class);
                                intent.putExtra("enrollment",enroll);
                                startActivity(intent);
                            }
                        })
                        .show();
            }else{
                new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Biometric Authentication Failed")
                        .setContentText("Retry or Report Impersonation")
                        .setConfirmText("Retry")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .setCancelText("Report Impersonation")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                new SweetAlertDialog(CandidateAuth.this,SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Are you Sure?")
                                        .setContentText("Student cant be verified again if you click yes.")
                                        .setConfirmText("Yes")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                reportImpersonation();
                                            }
                                        })
                                        .setCancelText("Retry")
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        }).show();
                            }
                        })
                        .show();
            }

        }
    }


    public void reportImpersonation(){
        rx.Observable.create(new rx.Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(final Subscriber<? super DefaultResponse> subscriber) {
                HttpRequest.ExamApiInterface examInterface = new HttpRequest(PrefUtils.getAccessToken(getApplicationContext()),"")
                        .retrofit.create(HttpRequest.ExamApiInterface.class);
                Call<DefaultResponse> responseCall = examInterface.reportImpersonation(
                                PrefUtils.getInvigilatorId(getApplicationContext()),
                                enroll
                );
                responseCall.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        if(response.code()==200){
                            subscriber.onNext(response.body());
                        }
                        else{
                            new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error : "+response.code())
                                    .setContentText(ARC.getPhrase(response.code()))
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        new SweetAlertDialog(CandidateAuth.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Something Went Wrong")
                                .setContentText("Check Your Internet Connection")
                                .show();
                    }
                });
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DefaultResponse>() {
                    @Override
                    public void call(DefaultResponse defaultResponse) {
                        new SweetAlertDialog(CandidateAuth.this,SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Impersonation Successfully \nreported!")
                                .setContentText("Redirecting to Home!!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent intent=new Intent(getApplicationContext(),CandidateLogin.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                });

    }


    @Override
    public void onBackPressed() {}

    public void logOut(){
            new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
                .setContentText("Current Candidate is not Authenticated.\nAre you sure you want to logout?")
                .setConfirmText("Yes, LogOut")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        PrefUtils.logout(getApplicationContext());
                        startActivity(new Intent(getApplicationContext(),InvigilatorLogin.class));
                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                }).show();
    }

    public void instruct(){
        AlertDialog.Builder terms = new AlertDialog.Builder(this);
        terms.setTitle("Instructions for Invigilators");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv1 = new TextView(this);
        tv1.setTextSize(17.0f);
        tv1.setTextColor(Color.DKGRAY);
        tv1.setText("\n\u25A0 This app is for use of Invigilator only" +
                "\n\n\u25A0 Unauthorized usage may result in legal action" +
                "\n\n\u25A0 Invigilator must report Impersonation case strictly after 3 failed attempts" +
                "\n\n\u25A0 In case of damaged hall ticket, enter Enrollment number manually");
        linearLayout.addView(tv1);
        terms.setView(linearLayout);
        terms.setPositiveButton("I Understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        terms.create().show();

    }

    public void goBack(){
        new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
                .setContentText("Current Candidate is not Authenticated.\nAre you sure you want to go to Home Screen?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(getApplicationContext(),CandidateLogin.class));
                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                }).show();
    }

    public void listClicked(){
        new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
                .setContentText("Current Candidate is not Authenticated.\nAre you sure you want to see Candidate List?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(getApplicationContext(),CandidateList.class));
                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                }).show();
    }


}
