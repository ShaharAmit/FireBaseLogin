package com.example.shahar.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button sign_in;
    private Button sign_out;
    private Button create_account;
    private Button verify_email;

    FirebaseHelper FirebaseHelperInstance;
    FirebaseUser currentUser;

    float x1,x2,y1,y2,leftx,rightx,screenHeight,screenWidth,deltaY;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FirebaseHelperInstance = FirebaseHelper.getInstance();
        mAuth = FirebaseHelperInstance.getmAuth();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        leftx = screenWidth/3;
        rightx = leftx*2;

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        // Buttons
        sign_in = (Button)findViewById(R.id.email_sign_in_button);
        sign_out = (Button)findViewById(R.id.sign_out_button);
        verify_email = (Button)findViewById(R.id.verify_email_button);
        create_account = (Button)findViewById(R.id.email_create_account_button);

        verify_email.setOnClickListener(this);
        create_account.setOnClickListener(this);
        sign_out.setOnClickListener(this);
        sign_in.setOnClickListener(this);

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = FirebaseHelperInstance.getCurrentUser();
        updateUI();
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        currentUser = FirebaseHelperInstance.createAccount(email,password);
        if(currentUser==null) {
            Toast.makeText(MainActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
        updateUI();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        currentUser = FirebaseHelperInstance.signIn(email,password);
        if(currentUser == null){
            Toast.makeText(MainActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(R.string.auth_failed);

        }
        updateUI();
    }

    private void signOut() {
        currentUser = FirebaseHelperInstance.signOutmAuth();
        updateUI();
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        if (currentUser != null) {
            currentUser.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + currentUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    });
        }
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI() {
        if (currentUser != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    currentUser.getEmail(), currentUser.isEmailVerified()));

            sign_out.setVisibility(View.VISIBLE);
            sign_in.setVisibility(View.GONE);
            verify_email.setVisibility(View.VISIBLE);
            create_account.setVisibility(View.GONE);

            findViewById(R.id.verify_email_button).setEnabled(!currentUser.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);

            sign_out.setVisibility(View.GONE);
            sign_in.setVisibility(View.VISIBLE);
            verify_email.setVisibility(View.GONE);
            create_account.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                deltaY = y2 - y1;
                if (deltaY > screenHeight/2.5 && x1 > leftx && x1 < rightx && x2 > leftx && x2 < rightx)
                {
                    currentUser = FirebaseHelperInstance.UpdateCurrentUser();
                    if (currentUser != null)
                        updateUI();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
