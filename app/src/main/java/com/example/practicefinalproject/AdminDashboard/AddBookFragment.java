package com.example.practicefinalproject.AdminDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.practicefinalproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookFragment extends Fragment {

    private EditText eTBookTitle, eTBookDescription, eTBookAuthor;
    private Button btnAdd;
    private ImageView imgBook;
    private Uri imageUri;
    private ProgressBar progressBar;

    private DatabaseReference booksRef;
    private StorageReference storageRef;
    private ActivityResultLauncher<String> pickImageLauncher;

    private static final String apiKey = "AIzaSyDs9J6ito0TyiR2Lpv-EWqQDSaz_FrtY3g";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_fragment, container, false);

        eTBookTitle = view.findViewById(R.id.eTBookTitle);
        eTBookDescription = view.findViewById(R.id.eTBookDescription);
        eTBookAuthor = view.findViewById(R.id.eTBookAuthor);
        btnAdd = view.findViewById(R.id.btnAdd);
        imgBook = view.findViewById(R.id.imgBook);
        progressBar = view.findViewById(R.id.progressBar);

        booksRef = FirebaseDatabase.getInstance().getReference("Books");
        storageRef = FirebaseStorage.getInstance().getReference("BookCovers");

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            imgBook.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e("ImageError", "Failed to load image", e);
                        }
                    }
                }
        );


        imgBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openImageFolder();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addNewBook();
            }
        });
        return view;
    }

    private void openImageFolder() {
        pickImageLauncher.launch("image/*");
    }


    private void addNewBook() {
        String title = eTBookTitle.getText().toString().trim();
        String description = eTBookDescription.getText().toString().trim();
        String author = eTBookAuthor.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(author)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        checkBookExists(title, author);

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String bookId = booksRef.push().getKey();

                    HashMap<String, Object> bookData = new HashMap<>();
                    bookData.put("bookId", bookId);
                    bookData.put("title", title);
                    bookData.put("description", description);
                    bookData.put("author", author);
                    bookData.put("imageUrl", imageUrl);

                    booksRef.child(bookId).setValue(bookData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Book saved successfully!", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save book. Please try again.", Toast.LENGTH_SHORT).show();
                            });progressBar.setVisibility(View.GONE);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void checkBookExists(String title, String author) {
        booksRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Book with the same title exists
                    Toast.makeText(getContext(), "Book with this title already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // No duplicate book found, proceed to upload the new book
                    uploadBookData(title, author);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to check for duplicates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadBookData(String title, String author) {
        progressBar.setVisibility(View.VISIBLE);

        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String bookId = booksRef.push().getKey();
                    String description = eTBookDescription.getText().toString();

                    HashMap<String, Object> bookData = new HashMap<>();
                    bookData.put("bookId", bookId);
                    bookData.put("title", title);
                    bookData.put("description", description);
                    bookData.put("author", author);
                    bookData.put("imageUrl", imageUrl);

                    booksRef.child(bookId).setValue(bookData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Book saved successfully!", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save book. Please try again.", Toast.LENGTH_SHORT).show();
                            });

                    progressBar.setVisibility(View.GONE);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void checkGoogleBooksApi(String title, String author) {
        // Get the Google Books API service from Retrofit
        GoogleBooksApi apiService = RetrofitClient.getGoogleBooksApiService();

        // Create the query by combining title and author
        String query = title + " " + author;

        // Make sure you pass the API key here (ensure apiKey is defined somewhere in your Fragment)
        String apiKey = "YOUR_GOOGLE_API_KEY";  // Replace with your actual API key

        // Call the searchBooks method with the query and API key
        Call<GoogleResponse> call = apiService.searchBooksForGoogleResponse(query, apiKey);

        // Enqueue the API call
        call.enqueue(new Callback<GoogleResponse>() {
            @Override
            public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the list of books from the response
                    List<GoogleResponse.GoogleBook> googleBooks = response.body().getItems();

                    // Check if there are any books in the response
                    if (googleBooks != null && !googleBooks.isEmpty()) {
                        // Book found
                        Toast.makeText(getContext(), "Book found in Google Books", Toast.LENGTH_SHORT).show();
                    } else {
                        // No books found, upload the book data
                        uploadBookData(title, author);
                    }
                } else {
                    // Response was unsuccessful or no body found
                    Toast.makeText(getContext(), "Failed to fetch data from Google Books", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GoogleResponse> call, Throwable t) {
                // Request failed (e.g., no network)
                Toast.makeText(getContext(), "Google Books API request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clearFields() {
                eTBookTitle.setText("");
                eTBookDescription.setText("");
                eTBookAuthor.setText("");
                imgBook.setImageResource(R.drawable.uploadpic);
                imageUri = null;
            }


}
