package thu.adse.energyquiz.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
//import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import thu.adse.energyquiz.R;

// Activity to reset the password via email of the logged out user
public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText sendNewPassword_email;
    private CardView cardViewSendPWButton, cardViewForgotPWBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        auth = FirebaseAuth.getInstance();
        sendNewPassword_email = findViewById(R.id.sendNewPassword_email);
        cardViewSendPWButton = findViewById(R.id.cardViewSendPWButton);
        cardViewForgotPWBack = findViewById(R.id.cardViewForgotPWBack);

        // Close the Keyboard by touching anywhere on the screen expect the EditText-fields
        findViewById(android.R.id.content).setFocusableInTouchMode(true);

        cardViewSendPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = sendNewPassword_email.getText().toString();

                // Checks if the inputs are legit
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.resetEmailSuccessfull), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
                                    }
                                    else {
                                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.resetEmailFailed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else if (email.isEmpty()) {
                    sendNewPassword_email.setError(getString(R.string.emailEmpty));
                } else {
                    sendNewPassword_email.setError(getString(R.string.emailNotValid));
                }
            }
        });

        cardViewForgotPWBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
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