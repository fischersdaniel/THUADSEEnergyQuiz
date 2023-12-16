package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SinglePlayerGameActivity extends AppCompatActivity {

    private TextView numberQuestionsProgress_textview, question_textview, answer1_textview, answer2_textview, answer3_textview, answer4_textview;
    private Button confirm_next_button;
    private int color_green, color_red, color_white, color_grey;
    private boolean answer1_choosen, answer2_choosen, answer3_choosen,answer4_choosen, switchConfirmNextButton;
    private int numberQuestionsPerRound, actualQuestionNumber, actualQuestionID, numberCorrectAnswersRound;
    private int[] questionIDsPerRound;
    DatabaseReference questionsDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_game);

        questionsDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Questions");

        color_green = Color.parseColor("#25e712");
        color_red = Color.parseColor("#c51d34");
        color_white = Color.parseColor("#FFFFFF");
        color_grey = Color.parseColor("#B9BBBE");

        numberQuestionsPerRound = 4; //for testing
        actualQuestionNumber = 0;
        actualQuestionID = 0;
        numberCorrectAnswersRound = 0;
        switchConfirmNextButton = false;

        numberQuestionsProgress_textview = findViewById(R.id.numberQuestionsProgress_textview);
        question_textview = findViewById(R.id.question_textview);
        answer1_textview = findViewById(R.id.answer1_textview);
        answer2_textview = findViewById(R.id.answer2_textview);
        answer3_textview = findViewById(R.id.answer3_textview);
        answer4_textview = findViewById(R.id.answer4_textview);
        confirm_next_button = findViewById(R.id.confirm_next_button);

        // get the passed integer value "numberQuestionsPerRound" from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
            //The key argument here must match that used in the other activity
        }

        // DB pulls missing!!!

        // Generate random question IDs within the question catalog
        //10 must be maxnumber ID from DB
       // for(int counter_questions=0; counter_questions<numberQuestionsPerRound; counter_questions++) {

       //     int randomQuestionID = new Random().nextInt(10);

            // Since Appstart asked questions should not be part of the round
            // randomQuestionID should not be within the DB in [array:int:usedSessionIDs]
               /* while(i<zahl.length){
                    zahl[i]=i;
                    System.out.println(zahl[i]);
                    i++;
                    //zahl ist das Array
                }*/
            // a question is only one time per round allowed

            //starts not with zero but with one as index!
       //     questionIDsPerRound[counter_questions] = randomQuestionID;
       // }

        // Question DB pull
        actualQuestionID = 1;

       // questionsDbRef.child(toString(actualQuestionID)).child("answers").child("answer4").child("correctAnswer").getKey();


        // set the textviews with the data for the first question
        actualQuestionNumber = 1;
        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
        question_textview.setText("text you want to display");
        answer1_textview.setText("text you want to display");
        answer2_textview.setText("text you want to display");
        answer3_textview.setText("text you want to display");
        answer4_textview.setText("text you want to display");
        confirm_next_button.setText(getString(R.string.confirm_button));

        answer1_textview.setBackgroundColor(color_white);
        answer2_textview.setBackgroundColor(color_white);
        answer3_textview.setBackgroundColor(color_white);
        answer4_textview.setBackgroundColor(color_white);

        answer1_choosen = false;
        answer2_choosen = false;
        answer3_choosen = false;
        answer4_choosen = false;

        //------------

        answer1_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer1_choosen = !answer1_choosen;
                    if(answer1_choosen){
                        answer1_textview.setBackgroundColor(color_grey);
                    }
                    else{
                        answer1_textview.setBackgroundColor(color_white);
                    }
                }
            }
        });
        answer2_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer2_choosen = !answer2_choosen;
                    if(answer2_choosen){
                        answer2_textview.setBackgroundColor(color_grey);
                    }
                    else{
                        answer2_textview.setBackgroundColor(color_white);
                    }
                }
            }
        });
        answer3_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer3_choosen = !answer3_choosen;
                    if(answer3_choosen){
                        answer3_textview.setBackgroundColor(color_grey);
                    }
                    else{
                        answer3_textview.setBackgroundColor(color_white);
                    }
                }
            }
        });
        answer4_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer4_choosen = !answer4_choosen;
                    if(answer4_choosen){
                        answer4_textview.setBackgroundColor(color_grey);
                    }
                    else{
                        answer4_textview.setBackgroundColor(color_white);
                    }
                }
            }
        });
        confirm_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // first click confirms the choosen answers and shows if the answers are right
                // second click (else) loads the next question or goes to the result screen
                if (false == switchConfirmNextButton) {
                    switchConfirmNextButton = true;
                    confirm_next_button.setText(getString(R.string.next_button));
                    //DB einmal holen
                    if (answer1_choosen == true) {
                        answer1_textview.setBackgroundColor(color_green);
                    } else {
                        answer1_textview.setBackgroundColor(color_red);
                    }

                    if (answer2_choosen == true) {
                        answer2_textview.setBackgroundColor(color_green);
                    } else {
                        answer2_textview.setBackgroundColor(color_red);
                    }

                    if (answer3_choosen == true) {
                        answer3_textview.setBackgroundColor(color_green);
                    } else {
                        answer3_textview.setBackgroundColor(color_red);
                    }

                    if (answer4_choosen == true) {
                        answer4_textview.setBackgroundColor(color_green);
                    } else {
                        answer4_textview.setBackgroundColor(color_red);
                    }

                    if (answer1_choosen == true & answer2_choosen == true & answer3_choosen == true & answer4_choosen == true) {
                        //true durch Auslesen der geholten Daten aus DB ersetzen
                        numberCorrectAnswersRound = numberCorrectAnswersRound + 1;
                    } else {
                        // no operation
                        // numberCorrectAnswersRound stays the same
                    }
                } else {
                    switchConfirmNextButton = false;

                    // if: not all questions were asked, ask the next one
                    // else: all questions were asked, go to the result screen
                    if(actualQuestionNumber < numberQuestionsPerRound){

                        actualQuestionNumber++;

                        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
                        question_textview.setText("text you want to display");
                        answer1_textview.setText("text you want to display");
                        answer2_textview.setText("text you want to display");
                        answer3_textview.setText("text you want to display");
                        answer4_textview.setText("text you want to display");
                        confirm_next_button.setText(getString(R.string.confirm_button));

                        answer1_textview.setBackgroundColor(color_white);
                        answer2_textview.setBackgroundColor(color_white);
                        answer3_textview.setBackgroundColor(color_white);
                        answer4_textview.setBackgroundColor(color_white);

                        answer1_choosen = false;
                        answer2_choosen = false;
                        answer3_choosen = false;
                        answer4_choosen = false;

                    } else{

                        // DB push missing!!!
                            //push of new IDs of the asked questions

                        //Pass the results of the round to the next activity (result screen)
                        Intent i = new Intent(SinglePlayerGameActivity.this, SinglePlayerResultActivity.class);
                        i.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                        i.putExtra("numberCorrectAnswersRound", numberCorrectAnswersRound);
                        startActivity(i);
                    }
                }
            }
        });
    }
}