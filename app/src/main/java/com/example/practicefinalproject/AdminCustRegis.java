package com.example.practicefinalproject;

//default imports
import androidx.annotation.NonNull; //@NonNull inside firebase for registration.
import androidx.appcompat.app.AppCompatActivity; // AppCompatActivity in public class AdminCustRegis extends AppCompatActivity

//new imports
import android.app.DatePickerDialog; // DatePickerDialog is a class in android.app package.
import android.text.TextUtils;//TextUtils returns true if the given string is null or has a length of 0.
import android.util.Patterns; //Patterns.EMAIL_ADDRESS.matcher(email).matches()
import android.view.View; //parent type when adding click listeners (view -> { }).
import android.content.Intent; //Intent
import android.os.Bundle; //Container to store sth to transfer to another activity + works with Intent always.

//These 4 are responsible for the UI components
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

//I need the 2 following lines as my classes are in different packages/folder.
import com.example.practicefinalproject.AdminDashboard.AdminDashboard;
import com.example.practicefinalproject.ReaderDashboard.ReaderDashboard;

import com.google.android.gms.tasks.OnCompleteListener; //Handling a Task<AuthResult> returned by Firebase authentication.
import com.google.android.gms.tasks.Task; //Checking task.isSuccessful() after an authentication attempt.
import com.google.firebase.auth.AuthResult;//Used in onComplete to retrieve the authenticated FirebaseUser.
import com.google.firebase.auth.FirebaseAuth; //FirebaseAuth.getInstance() initializes Firebase Authentication.
import com.google.firebase.auth.FirebaseUser; //FirebaseUser user = FirebaseAuth.getCurrentUser();.
import com.google.firebase.firestore.FieldValue; //FieldValue.serverTimestamp() adds a server-generated timestamp to a document.
import com.google.firebase.firestore.FirebaseFirestore; //FirebaseFirestore db = FirebaseFirestore.getInstance();.

import java.util.Calendar; //Calendar.getInstance()
import java.util.HashMap;//Map<String, Object> userData = new HashMap<>();.
import java.util.Map;//Map<String, Object> to store Firestore-compatible data.

public class AdminCustRegis extends AppCompatActivity {

    //-> 1) Declaration of UI components
    EditText eTfname, eTlname, eTemail, eTpassword, eTconfirmPassword, eTdob, eTphone;

    Button btnRegister, btnAdmin, btnReader, btnlogin;
    LinearLayout llAdmin, llReader;

    EditText eTadminName, etadminEmail, eTadminPassword, eTadminCode, eTadminAnswer;

    FirebaseAuth mauth;
    FirebaseFirestore db;
//  FirebaseAuth is a class type (from the Firebase SDK) that handles authentication operations. mauth -> my + authentification
//  FirebaseFirestore is a class type from the Firebase SDK that provides access to the Firestore database service. [Storing registered data]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cust_regis);

        //-> 2)Initialisation of UI components using findViewById(R.id.x);

        //7 input fields for reader: fname, lname, email, pswd, confirmpswd, dob, phone number
        eTfname = findViewById(R.id.eTfname);
        eTlname = findViewById(R.id.eTlname);
        eTemail = findViewById(R.id.eTemail);
        eTpassword = findViewById(R.id.eTpassword);
        eTconfirmPassword = findViewById(R.id.eTconfirmPassword);
        eTdob = findViewById(R.id.eTdob);
        eTphone = findViewById(R.id.eTphone);

        //4 buttons: Admin, Reader, Register and Login
        btnRegister = findViewById(R.id.btnRegister);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnReader = findViewById(R.id.btnReader);
        btnlogin = findViewById(R.id.btnLogin);

        //2 linear layout: reader and admin
        llReader = findViewById(R.id.llReader);
        llAdmin = findViewById(R.id.llAdmin);

        //5 input fields for admin: name, email, pswd, code, answer
        eTadminName = findViewById(R.id.eTadminName);
        etadminEmail = findViewById(R.id.etadminEmail);
        eTadminPassword = findViewById(R.id.eTadminPassword);
        eTadminCode = findViewById(R.id.eTadminCode);
        eTadminAnswer = findViewById(R.id.eTadminAnswer);

