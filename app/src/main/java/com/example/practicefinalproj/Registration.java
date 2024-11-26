package com.example.practicefinalproj;

//default 6 imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//new imports
import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import java.util.Calendar;

public class Registration extends AppCompatActivity {

    EditText eTfname, eTlname,eTemail, eTpassword, eTconfirmPassword, eTdob, eTphone;
    Button btnRegister;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        eTfname = findViewById(R.id.eTfname);
        eTlname = findViewById(R.id.eTlname);
        eTemail = findViewById(R.id.eTemail);
        eTpassword = findViewById(R.id.eTpassword);
        eTconfirmPassword = findViewById(R.id.eTconfirmPassword);
        eTdob = findViewById(R.id.eTdob);
        eTphone = findViewById(R.id.eTphone);
        btnRegister = findViewById(R.id.btnRegister);

        mauth = FirebaseAuth.getInstance();

        eTdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this,
                        (view, year1, month1, dayOfMonth) -> eTdob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                        year, month, day);
                datePickerDialog.show();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register(){
        String fname = eTfname.getText().toString().trim();
        String lname = eTlname.getText().toString().trim();
        String email = eTemail.getText().toString().trim();
        String password = eTpassword.getText().toString().trim();
        String confirmPassword = eTconfirmPassword.getText().toString().trim();
        String dob = eTdob.getText().toString().trim();
        String phone = eTphone.getText().toString().trim();

        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(dob) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill all fields to register!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eTemail.setError("Invalid Email format");
            eTemail.requestFocus();
            return;
        }

        if (password.length() < 8) {
            eTpassword.setError("Password must be at least 8 characters long!");
            eTpassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            eTconfirmPassword.setError("Passwords do not match!");
            eTconfirmPassword.requestFocus();
            return;
        }

        if (!phone.matches("^[2-9]\\d{2}-\\d{3}-\\d{4}$")) {
            eTphone.setError("Invalid Phone Number format. Use XXX-XXX-XXXX");
            eTphone.requestFocus();
            return;
        }


        mauth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registration.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
//                        Toast.makeText(Registration.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(Registration.this, "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mauth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

/*
Tools in TopBar starting with Apple logo nested btw Run and VCS.
Tools -> FireBase -> Assistant -> Realtime Database -> Get started with Realtime DB
-> 1) Click on the btn 'Connect to FireBase'
Login using my email -> Accept cdts, don't enable analytics, name project, connect, save
A bunch of stuff and agreement and pop-up to say ok to add SDk.
Now, I have a green 'tick connected' label instead of the button 'Connect to FireBase'
2) Add the Realtime DB SDK to your app
Gradle or nth running means good to go.
* */