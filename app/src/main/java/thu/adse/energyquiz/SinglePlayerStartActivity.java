package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SinglePlayerStartActivity extends AppCompatActivity {

    private Button startSinglePlayerGame_button;
    private int numberQuestionsPerRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_start);

        // Standard number of numberQuestionsPerRound
        numberQuestionsPerRound = 5;

        // get the passed integer value "numberQuestionsPerRound" from the previous activity
        // only used when one round gets played directly after another (!=null)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
            //The key argument here must match that used in the other activity
        }

        startSinglePlayerGame_button = findViewById(R.id.startSinglePlayerGame_button);

        // !!!!!!!!!! numberQuestionsPerRound muss durch Logik von Linus Mockup beschrieben werden, Code muss in diese Activity eingepasst werden
        // display the variable numberQuestionsPerRound and buttons for logic -/+

        startSinglePlayerGame_button.setOnClickListener(new View.OnClickListener() {
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