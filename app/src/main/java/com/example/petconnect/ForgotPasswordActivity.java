package com.example.petconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petconnect.utils.FirebaseAuthManager;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private Button sendEmailButton;
    private TextView emailSentMessage;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authManager = FirebaseAuthManager.getInstance();

        emailLayout = findViewById(R.id.emailLayout);
        sendEmailButton = findViewById(R.id.sendEmailButton);
        emailSentMessage = findViewById(R.id.emailSentMessage);
        progressBar = findViewById(R.id.progressBar);

        sendEmailButton.setOnClickListener(v -> handleSendEmail());
    }

    private void handleSendEmail() {
        String email = emailLayout.getEditText().getText().toString().trim();

        // Reset error
        emailLayout.setError(null);

        if (email.isEmpty()) {
            emailLayout.setError("Please enter your email");
            return;
        }

        // Show progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        sendEmailButton.setEnabled(false);

        // Send password reset email (Firebase sends a real email automatically)
        authManager.resetPassword(email, this, new FirebaseAuthManager.PasswordResetCallback() {
            @Override
            public void onSuccess() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                // Show success message
                emailSentMessage.setVisibility(View.VISIBLE);
                sendEmailButton.setText("Email sent");
                
                Toast.makeText(ForgotPasswordActivity.this, 
                        "A password reset email has been sent to " + email, 
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMessage) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                sendEmailButton.setEnabled(true);
                
                if (errorMessage.contains("email")) {
                    emailLayout.setError(errorMessage);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


