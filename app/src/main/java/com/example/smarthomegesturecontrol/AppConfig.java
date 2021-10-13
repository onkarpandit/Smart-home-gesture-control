package com.example.smarthomegesturecontrol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static String BASE_URL = "http://192.168.0.178:5000/";
    static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}