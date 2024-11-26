package com.example.practicefinalproject.AdminDashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.practicefinalproject.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBookFragment extends Fragment {

    GoogleBooksApi bookApi = RetrofitClient.getRetrofitInstance().create(GoogleBooksApi.class);

    private EditText eTSearch, eTBookTitle, eTBookDescription, eTBookAuthor;
    private Button btnSearch, btnEdit, btnDelete;
    private RecyclerView recyViewEdit;
    private LinearLayout llEditSection;
    private ImageView imgBookCover;
    private BookAdapter bookAdapter;
    private List<Book> bookList = new ArrayList<>();

    private String currentTitle = "";
    private String currentDescription = "";
    private String currentAuthor = "";
    private String currentId = "";
    private static final String apiKey = "AIzaSyDs9J6ito0TyiR2Lpv-EWqQDSaz_FrtY3g";

    public EditBookFragment() {
        // Required empty public constructor
    }

    public static EditBookFragment newInstance(String param1, String param2) {
        EditBookFragment fragment = new EditBookFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        eTSearch = view.findViewById(R.id.eTSearch);
        eTBookTitle = view.findViewById(R.id.eTBookTitle);
        eTBookDescription = view.findViewById(R.id.eTBookDescription);
        eTBookAuthor = view.findViewById(R.id.eTBookAuthor);

        btnSearch = view.findViewById(R.id.btnSearch);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnDelete = view.findViewById(R.id.btnDelete);

        recyViewEdit = view.findViewById(R.id.recyViewEdit);
        llEditSection = view.findViewById(R.id.llEditSection);
        imgBookCover = view.findViewById(R.id.imgBookCover);

        recyViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        setupListeners();

        bookAdapter = new BookAdapter(bookList, new BookAdapter.BookClickListener() {
            @Override
            public void onBookClick(Book book) {
                // Handle book click
                Toast.makeText(getContext(), "Book clicked: " + book.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        recyViewEdit.setAdapter(bookAdapter);
        fetchBooksFromAPI("Harry Potter");


        return view;
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = eTSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    performSearch(query);
                } else {
                    eTSearch.setError("Please enter a title or author");
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eTBookTitle.getText().toString().trim();
                String description = eTBookDescription.getText().toString().trim();
                String author = eTBookAuthor.getText().toString().trim();

                if (validateInputs(title, description, author)) {
                    editBook(title, description, author, currentId);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook();
            }
        });
    }


    private void performSearch(String query) {
        bookApi.searchBooksForList(query, apiKey).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body();
                    BookAdapter adapter = new BookAdapter(books, EditBookFragment.this::onBookSelected);
                    recyViewEdit.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onBookSelected(Book book) {
        currentId = book.getId();
        eTBookTitle.setText(book.getTitle());
        eTBookDescription.setText(book.getDescription());
        eTBookAuthor.setText(book.getAuthor());
    }


    public void editBook(String title, String description, String author, String id) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(getContext(), "Book ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Book updatedBook = new Book(id, title, description, author);
        updatedBook.setTitle(title);
        updatedBook.setDescription(description);
        updatedBook.setAuthor(author);

        bookApi.updateBook(currentId, updatedBook).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    currentTitle = title;
                    currentDescription = description;
                    currentAuthor = author;
                    Toast.makeText(getContext(), "Book updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update book.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validateInputs(String title, String description, String author) {
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && TextUtils.isEmpty(author)) {
            showErrorMessage("Please modify at least one field.");
            return false;
        }
        return true;
    }
    private void showSuccessMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Show error message
    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void deleteBook() {
        bookApi.deleteBook(currentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    currentTitle = "";
                    currentDescription = "";
                    currentAuthor = "";
                    eTBookTitle.setText("");
                    eTBookDescription.setText("");
                    eTBookAuthor.setText("");
                    Toast.makeText(getContext(), "Book deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to delete book.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBooksFromAPI(String query) {
        RetrofitClient.getGoogleBooksApiService().getBooks(query)
                .enqueue(new Callback<GoogleResponse>() {
                    @Override
                    public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Convert GoogleResponse.GoogleBook to your Book model if needed
//                            List<Book> books = convertToBookList(response.body().getItems());
                            bookList.clear(); // Clear the existing list
//                            bookList.addAll(books); // Add new books
                            bookAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch books", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GoogleResponse> call, Throwable t) {
                        // Handle failure
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
