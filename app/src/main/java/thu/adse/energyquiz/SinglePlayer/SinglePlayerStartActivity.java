package thu.adse.energyquiz.SinglePlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

// Activity to start a singleplayer game and choose the number of questions per round
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
            // The key argument of the intent here must match that used in the other activity
        }

        cardViewSinglePlayerStartPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase numberQuestionsPerRound by 1 when pressing the "plus"-cardView
                // The maximum number of questions per round is 10
                if(numberQuestionsPerRound < 10) {
                    numberQuestionsPerRound++;
                    TextViewSinglePlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
                }
            }
        });

        cardViewSinglePlayerStartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrease numberQuestionsPerRound by 1 when pressing the "minus"-cardView
                // The minimum number of questions per round is 1
                if (numberQuestionsPerRound > 1) {
                    numberQuestionsPerRound--;
                    TextViewSinglePlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
                }
            }
        });

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