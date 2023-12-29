package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MultiPlayerLobbyScreen extends AppCompatActivity {

    Button buttonCreateLobby, buttonBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_lobby_screen);

        buttonCreateLobby = findViewById(R.id.buttonCreateLobby);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        buttonCreateLobby.setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiPlayerStartActivity.class);
            startActivity(intent);
        });

        buttonBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });









    }
}