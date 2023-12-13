package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountActivity extends AppCompatActivity {

    private Button confirmDeleteAcc_button;
    private TextView cancelDeleteAcc_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        confirmDeleteAcc_button = findViewById(R.id.confirmDeleteAcc_button);
        cancelDeleteAcc_textview = findViewById(R.id.cancelDeleteAcc_textview);

        confirmDeleteAcc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DeleteAccountActivity.this, "User account deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteAccountActivity.this, SignUpActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(DeleteAccountActivity.this, "User account deleting failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        cancelDeleteAcc_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteAccountActivity.this, MainActivity.class));
            }
        });

    }
}