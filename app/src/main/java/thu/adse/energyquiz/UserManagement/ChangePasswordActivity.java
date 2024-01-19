package thu.adse.energyquiz.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

import thu.adse.energyquiz.R;

// Activity to change the password of the logged in user
public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText newPassword_text, confirmNewPassword_text;
    private CardView cardViewChangePWButton, cardViewChangePWBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_v2);

        auth = FirebaseAuth.getInstance();
        newPassword_text = findViewById(R.id.newPassword_text);
        confirmNewPassword_text = findViewById(R.id.confirmNewPassword_text);
        cardViewChangePWButton = findViewById(R.id.cardViewChangePWButton);
        cardViewChangePWBack = findViewById(R.id.cardViewChangePWBack);

        // Close the Keyboard by touching anywhere on the screen expect the EditText-fields
        findViewById(android.R.id.content).setFocusableInTouchMode(true);

        cardViewChangePWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = newPassword_text.getText().toString();
                String confirmNewPassword = confirmNewPassword_text.getText().toString();

                // Checks if the inputs are legit
                if(!confirmNewPassword.equals(newPassword)) {
                    newPassword_text.setError(getString(R.string.passwordsNotEqual));
                    confirmNewPassword_text.setError(getString(R.string.passwordsNotEqual));
                }
                else if (newPassword.isEmpty()){
                    newPassword_text.setError(getString(R.string.passwordEmpty));
                }
                else if (confirmNewPassword.isEmpty()) {
                    confirmNewPassword_text.setError(getString(R.string.confirmationPasswordEmpty));
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

        cardViewChangePWBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
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