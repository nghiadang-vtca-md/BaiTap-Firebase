package com.example.cloudstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Signed;

public class CloudStorageActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);

        auth =FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(this,SignedInActivity.class));
            finish();
        }
        else {
            authenticateUser();
        }
    }

    private void authenticateUser() {
        List<AuthUI.IdpConfig> providers = new ArrayList<>();

        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false)
                                    .build()
                    , REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                startActivity(new Intent(this, SignedInActivity.class));
                return;
            }
        }else {
            if(response == null){
                // User cancelled Sign-in
                return;
            }
            if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                return;
            }
            if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                return;
            }
        }
    }
}
