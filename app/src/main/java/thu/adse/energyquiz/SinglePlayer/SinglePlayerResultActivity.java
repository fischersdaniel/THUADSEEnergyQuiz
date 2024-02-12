package thu.adse.energyquiz.SinglePlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.Button;
//import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

// Activity to display the results of a singleplayer game
// author D.F.
public class SinglePlayerResultActivity extends AppCompatActivity {

    private TextView textViewSinglePlayerResultsUser1QuotePlaceholder, textViewSinglePlayerResultsUser1PointsEarnedPlaceholder, textViewSinglePlayerResultsUser1AllPointsPlaceholder, textViewSinglePlayerResultsUser1RankPlaceholder;
    private CardView cardViewSinglePlayerResultsPlayAgain, cardViewSinglePlayerResultsGoHome;

    private int numberQuestionsPerRound, numberCorrectAnswersRound, scoreRound, scoreUserLocal, totalCorrectAnswersLocal, totalAnswersLocal;
    private String userID, userRankLocal;

    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_result);

        textViewSinglePlayerResultsUser1QuotePlaceholder = findViewById(R.id.textViewSinglePlayerResultsUser1QuotePlaceholder);
        textViewSinglePlayerResultsUser1PointsEarnedPlaceholder = findViewById(R.id.textViewSinglePlayerResultsUser1PointsEarnedPlaceholder);
        textViewSinglePlayerResultsUser1AllPointsPlaceholder = findViewById(R.id.textViewSinglePlayerResultsUser1AllPointsPlaceholder);
        textViewSinglePlayerResultsUser1RankPlaceholder = findViewById(R.id.textViewSinglePlayerResultsUser1RankPlaceholder);
        cardViewSinglePlayerResultsPlayAgain = findViewById(R.id.cardViewSinglePlayerResultsPlayAgain);
        cardViewSinglePlayerResultsGoHome = findViewById(R.id.cardViewSinglePlayerResultsGoHome);

        numberQuestionsPerRound = 0;
        numberCorrectAnswersRound = 0;
        scoreRound = 0;
        scoreUserLocal = 0;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");

        if (currentUser != null) {
            userID = currentUser.getUid();
            Log.d("current User", "succesfully getting userID:" + userID);
        } else {
            Log.d("current User", "Bitte Anmelden");
        }

        // DB pull of old user values
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    scoreUserLocal = dataSnapshot.child("score").getValue(Integer.class);
                    totalCorrectAnswersLocal = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                    totalAnswersLocal = dataSnapshot.child("totalAnswers").getValue(Integer.class);


                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
                        numberCorrectAnswersRound = extras.getInt("numberCorrectAnswersRound");
                        // The key argument here must match that used in the other activity
                    }

                    // Scoring system
                    // Calculate the score of the round, negative values are possible and mean more false than right answers
                    scoreRound = numberCorrectAnswersRound + (numberCorrectAnswersRound - numberQuestionsPerRound);

                    // the user score should never be negative, score stays at zero and for lowest rank
                    if(0 <= scoreUserLocal + scoreRound){
                        scoreUserLocal = scoreUserLocal + scoreRound;
                    } else{
                        scoreUserLocal = 0;
                    }

                    // Check rank on the basis of the scoring system / user score
                    if(scoreUserLocal < 20){
                        userRankLocal = getString(R.string.userRank_0);
                    }
                    else if(scoreUserLocal < 50){
                        userRankLocal = getString(R.string.userRank_1);
                    }
                    else if(scoreUserLocal < 100){
                        userRankLocal = getString(R.string.userRank_2);
                    }
                    else if(scoreUserLocal < 200){
                        userRankLocal = getString(R.string.userRank_3);
                    }
                    else if(scoreUserLocal < 500){
                        userRankLocal = getString(R.string.userRank_4);
                    }
                    else{
                        userRankLocal = getString(R.string.userRank_5);
                    }

                    totalCorrectAnswersLocal = totalCorrectAnswersLocal + numberCorrectAnswersRound;
                    totalAnswersLocal = totalAnswersLocal + numberQuestionsPerRound;

                    // L.B.: Display results on screen
                    textViewSinglePlayerResultsUser1QuotePlaceholder.setText(String.valueOf(numberCorrectAnswersRound) + " / " + String.valueOf(numberQuestionsPerRound));
                    textViewSinglePlayerResultsUser1PointsEarnedPlaceholder.setText(String.valueOf(scoreRound));
                    textViewSinglePlayerResultsUser1AllPointsPlaceholder.setText(String.valueOf(scoreUserLocal));
                    textViewSinglePlayerResultsUser1RankPlaceholder.setText(userRankLocal);

                    // DB push of new user values
                    usersDatabaseReference.child("score").setValue(scoreUserLocal);
                    usersDatabaseReference.child("rank").setValue(userRankLocal);
                    usersDatabaseReference.child("totalCorrectAnswers").setValue(totalCorrectAnswersLocal);
                    usersDatabaseReference.child("totalAnswers").setValue(totalAnswersLocal);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });

        cardViewSinglePlayerResultsPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the number of questions to the next activity to directly start a new game round
                Intent i = new Intent(SinglePlayerResultActivity.this, SinglePlayerStartActivity.class);
                i.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                startActivity(i);
                finish(); // L.B.: needs to be called BEFORE the navigation implementation. Else onLeaveThisActivity will be called AFTER onStartNewActivity -> wrong animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
            }
        });

        cardViewSinglePlayerResultsGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SinglePlayerResultActivity.this, HomeScreenActivity.class));
                finish(); // L.B.: needs to be called BEFORE the navigation implementation. Else onLeaveThisActivity will be called AFTER onStartNewActivity -> wrong animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
            }
        });
    }
}