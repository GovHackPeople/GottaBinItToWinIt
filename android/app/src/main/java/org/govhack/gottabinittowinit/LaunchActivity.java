package org.govhack.gottabinittowinit;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LaunchActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LaunchActivity";
    private static final int RC_SIGN_IN = 101;
    private static final int LAUNCH_DELAY = 2500;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Setup Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // Delay for short period to show branding
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    // Launch MainActivity if already signed in
                    launchMainActivity();
                } else {
                    // User not signed in, setup Google sign in button
                    setupGoogleAuth();
                    showGoogleSignInButton();
                }
            }
        }, LAUNCH_DELAY);
    }

    private void launchMainActivity() {
        Intent i = new Intent(LaunchActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void showGoogleSignInButton() {
        View googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setVisibility(View.VISIBLE);
        googleSignInButton.setOnClickListener(this);
        googleSignInButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google auth successful, pass to Firebase auth
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                launchMainActivity();
            } else {
                // Google login failed
                Toast.makeText(this, "Unable to sign in to Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LaunchActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Error occurred when connecting to Google
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Unable to connect at this time.", Toast.LENGTH_SHORT).show();
    }


}