        //Firebase Auth instance + Firestore instance
        mauth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //.getInstance() -> Method stating to give me the global FirebaseAuth object configured for my app.

        // ->  3) Event 1: If a user clicks on Admin btn, the admin LL will display and reader LL will disappear using the function adminOrReaderLayout..
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminOrReaderLayout("admin");
            }
        });

        // ->  4) Event 2: If a user clicks on Reader btn, the reader LL will display and admin LL will disappear using the function adminOrReaderLayout. [By default]
        btnReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminOrReaderLayout("reader");
            }
        });

        // -> 5) Event 3: When a reader clicks on EditText dob, a DatePickerDialog / Calendar pops up and one can choose.
        eTdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminCustRegis.this,
                        (view, year1, month1, dayOfMonth) -> eTdob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                        year, month, day);
                datePickerDialog.show();
            }
        });

        // -> 6) Event 4: Register Button, if admin LL was filled. then we execute adminRegister();. If successfully, move to admin Dashboard.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llAdmin.getVisibility() == View.VISIBLE) {
                    adminRegister();
                } else if (llReader.getVisibility() == View.VISIBLE) {
                    readerRegister();
                }
            }
        });

        // -> 7) Event 5: Login Button, if a reader/admin already registered, move to log in to verify credentials.
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCustRegis.this, ACLogin.class);
                startActivity(intent);
            }
        });
    } //end of onCreate method
