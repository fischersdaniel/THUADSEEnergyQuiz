package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText newPassword_text, confirmNewPassword_text;
    private Button changePassword_button;
    private TextView loginRedirectChangePasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        newPassword_text = findViewById(R.id.newPassword_text);
        confirmNewPassword_text = findViewById(R.id.confirmNewPassword_text);
        changePassword_button = findViewById(R.id.changePassword_button);
        loginRedirectChangePasswordText = findViewById(R.id.loginRedirectChangePasswordText);

        changePassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = newPassword_text.getText().toString();
                String confirmNewPassword = confirmNewPassword_text.getText().toString();

                if(!confirmNewPassword.equals(newPassword)) {
                    newPassword_text.setError("Password and confirmation must be the same");
                    confirmNewPassword_text.setError("Password and confirmation must be the same");
                }
                else if (newPassword.isEmpty()){
                    newPassword_text.setError("Password cannot be empty");
                }
                else if (confirmNewPassword.isEmpty()) {
                    confirmNewPassword_text.setError("Password cannot be empty");
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.passwordChangeSuccessfull), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ChangePasswordActivity.this, getString(R.string.passwordChangeFailed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        loginRedirectChangePasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
            }
        });

    }
}