package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SinglePlayerStartActivity extends AppCompatActivity{

    private int numberQuestionsPerRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_start);

        CardView cardViewSinglePlayerStartBack = findViewById(R.id.cardViewSinglePlayerStartBack);
        CardView cardViewSinglePlayerStartPlayButton = findViewById(R.id.cardViewSinglePlayerStartPlayButton);
        CardView cardViewSinglePlayerStartPlus = findViewById(R.id.cardViewSinglePlayerStartPlus);
        CardView cardViewSinglePlayerStartMinus = findViewById(R.id.cardViewSinglePlayerStartMinus);
        TextView TextViewSinglePlayerStartNumberInput = findViewById(R.id.textViewLobbyCreatorUserName);

        cardViewSinglePlayerStartBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        // Standard number of numberQuestionsPerRound

        numberQuestionsPerRound = 5;

        // get the passed integer value "numberQuestionsPerRound" from the previous activity
        // only used when one round gets played directly after another (!=null)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
            TextViewSinglePlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
            //The key argument here must match that used in the other activity
        }

        cardViewSinglePlayerStartPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Erhöhe numberQuestionsPerRound um 1 bei Drücken der "Plus"-cardView
                numberQuestionsPerRound++;
                TextViewSinglePlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
            }
        });

        cardViewSinglePlayerStartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Erhöhe numberQuestionsPerRound um 1 bei Drücken der "Plus"-cardView
                if (numberQuestionsPerRound > 1) {
                    numberQuestionsPerRound--;
                    TextViewSinglePlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
                }
            }
        });


        // display the variable numberQuestionsPerRound and buttons for logic -/+

        cardViewSinglePlayerStartPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass the number of questions to the next activity
                Intent i = new Intent(SinglePlayerStartActivity.this, SinglePlayerGameActivity.class);
                i.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                startActivity(i);
            }
        });

    }
}