//===================================================================================================
//Section of Functions aiding the main operations

    //Method 1 aiding 3) Event 1 and 4) Event 2 for visibility purpose displaying the appropriate LL.
    private void adminOrReaderLayout(String user) {
        if (user.equals("admin")) {
            llAdmin.setVisibility(View.VISIBLE);
            llReader.setVisibility(View.GONE);
        } else {
            llReader.setVisibility(View.VISIBLE);
            llAdmin.setVisibility(View.GONE);
        }
    }

    //Method 2 aiding 6) Event 4 Register btn once admin side has been filled.
    private void adminRegister() {
        String adminName = eTadminName.getText().toString().trim();
        String adminEmail = etadminEmail.getText().toString().trim();
        String adminPassword = eTadminPassword.getText().toString().trim();
        String codeAccess = eTadminCode.getText().toString().trim();
        String securityAnswer = eTadminAnswer.getText().toString().trim();

        //Cdt 1: No empty fields
        if (TextUtils.isEmpty(adminName) || TextUtils.isEmpty(adminEmail) || TextUtils.isEmpty(adminPassword) ||
                TextUtils.isEmpty(codeAccess) || TextUtils.isEmpty(securityAnswer)) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Cdt 2: Code answer being 3210123 matches the input provided by new admin during registration and BookHeaven is the security answer. Both needs to work!
        if (!codeAccess.equals("3210123") || !securityAnswer.equals("BookHeaven")) {
            Toast.makeText(this, "Invalid code or security answer for Admin", Toast.LENGTH_SHORT).show();
            return;
        }

        //Firebase section explained downwards:
        mauth.createUserWithEmailAndPassword(adminEmail, adminPassword)
                .addOnCompleteListener(AdminCustRegis.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUserAdmin = mauth.getCurrentUser();
                            if (firebaseUserAdmin != null) {
                                String adminId = firebaseUserAdmin.getUid();

                                Map<String, Object> adminData = new HashMap<>();
                                adminData.put("adminName", adminName);
                                adminData.put("email", adminEmail);
                                adminData.put("role", "admin");
                                adminData.put("timestamp", FieldValue.serverTimestamp());

                                db.collection("AdminAlbum").document(adminId)
                                        .set(adminData)
                                        .addOnCompleteListener(AdminCustRegis.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminCustRegis.this, "Admin registered successfully!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(AdminCustRegis.this, AdminDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(AdminCustRegis.this, "Error saving admin data.!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed!";
                            Toast.makeText(AdminCustRegis.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(AdminCustRegis.this, e -> {
                    Toast.makeText(AdminCustRegis.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //Method 2 aiding 6) Event 4 Register btn once reader LL has been filled.
    private void readerRegister() {
        String fname = eTfname.getText().toString().trim();
        String lname = eTlname.getText().toString().trim();
        String email = eTemail.getText().toString().trim();
        String password = eTpassword.getText().toString().trim();
        String confirmPassword = eTconfirmPassword.getText().toString().trim();
        String dob = eTdob.getText().toString().trim();
        String phone = eTphone.getText().toString().trim();

        //Cdt 1: No empty fields
        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(dob) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill all fields to register!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Cdt 2: Valid email address format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eTemail.setError("Invalid Email format");
            eTemail.requestFocus(); //method commonly used in form validation to bring attention to the field that needs correction.
            return;//The return prevents the user from submitting invalid data.
        }

        //Cdt 3: Password should be 8 or more in chars. Standard number of chars today.
        if (password.length() < 8) {
            eTpassword.setError("Password must be at least 8 characters long!");
            eTpassword.requestFocus();
            return;
        }

        //Cdt 4: pswd = confirmpswd
        if (!password.equals(confirmPassword)) {
            eTconfirmPassword.setError("Passwords do not match!");
            eTconfirmPassword.requestFocus();
            return;
        }


        if (!phone.matches("^[2-9]\\d{2}-\\d{3}-\\d{4}$")) {
//514-513-8844 <- [first digit lies btw 2-9, followed by any 2 digits from 0-9: area code part, like 514 is Montreal area code] - [any 3 digits: exchange code] - [any 4 digits: unique code of the user.]
            eTphone.setError("Invalid Phone Number format. Use XXX-XXX-XXXX");
            eTphone.requestFocus();
            return;
        }

        //Firebase Section
        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminCustRegis.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUserReader = mauth.getCurrentUser();
                            if (firebaseUserReader != null) {
                                String readerId = firebaseUserReader.getUid();

                                Map<String, Object> readerData = new HashMap<>();
                                readerData.put("fname", fname);
                                readerData.put("lname", lname);
                                readerData.put("email", email);
                                readerData.put("dob", dob);
                                readerData.put("phone", phone);
                                readerData.put("timestamp", FieldValue.serverTimestamp());

                                db.collection("ReaderAlbum").document(readerId)
                                        .set(readerData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminCustRegis.this, "Reader registered successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(AdminCustRegis.this, ReaderDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(AdminCustRegis.this, "Error saving reader data.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(AdminCustRegis.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(AdminCustRegis.this, e -> {
                    Toast.makeText(AdminCustRegis.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}




/*
A)
Explanation of connecting real-time db as demonstrated in class:

Tools in TopBar starting with Apple logo nested btw Run and VCS.
Tools -> FireBase -> Assistant -> Realtime Database -> Get started with Realtime DB
-> 1) Click on the btn 'Connect to FireBase'
Login using my email -> Accept cdts, don't enable analytics, name project, connect, save
A bunch of stuff and agreement and pop-up to say ok to add SDk.
Now, I have a green 'tick connected' label instead of the button 'Connect to FireBase'
2) Add the Realtime DB SDK to your app
Gradle or nth running means good to go.
*/
//================================================================================================================================================================//
/*
B)
FireBase Education:

FireBase is a database which offers multiple tools and services.
Here, we are using FirebaseAuth and Firebase Firestone.

Firebase Authentication (FirebaseAuth):
Manages user authentication (sign-up, sign-in, password recovery, etc.).
After authenticating a user, you typically get a unique user identifier (UUID) from FirebaseAuth.

Firebase Firestore (FirebaseFirestore):
Data Storage: Once the user is authenticated, you can use their UID as a document ID in Firestore to store related user data.
Collections and Documents: Firestore organizes data in a hierarchy:
A collection is a group of related data (e.g., users or admins).
A document is a single record in a collection (e.g., the user profile of a specific user).
Later, you can query Firestore using the UID to get the user's details or role.

Example Structure in Firestore
Collection: users

Document ID (UID)	Name	        Email	            Role
aBc123XYZ	        Alice Smith	    alice@example.com	Admin
dEf456PQR	        Bob Jones	    bob@example.com	    Reader

FirebaseAuth: Validates the user, generates a UID, and handles login/logout sessions.
Firestore: Stores user details (like name, email, role) using the UID in collections and documents.

----------------------------------------------------------------------

Part 1:
1)mauth.createUserWithEmailAndPassword(adminEmail, adminPassword)
//object.existingMethod(param1, param2) <- adminEmail and adminPassword are eT variables that hold values that I want to use from admin LL that I declared above and call here.
//Once created, an UUID is generated and the db will check the email and pswd by using signInWithEmailAndPassword() method in the login.
                2).addOnCompleteListener(AdminCustRegis.this, new OnCompleteListener<AuthResult>() {
//This event is essential as mauth.createUserWithEmailAndPassword(adminEmail, adminPassword) runs asynchronously meaning that when this line is executed, the program continues to run the rest of the code without validating.
//So, we add an event to validate whether success or failure. If success, we proceed to add the UUID, move to respective dashboard while failure will generate a toast letting the user know that the registration of the email and password was not registered.
//event(currentClass.this, interface<ab>) , there are 2 parameters:
//1) (AdminCustRegis). This tells Firebase where to handle the event (e.g., updating the UI in the current activity).
//2) Interface: new OnCompleteListener<AuthResult>() is an implementation of the OnCompleteListener interface, which defines how you want to handle the success or failure of the task.
//ab: Represents the task's outcome (AuthResult)encapsulated in a Task<AuthResult> object.
//The AuthResult object contains authentication-related details, such as whether the operation succeeded and details about the authenticated user.
    Whether the task was successful (task.isSuccessful()).
    If successful, task.getResult() will give you the AuthResult.
    If it failed, task.getException() will provide details of the error.
                   3) @Override
//Indicates that the onComplete method is overriding the onComplete method of the OnCompleteListener interface.
                    4) public void onComplete(@NonNull Task<AuthResult> task) {
//This method is called when the asynchronous task initiated by createUserWithEmailAndPassword completes.
    @NonNull:Ensures the task parameter cannot be null.
    Task<AuthResult>: Holds success or failure information along with additional details like exceptions (if any).
                        5) if (task.isSuccessful()) {
//If true, Firebase has successfully created the user in its Authentication system, and the user is automatically signed in.
//If false, there was an issue during the process (e.g., invalid email, weak password, or network error).
                            6) FirebaseUser firebaseUser = mauth.getCurrentUser();
//Fetches the currently authenticated user from Firebase using the FirebaseAuth instance (mauth).
//getCurrentUser() returns an instance of the FirebaseUser class representing the signed-in user.
//After a successful registration, Firebase automatically signs the user in.
//By retrieving this user object, you can access details like their UID, email, and other properties.
                            7) if (firebaseUser != null) {
//If the user object is valid, it proceeds to extract the unique identifier (UID)
                                8) String userId = firebaseUser.getUid();
//Retrieves the unique identifier (UID) assigned to the user by Firebase Authentication.

Summary of Part 1:
I have mauth, which is an instance of the FirebaseAuth class.
The .createUserWithEmailAndPassword() method is an existing method in this class that takes two parameters—adminEmail and adminPassword, which have been declared and initialized earlier.
Since this method runs asynchronously, it doesn’t immediately confirm whether the user registration succeeded or failed.
Instead, I attach .addOnCompleteListener, which acts as an event listener to handle the result of this operation.
The event triggers the onComplete method, where we check the success or failure of the createUserWithEmailAndPassword task.
The Task<AuthResult> object holds the result. The @NonNull annotation ensures that the task object cannot be null.
If the task is successful, firebaseUser, an instance of the FirebaseUser class, retrieves the current authenticated user using mauth.getCurrentUser().
If the returned firebaseUser object is not null, I use the getUid() method to fetch the unique identifier (UID) assigned by Firebase Authentication.
--------------------
Part 2:
                                Map<String, Object> adminData = new HashMap<>();
                                adminData.put("adminName", adminName);
                                adminData.put("email", adminEmail);
                                adminData.put("role", "admin");
                                adminData.put("timestamp", FieldValue.serverTimestamp());

1) Map<String, Object> adminData = new HashMap<>();
    Map<String, Object>:
        This declares a variable named adminData of type Map which is an interface . A Map is a collection that associates keys with values.
        The key type is String (e.g., "adminName"), and the value type is Object, allowing you to store various types of data (e.g., strings, numbers, timestamps).
    new HashMap<>();:
        This creates a concrete instance of the HashMap class, which is a specific implementation of the Map interface.
        new HashMap<>() allocates memory and initializes an empty hash table for the map.
        Map<String, Object> adminData only defines the variable and its type.
        Without the new HashMap<>(), the map is not initialized, and any operation on it would throw a NullPointerException.
        A HashMap stores key-value pairs and ensures that keys are unique.
        HashMap is a class in Java that implements the Map interface.
        HashMap provides a data structure that uses a hash table to store and retrieve key-value pairs.

2)Adds a key-value pair to the map:
The key is the string "adminName", representing the field name.
The value is the variable adminName, which holds the administrator's name.

3)The key is "email", representing the email field.
The value is adminEmail, the administrator's email address.

4)The key is "role".
The value is the string "admin", indicating the user’s role as an administrator.

5) Adds a special key-value pair for the timestamp:
The key is "timestamp".
The value is FieldValue.serverTimestamp(), which is a Firestore-specific method.
It generates a timestamp based on the server's current time, ensuring consistency across all users regardless of their device's local time settings.

This adminData map likely prepares data to be sent to Firebase Firestore. You can use this map when calling Firestore methods like set() or add() to store the administrator's information in a document.

What’s Happening in Your Code
You are using a Map object (implemented by HashMap) to store user information.
Here’s a detailed explanation:
Data Structure:
    The HashMap class uses a hash table internally as its data structure. This means it organizes key-value pairs using a hashing mechanism, ensuring efficient storage and retrieval.
Key-Value Pair:
    On the left (key): You have identifiers like "adminName", "email", etc., which are String values. These serve as keys to uniquely identify the data.
    On the right (value): You have the actual data like "Alice", "alice@example.com", etc., which can be any type (String, Integer, Date, etc.) since the value is defined as an Object.
Purpose:
    The Map acts as a container to hold the user's information as key-value pairs. Each key provides a unique way to access the corresponding value.

Visualization of the Map Object
Key	        Value
adminName	Alice
email	    alice@example.com
role	    admin
timestamp	Timestamp object

Summary:
adminData is the name of a Map object instantiated as a HashMap to store key-value pairs, which uses a hash table as its underlying data structure.
Keys are String such "adminName" , "email " <- column 1
Values are Object such as adminName, adminEmail which will be replace by real value. <- column 2
using a Map (implemented by HashMap) to store user information.
The key uniquely identifies each piece of data, and the value holds the actual user information.
The underlying data structure is a hash table, ensuring quick lookups and efficient storage. 😊
--------------------
Part 3:
                                1) db.collection("users").document(userId)
                                        .set(adminData)
//db.collection("users"): Refers to the Firestore collection named "users". This is where data related to users is being stored in Firestore.
//document(userId): Refers to a specific document within the "users" collection. The userId (likely a unique identifier like UID) acts as the document's key.
//set(adminData): Uploads the adminData (a Map of key-value pairs) to the specified document. If the document doesn't exist, Firestore creates it.
                                        .addOnCompleteListener(AdminCustRegis.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//.addOnCompleteListener: Attaches a listener to handle what happens once the operation (writing to the database) is complete.
//AdminCustRegis.this: Context of the current activity (AdminCustRegis) is passed to the listener to ensure the callback runs in the correct context.
//new OnCompleteListener<Void>(): Implements the OnCompleteListener interface for handling the task completion. Void indicates that the task doesn't return any meaningful data.
//onComplete: Callback that triggers after the database operation finishes (success or failure).
//@NonNull Task<Void>: The Task represents the asynchronous operation. Void means no return value from the task.
//Void is used when the task does not return any specific data, just a success or failure status.
//Here, the Task<Void> only indicates whether the operation (writing data to Firestore) succeeded or failed, but does not provide additional data.
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AdminCustRegis.this, "Admin registered successfully!", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(AdminCustRegis.this, AdminDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(AdminCustRegis.this, "Failed to register admin!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
//errorMessage stores the exception. If it is not null,we display that exception else we display "Registration failed!"
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed!";
                            Toast.makeText(AdminCustRegis.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
 //The addOnFailureListener is specifically designed to catch any exceptions (e) during the execution process.
 //It displays the exception message (e.getMessage()) as a toast.
 //Normally, network issues or server-side errors unrelated to the task itself.
                .addOnFailureListener(AdminCustRegis.this, e -> {
                    Toast.makeText(AdminCustRegis.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

*/

//================================================================================================================================================================//
/*
C)
DatePickerDialog
Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminCustRegis.this,
                        (view, year1, month1, dayOfMonth) -> eTdob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1),
                        year, month, day);
                datePickerDialog.show();

Calendar.getInstance() creates a Calendar object that is initialized with the current date and time.
calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), and calendar.get(Calendar.DAY_OF_MONTH) fetch the current year, month, and day from the system’s calendar.
Note: The month in Calendar is zero-based (i.e., January is 0, February is 1, etc.), so you have to add 1 to the month when displaying it to the user or for other purposes.

new DatePickerDialog() creates a new instance of the DatePickerDialog. It requires several parameters:
AdminCustRegis.this: This is the context (activity) in which the DatePickerDialog will be displayed. It's needed because the dialog must be tied to the current activity to show up properly.
Lambda expression (view, year1, month1, dayOfMonth) -> ...: This is a listener that is triggered when the user selects a date. It is implemented using a lambda expression (which is equivalent to implementing DatePickerDialog.OnDateSetListener). The parameters here represent:
    view: The view that triggered the event (not used in this case).
    year1, month1, dayOfMonth: These are the year, month (0-based), and day selected by the user.
eTdob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1): Once the user selects a date, the lambda expression sets the date in the EditText field (eTdob) in the format day/month/year.
    Notice that (month1 + 1) is used to correct the 0-based month index.
year, month, day: These are the default values for the date displayed in the dialog (i.e., the current date).

Summary: 3 parameters:
When you call new DatePickerDialog(...), the constructor takes these parameters:
The first parameter is the context (AdminCustRegis.this <- activityInWhichDatepickerdialogWillBeDisplayed.this).
The second parameter is the listener, which processes the date selected by the user.
The third, fourth, and fifth parameters (i.e., year, month, day) are the initial values that the dialog will show. These are used to set the starting date when the dialog is opened.

datePickerDialog.show(); displays the DatePickerDialog so user can select a date.

Summary of the Flow:
When the EditText (eTdob) is clicked, the DatePickerDialog is shown to the user (datePickerDialog.show()).
After the user selects a date, the lambda expression runs and uses the selected values (year1, month1, dayOfMonth) to format the date and set it into the EditText (eTdob).
The formatted date is displayed on the screen in the desired format (day/month/year).

Example Flow:
If the current date is November 19, 2024, then:
year = 2024
month = 10 (November is the 11th month, but it's 0-based, so it’s 10)
day = 19
These values (year, month, day) are used to set the initial date when the DatePickerDialog is opened.
Recap of Parameters:
The 2nd parameter ((view, year1, month1, dayOfMonth) -> eTdob.setText(...)): This is the lambda that gets triggered when the user selects a date. It handles the date the user selects and updates the UI.
The 3rd, 4th, and 5th parameters (year, month, day): These are the initial date values that are displayed in the DatePickerDialog when it is first shown. They don't change after the dialog is shown; they are just the starting point.
So in summary, the 3rd parameter (year, month, day) is just setting the initial date in the dialog, while the 2nd parameter (the lambda) handles what happens when the user selects a date.

year1, month1, and dayOfMonth — hold the date values the user picked and after the user selects a date, the selected date is formatted and displayed in eTdob, which is your EditText field.
*/
//================================================================================================================================================================//