package com.example.practicefinalproj.AdminDashboard;

import java.util.List;

public class GoogleResponse {

    private int totalItems;  // Total number of books found
    private List<GoogleBook> items;  // List of books returned in the response

    // Getter and setter methods for totalItems and items
    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<GoogleBook> getItems() {
        return items;
    }

    public void setItems(List<GoogleBook> items) {
        this.items = items;
    }

    // Inner class to represent a Google Book
    public static class GoogleBook {

        private String id; // Book ID
        private VolumeInfo volumeInfo; // Detailed book information

        // Getter and setter methods for id and volumeInfo
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public VolumeInfo getVolumeInfo() {
            return volumeInfo;
        }

        public void setVolumeInfo(VolumeInfo volumeInfo) {
            this.volumeInfo = volumeInfo;
        }

        // Nested class to represent detailed volume information (e.g., title, authors)
        public static class VolumeInfo {
            private String title;
            private List<String> authors;

            // Getter and setter methods for title and authors
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<String> getAuthors() {
                return authors;
            }

            public void setAuthors(List<String> authors) {
                this.authors = authors;
            }
        }
    }
}
