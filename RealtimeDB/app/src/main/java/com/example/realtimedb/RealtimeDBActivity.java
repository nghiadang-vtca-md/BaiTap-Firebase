package com.example.realtimedb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDBActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_db);

        auth = FirebaseAuth.getInstance();

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

        startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                startActivity(new Intent(this,SignedInActivity.class));
                return;
            }
        }
        else {
            if(response == null){
                // User cancelled Sign-in
                return;
            }

            if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                // Device has no network connection
                return;
            }

            if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                // Unknown error occurred
                return;
            }
        }
    }
}
