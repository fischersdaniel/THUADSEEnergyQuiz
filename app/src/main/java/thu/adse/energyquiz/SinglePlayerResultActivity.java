package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePlayerResultActivity extends AppCompatActivity {

    private TextView results_textview, numberCorrectAnswers_textview, roundScore_textview, userScore_textview, userRank_textview;
    private Button repeatNewGame_button, home_button;

    private int numberQuestionsPerRound, numberCorrectAnswersRound, scoreRound, scoreUserLocal, totalCorrectAnswersLocal, totalAnswersLocal;
    private String userRankLocal;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_result);

        results_textview = findViewById(R.id.results_textview);
        numberCorrectAnswers_textview = findViewById(R.id.numberCorrectAnswers_textview);
        roundScore_textview = findViewById(R.id.roundScore_textview);
        userScore_textview = findViewById(R.id.userScore_textview);
        userRank_textview = findViewById(R.id.userRank_textview);
        repeatNewGame_button = findViewById(R.id.repeatNewGame_button);
        home_button = findViewById(R.id.home_button);

        numberQuestionsPerRound = 0;
        numberCorrectAnswersRound = 0;
        scoreRound = 0;
        scoreUserLocal = 0;

        // DB pull
            // pull of old user values
        String userId = "1"; //muss flex werden
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    results_textview.setText(getString(R.string.results_textview));
                    numberCorrectAnswers_textview.setText(getString(R.string.numberCorrectAnswers_textview) + "\n" + String.valueOf(numberCorrectAnswersRound) + " / " + String.valueOf(numberQuestionsPerRound));
                    roundScore_textview.setText(getString(R.string.roundScore_textview) + "\n" + String.valueOf(scoreRound));
                    userScore_textview.setText(getString(R.string.userScore_textview) + "\n" + String.valueOf(scoreUserLocal));
                    userRank_textview.setText(getString(R.string.userRank_textview) + "\n" + userRankLocal);

                    // DB push
                    //push of new user values
                    databaseReference.child("score").setValue(scoreUserLocal);
                    databaseReference.child("Rank").setValue(userRankLocal);
                    databaseReference.child("totalCorrectAnswers").setValue(totalCorrectAnswersLocal);
                    databaseReference.child("totalAnswers").setValue(totalAnswersLocal);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });

        repeatNewGame_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the number of questions to the next activity to directly start a new game round
                Intent i = new Intent(SinglePlayerResultActivity.this, SinglePlayerStartActivity.class);
                i.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                startActivity(i);
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SinglePlayerResultActivity.this, HomeScreenActivity.class));
            }
        });
    }
}