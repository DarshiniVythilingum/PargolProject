package com.example.practicefinalproject.AdminDashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicefinalproject.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>  {
    private List<Book> bookList;
    private BookClickListener listener;

    public BookAdapter(List<Book> bookList, BookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView title, author, description;

        public BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eTBookTitle);
            author = itemView.findViewById(R.id.eTBookAuthor);
            description = itemView.findViewById(R.id.eTBookDescription);
        }
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_edit_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);
        holder.title.setText(currentBook.getTitle());
        holder.author.setText(currentBook.getAuthor());
        holder.description.setText(currentBook.getDescription());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public interface BookClickListener {
        void onBookClick(Book book);
    }
}
