package com.example.practicefinalproj.AdminDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.practicefinalproj.ACLogin;
import com.example.practicefinalproj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboard extends AppCompatActivity {

    //1) Declaring
    Button btnAdd, btnEdit, btnReader, btnData, btnLogout;
    TextView tVFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

//2)Initialising
         btnAdd = findViewById(R.id.btnAdd);
         btnEdit = findViewById(R.id.btnEdit);
         btnReader = findViewById(R.id.btnReader);
         btnData = findViewById(R.id.btnData);
        btnLogout = findViewById(R.id.btnLogout);

        tVFullName = findViewById(R.id.tVFullName);

//3) Welcome Admin Name at the top Right corner when enters dashboard.
        loadAdminName();

// 4) By default, add fragment shows when register/log in
        loadFragment(new AddBookFragment());

// 5) Event 1 : Add Button to display AddFragment
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddBookFragment());
//I need to pass an object instance that is new AddBookFragment, if only AddBookFragment, it is a class.Java expects an object instance.
            }
        });

//  6) Event 2 : Edit Button to display EditFragment
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new EditBookFragment());
            }
        });

//  7) Event 3 : Reader Button to display ReaderFragment
        btnReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ReadersFragment());
            }
        });

// 8) Event 4 : Data Button to display current info about admin and allows him to change his/her personal data.
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PersonalData());
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboard.this, ACLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void loadAdminName() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String adminId = mAuth.getCurrentUser().getUid();

        DocumentReference adminDocRef = db.collection("AdminAlbum").document(adminId);

        adminDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String adminName = documentSnapshot.getString("adminName");
                        if (adminName != null) {
                            tVFullName.setText("Welcome " + adminName + "!");
                        } else {
                            tVFullName.setText("Welcome!");
                        }
                    } else {
                        Toast.makeText(this, "Admin data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving admin name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

//A modification is to save data current data in 1 Fragment when I switch Fragment.
/*
            private void loadFragment(Fragment fragment) {
//method(class specificInstanceOfAFragment)
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//FragmentTransaction transaction is the object that will allow you to define which fragment operations you want to perform.
    It represents a single operation of fragment changes, such as replacing the current fragment, adding a new one, or removing an existing one.
//getSupportFragmentManager gives access to the FragmentManager, which is responsible for handling fragments within the Activity.
//beginTransaction() method starts a fragment transaction
                transaction.replace(R.id.fragmentContainer, fragment);
//Replace fragmentContainer in my adminDashboard.xml with the desired fragment
                transaction.commit();
//Ensure the replacement with desired fragment occurs.
*/