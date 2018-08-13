package com.nemesis.nemesis.Activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nemesis.nemesis.ARC;
import com.nemesis.nemesis.ActivityIdentifiers;
import com.nemesis.nemesis.Http.HttpRequest;
import com.nemesis.nemesis.Pojos.InvigilatorDetails;
import com.nemesis.nemesis.Prefs.PrefUtils;
import com.nemesis.nemesis.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import static com.nemesis.nemesis.ActivityIdentifiers.BIO_SUCCESS;
import static com.nemesis.nemesis.ActivityIdentifiers.FINGERPRINT_SCAN_CODE;
import static com.nemesis.nemesis.ActivityIdentifiers.UID;

public class InvigilatorLogin extends AppCompatActivity {

    @BindView(R.id.id)
    TextInputEditText id;
    @BindView(R.id.key)
    TextInputEditText key;
    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;
    private InvigilatorDetails loginDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invigilator_login);
        ButterKnife.bind(this);
        ActivityIdentifiers.setCurrentScreen(getApplicationContext(),ActivityIdentifiers.INVIGILATOR_LOGIN_SCREEN);
    }

    @OnClick(R.id.login)
    public void onInvigilatorLogin(){
        rx.Observable.create(new rx.Observable.OnSubscribe<InvigilatorDetails>() {
         @Override
         public void call(final Subscriber<? super InvigilatorDetails> subscriber) {
             HttpRequest.ExamApiInterface examInterface=new HttpRequest("",key.getText().toString())
                     .retrofit.create(HttpRequest.ExamApiInterface.class);
             Call<InvigilatorDetails> responseCall=examInterface.getInvigilatorDetails(
                     id.getText().toString()
             );
             responseCall.enqueue(new Callback<InvigilatorDetails>() {
                 @Override
                 public void onResponse(Call<InvigilatorDetails> call, Response<InvigilatorDetails> response) {
                        if(response.code()==200){
                            subscriber.onNext(response.body());
                        }
                        else{
                        new SweetAlertDialog(InvigilatorLogin.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error : "+response.code())
                                    .setContentText(ARC.getPhrase(response.code()))
                                    .show();
                        }
                 }
                 @Override
                 public void onFailure(Call<InvigilatorDetails> call, Throwable t) {
                     new SweetAlertDialog(InvigilatorLogin.this, SweetAlertDialog.ERROR_TYPE)
                             .setTitleText("Something Went Wrong")
                             .setContentText("Check Your Internet Connection")
                             .show();
                 }
             });
         }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<InvigilatorDetails>() {
                    @Override
                    public void call(InvigilatorDetails invigilatorDetails) {
                        loginDetails=invigilatorDetails;
                        showSuccess(invigilatorDetails);
                    }
                });
    }

    public void showSuccess(final InvigilatorDetails invigilatorDetails){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Welcome "+invigilatorDetails.getFirstname()+" "+invigilatorDetails.getLastname())
                .setContentText("Perform Biometric Authentication to Continue")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        performBiometric(invigilatorDetails.getAadhaar());
                        sweetAlertDialog.cancel();
                        //Use this code if FM220 device not available
                        /*
                        onActivityResult(FINGERPRINT_SCAN_CODE, BIO_SUCCESS, null);
                        sweetAlertDialog.cancel();
                        */

                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .show();
    }

    public void performBiometric(String aadhaar){
        Intent intent=new Intent(getApplicationContext(),ScanningScreen.class);
        intent.putExtra(UID,aadhaar);
        startActivityForResult(intent, FINGERPRINT_SCAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==FINGERPRINT_SCAN_CODE) {
            if(resultCode == BIO_SUCCESS) {
                PrefUtils.login(getApplicationContext(), loginDetails);
                new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Biometric Auth Successful")
                        .setContentText("You are being redirected to Candidate Login.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                startActivity(new Intent(getApplicationContext(), CandidateLogin.class));
                            }
                        })
                        .show();
            }else{
                new SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(data.getStringExtra("AUTH_RESULT"))
                        .setContentText("Try Again or Contact Admin.")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                               sweetAlertDialog.cancel();
                            }
                        })
                        .show();
            }

        }
    }

    @Override
    public void onBackPressed() {}



}
