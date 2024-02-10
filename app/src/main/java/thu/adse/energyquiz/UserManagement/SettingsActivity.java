package thu.adse.energyquiz.UserManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

// D.F. & L.B.: Main Activity of the User Management
public class SettingsActivity extends AppCompatActivity {

    private CardView cardViewSettingsBack, cardViewUserSettingsLogout, cardViewUserSettingsPassword, cardViewUserSettingsDeleteUser;
    private TextView textViewSettingsUserMailDB, textViewSettingsUsernameDB;
    String userEmail, userName, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // L.B.: Link UI elements with corresponding XML IDs
        cardViewSettingsBack = findViewById(R.id.cardViewSettingsBack);
        cardViewUserSettingsLogout = findViewById(R.id.cardViewUserSettingsLogout);
        cardViewUserSettingsPassword = findViewById(R.id.cardViewUserSettingsPassword);
        cardViewUserSettingsDeleteUser = findViewById(R.id.cardViewUserSettingsDeleteUser);

        textViewSettingsUserMailDB = findViewById(R.id.textViewSettingsUserMailDB);
        textViewSettingsUsernameDB = findViewById(R.id.textViewSettingsUsernameDB);

        // L.B.: Check if a user is currently logged in
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            textViewSettingsUserMailDB.setText(userEmail);

            // L.B.: Retrieve username from the Firebase Database
            userId = currentUser.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("userName");

            // L.B.: Display the username from the Firebase Database if exists
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.getValue(String.class);
                        textViewSettingsUsernameDB.setText(userName);
                    } else {
                        // nothing
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Fehler beim Lesen der Datenbank: " + databaseError.getMessage());
                }
            });

        } else {
            // kein User angemeldet
        }

        // D.F. & L.B.: Set up click listeners for the various card views
        cardViewSettingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HomeScreenActivity.class ));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
            }
        });

        cardViewUserSettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Toast.makeText(SettingsActivity.this, getString(R.string.logOutSuccessfull), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, HomeScreenActivity.class ));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
            }
        });

        // L.B.: Checks for button press, changes activities accordingly
        cardViewUserSettingsPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
            }
        });

        // L.B.: Checks for button press, changes activities accordingly
        cardViewUserSettingsDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, DeleteAccountActivity.class));
            }
        });
    }
}