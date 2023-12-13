package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupConfirmPassword;
    private Button signUp_button;
    private TextView loginRedirectSignUp_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize the FirebaseAuth instance in the onCreate()
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById((R.id.signup_confirmpassword));
        signUp_button = findViewById(R.id.signup_button);
        loginRedirectSignUp_textview = findViewById(R.id.loginRedirectSignUp_textview);

        //Instance of class LoginActivity for getter of remainLoggedInFlag
        LoginActivity logAct = new LoginActivity();

        //When while opening the app a user is still logged in, there is no need to log in again
        //remain logged in had to be chosen in the login screen
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is logged in
            // Could be checked in the SplashScreen
            if(logAct.isRemainLoggedInFlag()){
                // User stays logged in
                Toast.makeText(SignUpActivity.this, "User is logged in.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
            else{
                auth.signOut();
                  /*  .then(function(){
//loggt stand jetzt immer aus, da VAriable nicht persistent: Lösung: in DB abspeichern
                    }
                    .catch(function(error) {

            });*/
            }
        }
        else{
            // No user is loggen in, sign up is neede
            // no_operation
        }

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String confirmPass = signupConfirmPassword.getText().toString().trim();

                if(!confirmPass.equals(pass)) {
                    signupPassword.setError("Password and confirmation must be the same");
                    signupConfirmPassword.setError("Password and confirmation must be the same");
                }
                else if (user.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                else if(pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                }
                else if(confirmPass.isEmpty()) {
                    signupPassword.setError("Confirmation password cannot be empty");
                } else{
                    auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            } else{
                                Toast.makeText(SignUpActivity.this, "Signup Failed" + " " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginRedirectSignUp_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}