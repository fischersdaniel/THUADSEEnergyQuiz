package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SinglePlayerResultActivity extends AppCompatActivity {

    private TextView results_textview, numberCorrectAnswers_textview, roundScore_textview, userScore_textview, userRank_textview;
    private Button repeatNewGame_button, home_button;

    private int numberQuestionsPerRound, numberCorrectAnswersRound, scoreRound, scoreUserTemp;
    private String userRankLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_result);

        numberQuestionsPerRound = 0;
        numberCorrectAnswersRound = 0;
        scoreRound = 0;
        scoreUserTemp = 0;

        //pull DB missing!!!


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
        if(0 <= scoreUserTemp + scoreRound){
            scoreUserTemp = scoreUserTemp + scoreRound;
        } else{
            scoreUserTemp = 0;
        }

        // Check rank on the basis of the scoring system / user score
        if(scoreUserTemp <= 20){
            userRankLocal = getString(R.string.userRank_0);
        }
        else if(scoreUserTemp <= 50){
            userRankLocal = getString(R.string.userRank_1);
        }
        else if(scoreUserTemp <= 100){
            userRankLocal = getString(R.string.userRank_2);
        }
        else if(scoreUserTemp <= 200){
            userRankLocal = getString(R.string.userRank_3);
        }
        else if(scoreUserTemp <= 500){
            userRankLocal = getString(R.string.userRank_4);
        }
        else{
            userRankLocal = getString(R.string.userRank_5);
        }

        results_textview = findViewById(R.id.results_textview);
        numberCorrectAnswers_textview = findViewById(R.id.numberCorrectAnswers_textview);
        roundScore_textview = findViewById(R.id.roundScore_textview);
        userScore_textview = findViewById(R.id.userScore_textview);
        userRank_textview = findViewById(R.id.userRank_textview);
        repeatNewGame_button = findViewById(R.id.repeatNewGame_button);
        home_button = findViewById(R.id.home_button);

        results_textview.setText(getString(R.string.results_textview));
        numberCorrectAnswers_textview.setText(getString(R.string.numberCorrectAnswers_textview) + "\n" + String.valueOf(numberCorrectAnswersRound) + " / " + String.valueOf(numberQuestionsPerRound));
        roundScore_textview.setText(getString(R.string.roundScore_textview) + "\n" + String.valueOf(scoreRound));
        userScore_textview.setText(getString(R.string.userScore_textview) + "\n" + String.valueOf(scoreUserTemp));
        userRank_textview.setText(getString(R.string.userScore_textview) + "\n" + userRankLocal);

        // DB push missing!!!
            //push of new scoreUserTemp, numberaskedquestions, numbercorrectanswers

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
                startActivity(new Intent(SinglePlayerResultActivity.this, MainActivity.class));
            }
        });
    }
}