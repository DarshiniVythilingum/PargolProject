package com.example.practicefinalproj;

//default
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


//new imports
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.practicefinalproj.AdminDashboard.AdminDashboard;
import com.example.practicefinalproj.ReaderDashboard.ReaderDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ACLogin extends AppCompatActivity {

    //1) Declaration of my 5 UI components and 2 firebase
    EditText eTemail, eTpassword;
    Button btnLogIn, btnRegister;
    FirebaseAuth mauth;
    FirebaseFirestore db;


    /*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if ( user != null){
            // fetch the account
            // check the role
            // redirect based on the role.
        }
    }
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aclogin);

        //2) Initialisation of my 7 components
        eTemail = findViewById(R.id.eTemail);
        eTpassword = findViewById(R.id.eTpassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogIn = findViewById(R.id.btnLogIn);

        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //3) Event 1 : Register button redirecting to Register Page
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACLogin.this, AdminCustRegis.class);
                startActivity(intent);
                finish();
            }
        });

        //4) Event 2: Admin button will validate log in credentials of an admin using the func login() and role admin
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

//Only Method validating logIn credentials
    private void login() {
        String email = eTemail.getText().toString().trim();
        String password = eTpassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            eTemail.setError("Email is required!");
            eTemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eTemail.setError("Please enter a valid email address!");
            eTemail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            eTpassword.setError("Password is required!");
            eTpassword.requestFocus();
            return;
        }

        mauth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(ACLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mauth.getCurrentUser();
                            if (currentUser != null) {
                                String currentUserUid = currentUser.getUid();

                                db.collection("AdminAlbum").document(currentUserUid)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                                    Intent intent = new Intent(ACLogin.this, AdminDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    db.collection("ReaderAlbum").document(currentUserUid)
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                                                        Intent intent = new Intent(ACLogin.this, ReaderDashboard.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    } else {
                                                                        Toast.makeText(ACLogin.this, "Error: User type not recognized.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed!";
                            Toast.makeText(ACLogin.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(ACLogin.this, e -> {
                    Toast.makeText(ACLogin.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }


}
/*

*/