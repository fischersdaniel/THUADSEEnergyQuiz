package thu.adse.energyquiz.UserManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import thu.adse.energyquiz.HomeScreenActivity;
import thu.adse.energyquiz.R;
import thu.adse.energyquiz.SinglePlayer.SinglePlayerStartActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button logoutButton, deleteAccButton, changePasswordMain_button, startSingleplayer_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout_button);
        deleteAccButton = findViewById(R.id.deleteAcc_button);
        changePasswordMain_button = findViewById(R.id.changePasswordMain_button);
        startSingleplayer_button = findViewById(R.id.startSingleplayer_button);




        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Toast.makeText(MainActivity.this, getString(R.string.logOutSuccessfull), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, HomeScreenActivity.class ));
                finish();
            }
        });

        deleteAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DeleteAccountActivity.class));
            }
        });

        changePasswordMain_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
            }
        });
        startSingleplayer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SinglePlayerStartActivity.class));
            }
        });
    }
}