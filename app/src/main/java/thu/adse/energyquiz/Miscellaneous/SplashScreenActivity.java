package thu.adse.energyquiz.Miscellaneous;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import thu.adse.energyquiz.R;


public class SplashScreenActivity extends AppCompatActivity {
    private String userID;
    private boolean remainLogInLocal;
    private List<Integer> usedSessionIDsInit = new ArrayList<>();
    private DatabaseReference usersDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //When while opening the app a user is still logged in, there is no need to log in again
        //remain logged in had to be chosen in the login screen
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");

        if (currentUser != null) {
            userID = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
            Log.d("current User", "succesfully getting userID:" + userID);

            usersDatabaseReference = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(userID);
            usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("current User", "datasnapshot exists");
                        remainLogInLocal = dataSnapshot.child("remainLogIn").getValue(boolean.class);
                        if(true == remainLogInLocal){
                            Log.d("current User", "remainLogIn true");
                            // User stays logged in
                            // delete the used session IDs in the user DB, IDs are only from app start to app kill needed
                            // if an overall check of used IDs is requested, the following 2 lines code be excluded / deleted
                            usedSessionIDsInit.add(0);
                            usersDatabaseReference.child("usedSessionIDs").setValue(usedSessionIDsInit);
                            Toast.makeText(SplashScreenActivity.this, getString(R.string.userIsLoggedIn), Toast.LENGTH_SHORT).show();
                           // startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        }
                        else{
                            Log.d("current User", "remainLogIn false");
                            // User gets logged out
                            mAuth.signOut();
                            Log.d("current User", getString(R.string.userIsLoggedOut));
                            Toast.makeText(SplashScreenActivity.this, getString(R.string.userIsLoggedOut), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Log.d("current User", "datasnapshot does not exist");
                        //not found
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
                }
            });
        } else {
            // No user is logged in, sign up is needed
            // no_operation
            Log.d("current User", "Bitte Anmelden");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}