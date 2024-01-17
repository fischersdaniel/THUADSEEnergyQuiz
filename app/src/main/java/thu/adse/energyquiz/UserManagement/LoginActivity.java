package thu.adse.energyquiz.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Switch remainLoggedInSwitch;
    private TextView signUpRedirectText, forgotPasswordRedirectText;
//    private Button loginButton;
    private CardView cardViewLoginCardLoginButton, cardViewLoginBack;
    private String userID;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
//        loginButton = findViewById(R.id.login_button);
        cardViewLoginCardLoginButton = findViewById(R.id.cardViewLoginCardLoginButton);
        cardViewLoginBack = findViewById(R.id.cardViewLoginBack);
        forgotPasswordRedirectText = findViewById(R.id.forgotPasswordRedirectText);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);
        remainLoggedInSwitch = findViewById(R.id.remainLoggedIn_switch);

        findViewById(android.R.id.content).setFocusableInTouchMode(true);

        cardViewLoginBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        cardViewLoginCardLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = mAuth.getCurrentUser();

                                        Log.d("current User", "Test");

                                        if (currentUser != null) {
                                            userID = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
                                            Log.d("current User", "succesfully getting userID:" + userID);
                                        } else {
                                            Log.d("current User", "Bitte Anmelden");
                                        }

                                        // DB pull
                                        // pull of old user values
                                        //String userID = "1"; //muss flex werden
                                        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                                        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (remainLoggedInSwitch.isChecked()){
                                                        usersDatabaseReference.child("remainLogIn").setValue(true);
                                                        //nothing to do, log in will remain automaticly
                                                    }
                                                    else {
                                                        usersDatabaseReference.child("remainLogIn").setValue(false);
                                                        //not so easy to logout while appkill, so just logout by appstart in signupacitivity (because first screen here)
                                                    }
                                                }
                                                else{
                                                    //not found
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
                                            }
                                        });


                                        Toast.makeText(LoginActivity.this, getString(R.string.logInSuccessfull), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class ));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, getString(R.string.logInFailed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError(getString(R.string.passwordEmpty));
                    }

                } else if (email.isEmpty()) {
                    loginEmail.setError(getString(R.string.emailEmpty));
                } else {
                    loginEmail.setError(getString(R.string.emailNotValid));
                }
            }
        });

        forgotPasswordRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        signUpRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
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