package thu.adse.energyquiz.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupUserName, signupEmail, signupPassword, signupConfirmPassword;
//    private Button signUp_button;
    private CardView cardViewSignUpButton, cardViewSignUpBack;
    private TextView loginRedirectSignUp_textview;
    private String userID;
    private List<Integer> usedSessionIDsInit = new ArrayList<>();
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_v2);

        // Initialize the GUI
        signupUserName = findViewById(R.id.signup_userName);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById((R.id.signup_confirmpassword));
//        signUp_button = findViewById(R.id.signup_button);
        cardViewSignUpButton = findViewById(R.id.cardViewSignUpButton);
        loginRedirectSignUp_textview = findViewById(R.id.loginRedirectSignUp_textview);
        cardViewSignUpBack = findViewById(R.id.cardViewSignUpBack);

        findViewById(android.R.id.content).setFocusableInTouchMode(true);

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

        cardViewSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = signupUserName.getText().toString().trim();
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String confirmPass = signupConfirmPassword.getText().toString().trim();

                if(!confirmPass.equals(pass)) {
                    signupPassword.setError(getString(R.string.passwordsNotEqual));
                    signupConfirmPassword.setError(getString(R.string.passwordsNotEqual));
                }
                else if (userName.isEmpty()){
                    signupUserName.setError(getString(R.string.userNameEmpty));
                }
                else if (user.isEmpty()){
                    signupEmail.setError(getString(R.string.emailEmpty));
                }
                else if(pass.isEmpty()) {
                    signupPassword.setError(getString(R.string.passwordEmpty));
                }
                else if(confirmPass.isEmpty()) {
                    signupPassword.setError(getString(R.string.confirmationPasswordEmpty));
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
                                Toast.makeText(SignUpActivity.this, getString(R.string.signUpSuccessfull), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, HomeScreenActivity.class));
                            } else{
                                Toast.makeText(SignUpActivity.this, getString(R.string.signUpFailed) + " " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        cardViewSignUpBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {

        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            findViewById(android.R.id.content).clearFocus();
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }
}