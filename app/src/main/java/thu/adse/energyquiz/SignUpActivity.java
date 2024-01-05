package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupUserName, signupEmail, signupPassword, signupConfirmPassword;
    private Button signUp_button;
    private TextView loginRedirectSignUp_textview;
    private String userID;
    private List<Integer> usedSessionIDsInit = new ArrayList<>();
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize the GUI
        signupUserName = findViewById(R.id.signup_userName);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById((R.id.signup_confirmpassword));
        signUp_button = findViewById(R.id.signup_button);
        loginRedirectSignUp_textview = findViewById(R.id.loginRedirectSignUp_textview);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");
        if (currentUser != null) {
            userID = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
            Log.d("current User", "succesfully getting userID:" + userID);
        } else
        {
            // No user is logged in, sign up is needed
            // no_operation
            Log.d("current User", "Bitte Anmelden");
        }

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = signupUserName.getText().toString().trim();
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String confirmPass = signupConfirmPassword.getText().toString().trim();

                if(!confirmPass.equals(pass)) {
                    signupPassword.setError("Password and confirmation must be the same");
                    signupConfirmPassword.setError("Password and confirmation must be the same");
                }
                else if (userName.isEmpty()){
                    signupUserName.setError("Username cannot be empty");
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
                    mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("current User", "Create user in firebase authenticator erfolgreich");
                                // with the sign up a user specific DB entry is created to save some attributes like the score etc., init with zeros
                                userID = FirebaseAuth.getInstance().getUid();
                                // Set the DB reference
                                usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                // create a new child the DB part users
                                usersDatabaseReference.child(userID).push();
                                // create a list with a non relevant item for the usedSessionIDs
                                // questionID = 0 does not exist
                                usedSessionIDsInit.add(0);
                                // Write into the DB
                                usersDatabaseReference.child(userID).child("userName").setValue(userName);
                                usersDatabaseReference.child(userID).child("remainLogIn").setValue(false);
                                usersDatabaseReference.child(userID).child("usedSessionIDs").setValue(usedSessionIDsInit);
                                usersDatabaseReference.child(userID).child("score").setValue(0);
                                usersDatabaseReference.child(userID).child("rank").setValue("Anfänger");
                                usersDatabaseReference.child(userID).child("totalAnswers").setValue(0);
                                usersDatabaseReference.child(userID).child("totalCorrectAnswers").setValue(0);

                                Log.d("current User", "Create user in database erfolgreich");
                                Toast.makeText(SignUpActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, HomeScreenActivity.class));
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