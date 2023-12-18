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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePlayerGameActivity extends AppCompatActivity {

    private TextView numberQuestionsProgress_textview, question_textview, answer1_textview, answer2_textview, answer3_textview, answer4_textview;
    private Button confirm_next_button;
    private int color_green, color_red, color_white, color_grey;
    private boolean answer1_choosen, answer2_choosen, answer3_choosen,answer4_choosen, switchConfirmNextButton;
    private int numberQuestionsPerRound, counterRandomQuestions, actualQuestionNumber, actualQuestionID, numberCorrectAnswersRound;
    private String actualQuestionTitle, actualQuestionAnswer1, actualQuestionAnswer2, actualQuestionAnswer3, actualQuestionAnswer4;
    private Boolean actualQuestionAnswer1Correct, actualQuestionAnswer2Correct, actualQuestionAnswer3Correct, actualQuestionAnswer4Correct;
    private int[] questionIDsPerRound;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_game);

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

        questionIDsPerRound = new int[numberQuestionsPerRound];
        Random randomQuestionID = new Random();
        counterRandomQuestions = 0;
        while(counterRandomQuestions < numberQuestionsPerRound){

            int i = randomQuestionID.nextInt(10+1);
            //+1, because the maximum is excluded
            // Since app start asked questions should not be part of the round
            // randomQuestionID should not be within the DB in [array:int:usedSessionIDs]
               /* while(i<zahl.length){
                    zahl[i]=i;
                    System.out.println(zahl[i]);
                    i++;
                    //zahl ist das Array
                }*/
            // a question is only one time per round allowed
            questionIDsPerRound[counterRandomQuestions] = i;
            counterRandomQuestions++;
        }

        // set the textviews with the default data
        actualQuestionNumber = 1;
        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
        confirm_next_button.setText(getString(R.string.confirm_button));

        answer1_textview.setBackgroundColor(color_white);
        answer2_textview.setBackgroundColor(color_white);
        answer3_textview.setBackgroundColor(color_white);
        answer4_textview.setBackgroundColor(color_white);

        answer1_choosen = false;
        answer2_choosen = false;
        answer3_choosen = false;
        answer4_choosen = false;


        // Question DB pull
        actualQuestionID = 1;
       // databaseReference = FirebaseDatabase.getInstance().getReference().child("Questions").child(actualQuestionIDString);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Questions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    actualQuestionTitle = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("questionTitle").getValue(String.class);
                    actualQuestionAnswer1 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer1").child("answerText").getValue(String.class);
                    actualQuestionAnswer1Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class);
                    actualQuestionAnswer2 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer2").child("answerText").getValue(String.class);
                    actualQuestionAnswer2Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class);
                    actualQuestionAnswer3 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer3").child("answerText").getValue(String.class);
                    actualQuestionAnswer3Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class);
                    actualQuestionAnswer4 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer4").child("answerText").getValue(String.class);
                    actualQuestionAnswer4Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class);

                    // set the textviews with the data for the first question
                    numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
                    question_textview.setText(actualQuestionTitle);
                    answer1_textview.setText(actualQuestionAnswer1);
                    answer2_textview.setText(actualQuestionAnswer2);
                    answer3_textview.setText(actualQuestionAnswer3);
                    answer4_textview.setText(actualQuestionAnswer4);
                    confirm_next_button.setText(getString(R.string.confirm_button));
                }
                else{
                    question_textview.setText("Not found");
                    answer1_textview.setText("Not found");
                    answer2_textview.setText("Not found");
                    answer3_textview.setText("Not found");
                    answer4_textview.setText("Not found");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });

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
                    if (answer1_choosen == actualQuestionAnswer1Correct) {
                        answer1_textview.setBackgroundColor(color_green);
                    } else {
                        answer1_textview.setBackgroundColor(color_red);
                    }

                    if (answer2_choosen == actualQuestionAnswer2Correct) {
                        answer2_textview.setBackgroundColor(color_green);
                    } else {
                        answer2_textview.setBackgroundColor(color_red);
                    }

                    if (answer3_choosen == actualQuestionAnswer3Correct) {
                        answer3_textview.setBackgroundColor(color_green);
                    } else {
                        answer3_textview.setBackgroundColor(color_red);
                    }

                    if (answer4_choosen == actualQuestionAnswer4Correct) {
                        answer4_textview.setBackgroundColor(color_green);
                    } else {
                        answer4_textview.setBackgroundColor(color_red);
                    }

                    if (answer1_choosen == actualQuestionAnswer1Correct & answer2_choosen == actualQuestionAnswer2Correct & answer3_choosen == actualQuestionAnswer3Correct & answer4_choosen == actualQuestionAnswer4Correct) {
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
                        actualQuestionID++; //for test without random

                        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));

                        answer1_textview.setBackgroundColor(color_white);
                        answer2_textview.setBackgroundColor(color_white);
                        answer3_textview.setBackgroundColor(color_white);
                        answer4_textview.setBackgroundColor(color_white);

                        answer1_choosen = false;
                        answer2_choosen = false;
                        answer3_choosen = false;
                        answer4_choosen = false;

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    actualQuestionTitle = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("questionTitle").getValue(String.class);
                                    actualQuestionAnswer1 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer1").child("answerText").getValue(String.class);
                                    actualQuestionAnswer1Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer2 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer2").child("answerText").getValue(String.class);
                                    actualQuestionAnswer2Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer3 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer3").child("answerText").getValue(String.class);
                                    actualQuestionAnswer3Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer4 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer4").child("answerText").getValue(String.class);
                                    actualQuestionAnswer4Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber])).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class);

                                    // set the textviews with the data for the questions
                                    numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
                                    question_textview.setText(actualQuestionTitle);
                                    answer1_textview.setText(actualQuestionAnswer1);
                                    answer2_textview.setText(actualQuestionAnswer2);
                                    answer3_textview.setText(actualQuestionAnswer3);
                                    answer4_textview.setText(actualQuestionAnswer4);
                                    confirm_next_button.setText(getString(R.string.confirm_button));
                                }
                                else{
                                    question_textview.setText("Not found");
                                    answer1_textview.setText("Not found");
                                    answer2_textview.setText("Not found");
                                    answer3_textview.setText("Not found");
                                    answer4_textview.setText("Not found");
                                    confirm_next_button.setText(getString(R.string.confirm_button));
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
                            }
                        });

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