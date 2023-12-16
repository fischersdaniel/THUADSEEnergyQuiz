package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText sendNewPassword_email;
    private Button sendNewPassword_button;
    private TextView loginRedirectForgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        sendNewPassword_email = findViewById(R.id.sendNewPassword_email);
        sendNewPassword_button = findViewById(R.id.sendNewPassword_button);
        loginRedirectForgotPasswordText = findViewById(R.id.loginRedirectForgotPasswordText);


        sendNewPassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = sendNewPassword_email.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Password reset mail sended, please look in your mails", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Password reset mail sending failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else if (email.isEmpty()) {
                    sendNewPassword_email.setError("Email cannot be empty");
                } else {
                    sendNewPassword_email.setError("Please enter a valid email");
                }
            }
        });

        loginRedirectForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
        });
    }
}