package com.nemesis.nemesis.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemesis.nemesis.ActivityIdentifiers;
import com.nemesis.nemesis.Adapters.CandidateListAdapter;
import com.nemesis.nemesis.ApiResponseCodes;
import com.nemesis.nemesis.Fragments.BottomFragment;
import com.nemesis.nemesis.Fragments.TopFragment;
import com.nemesis.nemesis.Http.HttpRequest;
import com.nemesis.nemesis.Pojos.MyCandidates;
import com.nemesis.nemesis.Prefs.PrefUtils;
import com.nemesis.nemesis.R;

import java.util.HashMap;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class CandidateList extends AppCompatActivity {

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.summary)
    TextView summary;
    ApiResponseCodes arc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_list);
        ButterKnife.bind(this);
        ActivityIdentifiers.setCurrentScreen(getApplicationContext(),ActivityIdentifiers.CANDIDATE_LIST_SCREEN);
        arc=new ApiResponseCodes();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportFragmentManager().beginTransaction().add(R.id.topFrame,new TopFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.bottomframe,new BottomFragment()).addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
        getData();
    }

    public void getData(){
        rx.Observable.create(new rx.Observable.OnSubscribe<MyCandidates>() {
            @Override
            public void call(final Subscriber<? super MyCandidates> subscriber) {
                HttpRequest.ExamApiInterface examInterface = HttpRequest.retrofit.create(HttpRequest.ExamApiInterface.class);
                Call<MyCandidates> responseCall = examInterface.getAllCandidates(
                        RequestBody.create(MediaType.parse("text/string"),PrefUtils.getInvigilatorId(getApplicationContext())),
                        RequestBody.create(MediaType.parse("text/string"),PrefUtils.getInvigilatorKey(getApplicationContext()))
                );
                responseCall.enqueue(new Callback<MyCandidates>() {
                    @Override
                    public void onResponse(Call<MyCandidates> call, Response<MyCandidates> response) {
                        if(response.body().getStatuscode()==200){
                            subscriber.onNext(response.body());
                        }
                        else{
                            new SweetAlertDialog(CandidateList.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error : "+response.body().getStatuscode())
                                    .setContentText(arc.getResponsePhrase(response.body().getStatuscode()))
                                    .show();
                        }
                    }
                    @Override
                    public void onFailure(Call<MyCandidates> call, Throwable t) {
                        new SweetAlertDialog(CandidateList.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Something Went Wrong")
                                .setContentText("Check Your Internet Connection")
                                .show();
                    }
                });
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<MyCandidates>() {
                    @Override
                    public void call(MyCandidates myCandidates) {
                        recyclerView.setAdapter(new CandidateListAdapter(getApplicationContext(),myCandidates.getList()));
                        int total=myCandidates.getCount();
                        int unknown=0;int success=0;int failure=0;
                        for(HashMap<String,String> hash:myCandidates.getList()){
                            switch(hash.get("status")){
                                case "SUCCESS":
                                    success++;
                                    break;
                                case "FAILURE":
                                    failure++;
                                    break;
                                case "UNKNOWN":
                                    unknown++;
                                    break;
                            }
                        }
                        summary.setText(
                                "Total Students :"+total+"\n"+
                                "UnAuthenticated :"+unknown+"\n"+
                                "Successfully Authenticated : "+success+"\n"+
                                "Impersonation Detected : "+failure+"\n"
                        );

                    }
                });
    }

    @Override
    public void onBackPressed() {}

    public void goBack(){
        startActivity(new Intent(getApplicationContext(),CandidateLogin.class));
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
