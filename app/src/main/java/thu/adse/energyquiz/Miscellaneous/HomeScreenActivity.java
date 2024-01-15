package thu.adse.energyquiz.Miscellaneous;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import thu.adse.energyquiz.MultiPlayer.MultiPlayerLobbyScreen;
import thu.adse.energyquiz.QuestionCatalog.MainActivityQuestionCatalog;
import thu.adse.energyquiz.R;
import thu.adse.energyquiz.SinglePlayer.SinglePlayerStartActivity;
import thu.adse.energyquiz.UserManagement.ForgotPasswordActivity;
import thu.adse.energyquiz.UserManagement.LoginActivity;
import thu.adse.energyquiz.UserManagement.MainActivity;

import thu.adse.energyquiz.MultiPlayer.MultiPlayerLobbyScreen;
public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean userLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Findet und initialisiere die CardView-Elemente
//        CardView cardViewHomeSettings = findViewById(R.id.cardViewHomeSettings);
        CardView cardViewHomeUser = findViewById(R.id.cardViewHomeUser);
        CardView cardViewHomeSingle = findViewById(R.id.cardViewHomeSingle);
        CardView cardViewHomeMulti = findViewById(R.id.cardViewHomeMulti);
        CardView cardViewHomeStatistics = findViewById(R.id.cardViewHomeStatistics);
        CardView cardViewHomeCatalog = findViewById(R.id.cardViewHomeCatalog);

        // Weist allen CardViews denselben OnClickListener zu, um Klickereignisse zu erfassen
//        cardViewHomeSettings.setOnClickListener(this);
        cardViewHomeUser.setOnClickListener(this);
        cardViewHomeSingle.setOnClickListener(this);
        cardViewHomeMulti.setOnClickListener(this);
        cardViewHomeStatistics.setOnClickListener(this);
        cardViewHomeCatalog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userLoggedIn = true;
        } else {
            userLoggedIn = false;
        }

        if (v.getId() == R.id.cardViewHomeUser) {
            // Aktionen für cardUser
            if(userLoggedIn){
                startActivity(new Intent(HomeScreenActivity.this, MainActivity.class));
            }else{
                Toast.makeText(HomeScreenActivity.this, getString(R.string.userNotLoggedIn), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
            }
        } else if (v.getId() == R.id.cardViewHomeSingle) {
            // Aktionen für cardSingle
            if(userLoggedIn){
                startActivity(new Intent(HomeScreenActivity.this, SinglePlayerStartActivity.class));
            }else{
                Toast.makeText(HomeScreenActivity.this, getString(R.string.userNotLoggedIn), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
            }
        } else if (v.getId() == R.id.cardViewHomeMulti) {
            // Aktionen für cardMulti
            if(userLoggedIn){
                MultiPlayerLobbyScreen.deletePossibleLobbyEntries();
                startActivity(new Intent(HomeScreenActivity.this, MultiPlayerLobbyScreen.class));
            }else{
                Toast.makeText(HomeScreenActivity.this, getString(R.string.userNotLoggedIn), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
            }
        } else if (v.getId() == R.id.cardViewHomeStatistics) {
            // Aktionen für cardStats
            if(userLoggedIn){
                startActivity(new Intent(HomeScreenActivity.this, StatisticsScreenActivity.class));
            }else{
                Toast.makeText(HomeScreenActivity.this, getString(R.string.userNotLoggedIn), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
            }
        } else if (v.getId() == R.id.cardViewHomeCatalog) {
            // Aktionen für cardCatalog
            // no check of userLoggedIn because
            startActivity(new Intent(HomeScreenActivity.this, MainActivityQuestionCatalog.class));
        } else {
            // Do Nothing // Standardaktion, wenn keine Übereinstimmung gefunden wurde
        }
    }
}

