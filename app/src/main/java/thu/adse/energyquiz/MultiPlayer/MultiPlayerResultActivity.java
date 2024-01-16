package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import thu.adse.energyquiz.SinglePlayer.SinglePlayerResultActivity;

public class MultiPlayerResultActivity extends AppCompatActivity {

    private TextView textViewMultiPlayerResultsUser1QuotePlaceholder, textViewMultiPlayerResultsUser2QuotePlaceholder,
            textViewMultiPlayerResultsUser1PointsEarnedPlaceholder, textViewMultiPlayerResultsUser1AllPointsPlaceholder,
            textViewMultiPlayerResultsUser1RankPlaceholder, textViewWinner;
    private CardView cardViewMultiPlayerResultsPlayAgain, cardViewMultiPlayerResultsGoHome;

    private int numberQuestionsPerRound, numberCorrectAnswersRound, scoreRound, scoreUserLocal, totalCorrectAnswersLocal, totalAnswersLocal, numberCorrectAnswersRoundOpponent;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference dbRef, userDbRef;
    String userRankLocal, winner;
    Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_result);

        textViewMultiPlayerResultsUser1QuotePlaceholder = findViewById(R.id.textViewMultiPlayerResultsUser1QuotePlaceholder);
        textViewMultiPlayerResultsUser2QuotePlaceholder = findViewById(R.id.textViewMultiPlayerResultsUser2QuotePlaceholder);
        textViewMultiPlayerResultsUser1PointsEarnedPlaceholder = findViewById(R.id.textViewMultiPlayerResultsUser1PointsEarnedPlaceholder);
        textViewMultiPlayerResultsUser1AllPointsPlaceholder = findViewById(R.id.textViewMultiPlayerResultsUser1AllPointsPlaceholder);
        textViewMultiPlayerResultsUser1RankPlaceholder = findViewById(R.id.textViewMultiPlayerResultsUser1RankPlaceholder);
        textViewWinner = findViewById(R.id.textViewWinner);

        cardViewMultiPlayerResultsPlayAgain = findViewById(R.id.cardViewMultiPlayerResultsPlayAgain);
        cardViewMultiPlayerResultsGoHome = findViewById(R.id.cardViewMultiPlayerResultsGoHome);
        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference();
        userDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        extras = getIntent().getExtras();
        currentUser = auth.getCurrentUser();


        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scoreUserLocal = snapshot.child("Users").child(currentUser.getUid()).child("score").getValue(Integer.class);
                totalAnswersLocal = snapshot.child("Users").child(currentUser.getUid()).child("totalAnswers").getValue(Integer.class);
                totalCorrectAnswersLocal = snapshot.child("Users").child(currentUser.getUid()).child("totalCorrectAnswers").getValue(Integer.class);

                if (extras != null) {
                    numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
                    numberCorrectAnswersRound = extras.getInt("numberCorrectAnswersRound");
                }

                scoreRound = numberCorrectAnswersRound + (numberCorrectAnswersRound - numberQuestionsPerRound);

                if (0 <= scoreUserLocal + scoreRound) {
                    scoreUserLocal += scoreRound;
                } else {
                    scoreUserLocal = 0;
                }
                if (scoreUserLocal < 20) {
                    userRankLocal = getString(R.string.userRank_0);
                } else if (scoreUserLocal < 50) {
                    userRankLocal = getString(R.string.userRank_1);
                } else if (scoreUserLocal < 100) {
                    userRankLocal = getString(R.string.userRank_2);
                } else if (scoreUserLocal < 200) {
                    userRankLocal = getString(R.string.userRank_3);
                } else if (scoreUserLocal < 500) {
                    userRankLocal = getString(R.string.userRank_4);
                } else {
                    userRankLocal = getString(R.string.userRank_5);
                }

                totalCorrectAnswersLocal = totalCorrectAnswersLocal + numberCorrectAnswersRound;
                totalAnswersLocal = totalAnswersLocal + numberQuestionsPerRound;

                textViewMultiPlayerResultsUser1QuotePlaceholder.setText(String.valueOf(numberCorrectAnswersRound) + " / " + String.valueOf(numberQuestionsPerRound));
                textViewMultiPlayerResultsUser1PointsEarnedPlaceholder.setText(String.valueOf(scoreRound));
                textViewMultiPlayerResultsUser1AllPointsPlaceholder.setText(String.valueOf(scoreUserLocal));
                textViewMultiPlayerResultsUser1RankPlaceholder.setText(String.valueOf(userRankLocal));

                getWinner(snapshot);
                setTextForWinner();

                if (extras.getBoolean("creatorIsLoggedIn")) {
                    textViewMultiPlayerResultsUser2QuotePlaceholder.setText(String.valueOf(snapshot.child("Lobbies").child("full").child(extras.getString("player1ID")).child("correctAnswersPlayer2").getValue(Long.class)));
                } else if (!extras.getBoolean("creatorIsLoggedIn")) {
                    textViewMultiPlayerResultsUser2QuotePlaceholder.setText(String.valueOf(snapshot.child("Lobbies").child("full").child(extras.getString("player1ID")).child("correctAnswersPlayer1").getValue(Long.class)));
                }

                updateUserDb();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        cardViewMultiPlayerResultsPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultiPlayerResultActivity.this, MultiPlayerLobbyScreen.class);
                dbRef.child("Lobbies").child("full").child(extras.getString("player1ID")).removeValue();
                startActivity(intent);
            }
        });

        cardViewMultiPlayerResultsGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRef.child("Lobbies").child("full").child(extras.getString("player1ID")).removeValue();
                startActivity(new Intent(MultiPlayerResultActivity.this, HomeScreenActivity.class));
            }
        });

    }

    private void getWinner(DataSnapshot snapshot) {
        long scorePlayer1 = snapshot.child("Lobbies").child("full").child(extras.getString("player1ID")).child("correctAnswersPlayer1").getValue(Long.class);
        long scorePlayer2 = snapshot.child("Lobbies").child("full").child(extras.getString("player1ID")).child("correctAnswersPlayer2").getValue(Long.class);

        if (scorePlayer1 < scorePlayer2) {
            winner = "player2";
        } else if (scorePlayer2 < scorePlayer1) {
            winner = "player1";

        } else if (scorePlayer1 == scorePlayer2) {
            winner = "tie";
        }
    }

    private void setTextForWinner() { //TODO: Strings nicht mehr softcoden.
        if (winner.equals("tie")) {
            textViewWinner.setText("Untentschieden.");
        } else if (currentUser.getUid().equals(extras.getString("player1ID"))) {
            if (winner.equals("player1")) {
                textViewWinner.setText("Gewonnen!");
            } else if (winner.equals("player2")) {
                textViewWinner.setText("Leider Verloren :(");
            }

        } else if (currentUser.getUid().equals(extras.getString("player2ID"))) {
            if (winner.equals("player2")) {
                textViewWinner.setText("Gewonnen!");
            } else if (winner.equals("player1")) {
                textViewWinner.setText("Leider Verloren :(");
            }
        }
    }

    private void updateUserDb() {
        if (extras.getBoolean("creatorIsLoggedIn")) {
            userDbRef.child(extras.getString("player1ID")).child("score").setValue(scoreUserLocal);
            userDbRef.child(extras.getString("player1ID")).child("rank").setValue(userRankLocal);
            userDbRef.child(extras.getString("player1ID")).child("totalCorrectAnswers").setValue(totalCorrectAnswersLocal);
            userDbRef.child(extras.getString("player1ID")).child("totalAnswers").setValue(totalAnswersLocal);
        } else {
            userDbRef.child(extras.getString("player2ID")).child("score").setValue(scoreUserLocal);
            userDbRef.child(extras.getString("player2ID")).child("rank").setValue(userRankLocal);
            userDbRef.child(extras.getString("player2ID")).child("totalCorrectAnswers").setValue(totalCorrectAnswersLocal);
            userDbRef.child(extras.getString("player2ID")).child("totalAnswers").setValue(totalAnswersLocal);
        }
    }
}