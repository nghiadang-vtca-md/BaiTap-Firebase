package com.example.firebaseauth2v;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAuthActivity extends AppCompatActivity {
    // OpenSSL
    // Debug key
    // DwActexIFG3cb/XmFoRTeHcYMjc=
    // Release
    // STL2jB3xSQXNka+PVpm/3Fu11v8=

    private FirebaseAuth auth;
    private static final int REQUEST_CODE = 101;
    //private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, SignedInActivity.class));
            finish();
        } else {

            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.default_web_client_id))
                    //.requestServerAuthCode(getResources().getString((R.string.default_web_client_id)))
                    .requestEmail()
                    .build();
            //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            authenticateUser();
        }
    }

    private void authenticateUser() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setLogo(R.drawable.logo_firebase)
                        .setAvailableProviders(getProviderList())
                        .setIsSmartLockEnabled(false)
                        .build(),
                REQUEST_CODE);
    }

    private List<AuthUI.IdpConfig> getProviderList() {

        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(
                new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(
                new AuthUI.IdpConfig.GoogleBuilder().setSignInOptions(gso).build());
        //providers.add(
                //new AuthUI.IdpConfig.FacebookBuilder().build());
        providers.add(
                new AuthUI.IdpConfig.PhoneBuilder().build());
        return providers;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, SignedInActivity.class));
                return;
            }
        }else {
            if (response == null) {
                // User cancelled Sign-in
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                // Device has no network connection
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                // Unknown error occurred
                return;
            }
        }
    }
}
