package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
// Docs
// https://firebase.google.com/docs/cloud-messaging/android/first-message
public class MessagingActivity extends AppCompatActivity {
    // Token
    // fdypdG3NxLg:APA91bFq4ApoedhC13U7-dncYlx4xGp9MxhDvm19b7Roert738ZTsfYEsGoVDSjIGPRBD3W0U0fQGziUhowqGTcvIrDTBqyEvHb5TEuZUydzPBUiEHFPxjQp2XIbpyraEdpoOuNnP3vz
    private static final String TAG = "MessagingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MessagingActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
