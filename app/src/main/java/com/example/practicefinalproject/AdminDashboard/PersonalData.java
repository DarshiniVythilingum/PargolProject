package com.example.practicefinalproject.AdminDashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicefinalproject.ACLogin;
import com.example.practicefinalproject.AdminCustRegis;
import com.example.practicefinalproject.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class PersonalData extends Fragment {

    //1) Declaration
    EditText eTAdminName, eTAdminEmail, eTAdminPassword;
    Button btnUpdate;
    FirebaseAuth mauth;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_data, container, false);

        // 2) Initialisation
        eTAdminName = view.findViewById(R.id.eTAdminName);
        eTAdminEmail = view.findViewById(R.id.eTAdminEmail);
        eTAdminPassword = view.findViewById(R.id.eTAdminPassword);

        btnUpdate = view.findViewById(R.id.btnUpdate);

        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadAdminData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = eTAdminName.getText().toString().trim();
                String email = eTAdminEmail.getText().toString().trim();
                String password = eTAdminPassword.getText().toString().trim();

                if (!name.isEmpty()) {
                    updateAdminName(name);
                }
                if (!email.isEmpty()) {
                    updateAdminEmail(email);
                }
                if (!password.isEmpty()) {
                    updateAdminPassword(password);
                }
                if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(getContext(), "No changes detected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    //================================================================================================================================
    private void loadAdminData() {
        if (currentUser != null) {
            String adminId = currentUser.getUid();
            DocumentReference adminDocRef = db.collection("AdminAlbum").document(adminId);

            adminDocRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String currentName = documentSnapshot.getString("adminName");
                            String currentEmail = documentSnapshot.getString("email");

                            if (currentName != null) {
                                eTAdminName.setText(currentName);
                            }
                            if (currentEmail != null) {
                                eTAdminEmail.setText(currentEmail);
                            }
                        } else {
                            Toast.makeText(getContext(), "Admin data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error loading admin data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateAdminName(String newName) {
        DocumentReference adminDocRef = db.collection("AdminAlbum").document(currentUser.getUid());

        adminDocRef.update("adminName", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Name updated successfully!", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof AdminDashboard) {
                        ((AdminDashboard) getActivity()).loadAdminName(); // Refresh top bar
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void updateAdminEmail(String newEmail) {
        String password = eTAdminPassword.getText().toString().trim();

        if (newEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Both email and password are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
        currentUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    currentUser.updateEmail(newEmail)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Email updated successfully!", Toast.LENGTH_SHORT).show();
                                // Now, update Firestore with the new email
                                updateFirestoreField("email", newEmail);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error updating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAdminPassword(String newPassword) {
        currentUser.updatePassword(newPassword)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void updateFirestoreField(String field, String value) {
        DocumentReference adminDocRef = db.collection("AdminAlbum").document(currentUser.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);

        adminDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), field + " updated in Firestore!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }













}


