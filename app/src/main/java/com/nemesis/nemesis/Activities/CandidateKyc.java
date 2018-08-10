package com.nemesis.nemesis.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemesis.nemesis.ARC;
import com.nemesis.nemesis.ActivityIdentifiers;
import com.nemesis.nemesis.Fragments.BottomFragment;
import com.nemesis.nemesis.Fragments.TopFragment;
import com.nemesis.nemesis.Http.HttpRequest;
import com.nemesis.nemesis.Pojos.CandidateDetails;
import com.nemesis.nemesis.Prefs.PrefUtils;
import com.nemesis.nemesis.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CandidateKyc extends AppCompatActivity {

    @BindView(R.id.profile) ImageView profile;
    @BindView(R.id.attempts) TextView attempts;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.rollno) TextView rollno;
    @BindView(R.id.center) TextView center;
    @BindView(R.id.dob) TextView dob;
    @BindView(R.id.name) TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_kyc);
        ButterKnife.bind(this);
        ActivityIdentifiers.setCurrentScreen(getApplicationContext(),ActivityIdentifiers.CANDIDATE_KYC_SCREEN);

        getSupportFragmentManager().beginTransaction().add(R.id.topFrame,new TopFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.bottomframe,new BottomFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();

        Intent intent=getIntent();
        String rollno=intent.getStringExtra("enrollment");
        viewKyc(rollno);


    }

    public void viewKyc(final String enroll){
        rx.Observable.create(new rx.Observable.OnSubscribe<CandidateDetails>() {
            @Override
            public void call(final Subscriber<? super CandidateDetails> subscriber) {
                HttpRequest.ExamApiInterface examInterface = new HttpRequest(PrefUtils.getAccessToken(getApplicationContext()),"")
                        .retrofit.create(HttpRequest.ExamApiInterface.class);
                Call<CandidateDetails> responseCall = examInterface.getCandidatesDetails(
                                PrefUtils.getInvigilatorId(getApplicationContext()),
                                enroll
                );
                responseCall.enqueue(new Callback<CandidateDetails>() {
                    @Override
                    public void onResponse(Call<CandidateDetails> call, Response<CandidateDetails> response) {
                        if(response.code()==200){
                            subscriber.onNext(response.body());
                        }
                        else{
                            new SweetAlertDialog(CandidateKyc.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error : "+response.code())
                                    .setContentText(ARC.getPhrase(response.code()))
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CandidateDetails> call, Throwable t) {
                        new SweetAlertDialog(CandidateKyc.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Something Went Wrong")
                                .setContentText("Check Your Internet Connection")
                                .show();
                    }
                });
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CandidateDetails>() {
                    @Override
                    public void call(CandidateDetails candidateDetails) {
                        Picasso.with(getApplicationContext()).load("http://13.232.71.170/"+candidateDetails.getProfile()).noFade().into(profile);
                        rollno.setText("Enrollment No : "+candidateDetails.getEnrollment());
                        center.setText("Center : "+PrefUtils.getInvigilatorCenter(getApplicationContext()));
                        name.setText(candidateDetails.getName());
                        status.setText("Status : "+candidateDetails.getCstatus());
                        attempts.setText("Attempts : "+candidateDetails.getAttempts());
                        dob.setText("DOB : "+candidateDetails.getDob());
                    }
                });

    }

    public void goBack(){
        startActivity(new Intent(getApplicationContext(),CandidateLogin.class));
    }

    @Override
    public void onBackPressed() {}

    public void listClicked(){
        startActivity(new Intent(getApplicationContext(),CandidateList.class));
    }


    public void logOut(){
        new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are You Sure?")
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
}
