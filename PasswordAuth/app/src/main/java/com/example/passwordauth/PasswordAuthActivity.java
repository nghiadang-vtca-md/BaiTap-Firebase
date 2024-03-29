package com.example.passwordauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class PasswordAuthActivity extends AppCompatActivity {

    private TextView userText;
    private TextView statusText;
    private EditText emailText;
    private EditText passwordText;

    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_auth);

        userText = findViewById(R.id.userText);
        statusText = findViewById(R.id.statusText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        userText.setText("");
        statusText.setText("Signed Out");

        fbAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    userText.setText(user.getEmail());
                    statusText.setText("Signed In");
                }else {
                    userText.setText("");
                    statusText.setText("Signed Out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null){
            fbAuth.removeAuthStateListener(authListener);
        }
    }

    private void notifyUser(String message){
        Toast.makeText(PasswordAuthActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void createAccount(View view){
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(email.length() == 0){
            emailText.setError("Enter an email address");
            return;
        }
        if(password.length() < 6){
            passwordText.setError("Password must be at least 6 characters");
            return;
        }

        fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            notifyUser("Account creation failed");
                        }
                    }
                });
    }

    public void signIn(View view){
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(email.length() == 0){
            emailText.setError("Enter an email address");
            return;
        }
        if(password.length() < 6){
            passwordText.setError("Password must be at least 6 characters");
            return;
        }

        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            //notifyUser("Authentication failed");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                            notifyUser("Invalid password");
                        } else if(e instanceof FirebaseAuthInvalidUserException){
                            String errorCode = ((FirebaseAuthInvalidUserException)e).getErrorCode();
                            if(errorCode.equals("ERROR_USER_NOT_FOUND")){
                                notifyUser("No matching account found");
                            }else if(errorCode.equals("ERROR_USER_DISABLED")){
                                notifyUser("User account has been disabled");
                            }
                        } else {
                            notifyUser(e.getLocalizedMessage());
                        }
                    }
                });
    }

    public void signOut(View view){
        fbAuth.signOut();
    }

    public void resetPassword(View view){
        String email = emailText.getText().toString();

        if(email.length() == 0){
            emailText.setError("Enter an email address");
            return;
        }

        fbAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this,new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            notifyUser("Reset email sent");
                        }
                    }
                });
    }
}
