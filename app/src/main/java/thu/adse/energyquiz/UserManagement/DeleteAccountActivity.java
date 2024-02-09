package thu.adse.energyquiz.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

// Activity to delete the account of the logged in user
public class DeleteAccountActivity extends AppCompatActivity {

    private CardView cardViewConfirmDeleteAcc_button, cardViewDeleteAccountBack;
    private String userID;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_delete_account);

        cardViewConfirmDeleteAcc_button = findViewById(R.id.cardViewConfirmDeleteAcc_button);
        cardViewDeleteAccountBack = findViewById(R.id.cardViewDeleteAccountBack);

        cardViewConfirmDeleteAcc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                // Delete the user specific DB entries
                userID = FirebaseAuth.getInstance().getUid();
                userDatabaseReference.child(userID).removeValue();
                // Delete the firebase user itself
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DeleteAccountActivity.this, getString(R.string.deleteAccountSuccessfull), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteAccountActivity.this, HomeScreenActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
                                }
                                else {
                                    Toast.makeText(DeleteAccountActivity.this, getString(R.string.deleteAccountFailed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        cardViewDeleteAccountBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeleteAccountActivity.this, SettingsActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
            }
        });
    }
}