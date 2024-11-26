package com.example.practicefinalproject.AdminDashboard;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes/";
    private static Retrofit retrofit;

    private RetrofitClient() {}

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to Java objects
                    .build();
        }
        return retrofit;
    }

    public static GoogleBooksApi getGoogleBooksApiService() {
        return getRetrofitInstance().create(GoogleBooksApi.class);
    }
}
