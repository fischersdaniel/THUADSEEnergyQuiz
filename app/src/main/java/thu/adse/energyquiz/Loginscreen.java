package thu.adse.energyquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Loginscreen extends AppCompatActivity {

    //All variables are declared here

    EditText editText;
    Button button;
    String playerName = "";
    FirebaseDatabase database;
    DatabaseReference playerRef;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        database = FirebaseDatabase.getInstance();
            //Check if player exists and gets reference
            SharedPreferences preferences =getSharedPreferences("PREFS", 0);
            playerName = preferences.getString("playername", "");
            if(!playerName.equals("")){
                playerRef=database.getReference("players/" + playerName);
                addEventListener();
                playerRef.setValue("");
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //logging the player in
                    playerName = editText.getText().toString();
                    editText.setText("");
                    if(!playerName.equals("")){
                        button.setText("Logging In");
                        button.setEnabled(false);
                        playerRef=database.getReference("players/" + playerName);
                        addEventListener();
                        playerRef.setValue("");
                    }
                }
            });
    }
    private void addEventListener(){
            //read from the database
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //sucess = continue to the next screen after saving the player name
                if(!playerName.equals("")){
                    SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), Lobbyscreen.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                button.setText("Log In");
                button.setEnabled(false);
                Toast.makeText(Loginscreen.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


