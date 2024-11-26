package com.example.practicefinalproj.AdminDashboard;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

//Right-click -> new -> Java -> interface
public interface GoogleBooksApi {

    @GET("books/v1/volumes")
    Call<GoogleResponse> searchBooksForGoogleResponse(@Query("q") String query, @Query("key") String apiKey);

    @GET("books/v1/volumes")
    Call<List<Book>> searchBooksForList(@Query("q") String query, @Query("key") String apiKey);

    @GET("volumes")
    Call<GoogleResponse> getBooks(@Query("q") String query);

    @GET("books/{id}")
    Call<Book> getBookById(@Path("id") String id);

    @POST("books")
    Call<Book> addBook(@Body Book book);

    @PUT("books/{id}")
    Call<Book> updateBook(@Path("id") String id, @Body Book book);

    @DELETE("books/{id}")
    Call<Void> deleteBook(@Path("id") String id);
}



