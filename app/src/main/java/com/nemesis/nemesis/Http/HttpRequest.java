package com.nemesis.nemesis.Http;

import com.nemesis.nemesis.Pojos.MyCandidates;
import com.nemesis.nemesis.Pojos.CandidateDetails;
import com.nemesis.nemesis.Pojos.CandidateInfo;
import com.nemesis.nemesis.Pojos.DefaultResponse;
import com.nemesis.nemesis.Pojos.InvigilatorDetails;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by aditya on 4/1/17.
 */

public class HttpRequest {
    public static String API_URL="http://13.232.71.170/api/";
    public static String X_ACCESS_TOKEN;
    public static String AUTH_TOKEN;

    public HttpRequest(String token,String auth_token){
        X_ACCESS_TOKEN = token;
        AUTH_TOKEN = auth_token;
    }

    public static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("user-agent","android")
                    .addHeader("user-agent-secret","nemesis-dev01-dbtandroid")
                    .addHeader("x-access-token",X_ACCESS_TOKEN)
                    .addHeader("auth-token",AUTH_TOKEN);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }).build();

    public static Retrofit retrofit= new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

    public interface ExamApiInterface{
        @POST("slogin")
        @FormUrlEncoded
        Call<CandidateInfo> getCandidateInfo(
                @Field("id") String id,
                @Field("enrollment") String enrollment
        );

        @POST("ilogin")
        @FormUrlEncoded
        Call<InvigilatorDetails> getInvigilatorDetails(
               @Field("id") String id
        );

        @POST("sdetails")
        @FormUrlEncoded
        Call<CandidateDetails> getCandidatesDetails(
                @Field("id") String id,
                @Field("enrollment") String enrollment
        );

        @POST("bio")
        @FormUrlEncoded
        Call<DefaultResponse> bioAttempt(
                @Field("id") String id,
                @Field("enrollment") String enrollment
        );

        @POST("impersonation")
        @FormUrlEncoded
        Call<DefaultResponse> reportImpersonation(
                @Field("id") String id,
                @Field("enrollment") String enrollment
        );

        @POST("mycandidates")
        @FormUrlEncoded
        Call<MyCandidates> getAllCandidates(
                @Field("id") String id
        );

        @POST("auth")
        @FormUrlEncoded
        Call<DefaultResponse> authSuccess(
                @Field("id") String id,
                @Field("enrollment") String enrollment
        );
    }
}
