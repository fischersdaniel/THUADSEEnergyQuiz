package thu.adse.energyquiz.SinglePlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import thu.adse.energyquiz.R;

public class SinglePlayerGameActivity extends AppCompatActivity {

    private TextView numberQuestionsProgress_textview, question_textview, answer1_textview, answer2_textview, answer3_textview, answer4_textview;
    private Button confirm_next_button;
    private int color_green, color_red, color_white, color_grey;
    private boolean answer1_choosen, answer2_choosen, answer3_choosen,answer4_choosen, switchConfirmNextButton;
    private int numberQuestionsPerRound, counterRandomQuestions, actualQuestionNumber, actualQuestionID, numberCorrectAnswersRound, counterUsedSessionIDs, counterSameRandomIDs;
    private String actualQuestionTitle, actualQuestionAnswer1, actualQuestionAnswer2, actualQuestionAnswer3, actualQuestionAnswer4;
    private Boolean actualQuestionAnswer1Correct, actualQuestionAnswer2Correct, actualQuestionAnswer3Correct, actualQuestionAnswer4Correct;
    private int[] questionIDsPerRound;
    private List<Integer> usedSessionIDsLocal = new ArrayList<>();
    private String userID;

    private DatabaseReference usersDatabaseReference;
    private DatabaseReference questionsDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_game);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");

        if (currentUser != null) {
            userID = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
            Log.d("current User", "successfully getting userID:" + userID);
        } else {
            Log.d("current User", "Bitte Anmelden");
        }

        // get the passed integer value "numberQuestionsPerRound" from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
            //The key argument here must match that used in the other activity
        }
        else{
            numberQuestionsPerRound = 4; //for testing
        }

        color_green = Color.parseColor("#25e712");
        color_red = Color.parseColor("#c51d34");
        color_white = Color.parseColor("#FFFFFF");
        color_grey = Color.parseColor("#B9BBBE");

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

        // Get the user DB
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Type indicator here is needed to read the array from the database
                    GenericTypeIndicator<ArrayList<Integer>> typeIndi = new GenericTypeIndicator<ArrayList<Integer>>() {};
                    //ArrayList<Integer> yourStringArray = dataSnapshot.child("usedSessionIDs").getValue(typeIndi);
                    usedSessionIDsLocal = dataSnapshot.child("usedSessionIDs").getValue(typeIndi);
                    Log.d("current User", "usedSessionIDsLocal:" + usedSessionIDsLocal);
                    Log.d("current User", "usedSessionIDsLocal SIZE:" + usedSessionIDsLocal.size());

                    // Generate random question IDs within the question catalog
                    // 10 must be maxnumber ID from DB
                    questionIDsPerRound = new int[numberQuestionsPerRound];
                    Random randomQuestionID = new Random();
                    counterRandomQuestions = 0;
                    int max = 11;
                    int min = 1;
                    boolean IDchecked = true;
                    boolean IDUsedOncechecked;
                    while(counterRandomQuestions < numberQuestionsPerRound){
                        Log.d("current User", "counterRandomQuestions:" + counterRandomQuestions);
                        // normal random function: [0, n-1], here: [1, n]
                        // +1, because max is excluded
                        int randID = randomQuestionID.nextInt((max - min) + 1) + min;
                        Log.d("current User", "randID:" + randID);
                        // Check, that it is a possible to solve problem and no endless repetition without the chance of solving the problem
                        if(11 - usedSessionIDsLocal.size() > numberQuestionsPerRound){
                            // Since app start asked questions should not be part of the round
                            // randomQuestionID should not be within the user specific DB entry [array:int:usedSessionIDs]
                            counterUsedSessionIDs = 0;
                            Log.d("current User", "usedSessionIDsLocal SIZE:" + usedSessionIDsLocal.size());
                            IDchecked = true;
                            while(counterUsedSessionIDs < usedSessionIDsLocal.size()){
                                Log.d("current User", "counterUsedSessionIDs:" + counterUsedSessionIDs);
                                if(usedSessionIDsLocal.get(counterUsedSessionIDs) == randID){
                                    // do not use the randID because it was used since app start
                                    Log.d("current User", "IDchecked inside:" + IDchecked);
                                    IDchecked = false;
                                    break;
                                }
                                Log.d("current User", "IDchecked:" + IDchecked);
                                counterUsedSessionIDs++;
                            }
                            IDUsedOncechecked = false;
                        }
                        else{
                            // every ID is okay, no checks if already used since app start
                            // only check, if used in the round (no question more than one time per round)
                            IDchecked = true;
                            for(int counterCheckRound=0; counterCheckRound<numberQuestionsPerRound; counterCheckRound++){
                                if(questionIDsPerRound[counterCheckRound] == randID){
                                    IDchecked = false;
                                }
                            }
                            // no need to add the questionID more than once to the usedSessionIDs
                            IDUsedOncechecked = true;
                        }

                        if(true == IDchecked){
                            // check if the while search in the usedSessionIDsLocal was okay
                            // a question is only one time per round allowed, so add it to the usedSessionIDsLocal to check next repeat if already used
                            questionIDsPerRound[counterRandomQuestions] = randID;
                            counterRandomQuestions++;
                            // no need to add the questionID more than once to the usedSessionIDs
                            if(false == IDUsedOncechecked){
                                usedSessionIDsLocal.add(randID);
                            }

                        }
                    }

                    // Question DB pull
                    //actualQuestionID = 1;
                    actualQuestionNumber = 1;
                    // databaseReference = FirebaseDatabase.getInstance().getReference().child("Questions").child(actualQuestionIDString);
                    questionsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Questions");
                    questionsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                actualQuestionTitle = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("questionTitle").getValue(String.class);
                                actualQuestionAnswer1 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer1").child("answerText").getValue(String.class);
                                actualQuestionAnswer1Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer2 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer2").child("answerText").getValue(String.class);
                                actualQuestionAnswer2Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer3 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer3").child("answerText").getValue(String.class);
                                actualQuestionAnswer3Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer4 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer4").child("answerText").getValue(String.class);
                                actualQuestionAnswer4Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class);

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

                } else {
                    //not found
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });

        /*
        // Test array, check on the right array logic
        questionIDsPerRound[0] = 1;
        questionIDsPerRound[1] = 2;
        questionIDsPerRound[2] = 3;
        questionIDsPerRound[3] = 4;
        questionIDsPerRound[4] = 5;
        */


        // set the text views with the default data
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
                        //actualQuestionID++; //for test without random

                        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));

                        answer1_textview.setBackgroundColor(color_white);
                        answer2_textview.setBackgroundColor(color_white);
                        answer3_textview.setBackgroundColor(color_white);
                        answer4_textview.setBackgroundColor(color_white);

                        answer1_choosen = false;
                        answer2_choosen = false;
                        answer3_choosen = false;
                        answer4_choosen = false;

                        questionsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    actualQuestionTitle = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("questionTitle").getValue(String.class);
                                    actualQuestionAnswer1 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer1").child("answerText").getValue(String.class);
                                    actualQuestionAnswer1Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer2 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer2").child("answerText").getValue(String.class);
                                    actualQuestionAnswer2Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer3 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer3").child("answerText").getValue(String.class);
                                    actualQuestionAnswer3Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class);
                                    actualQuestionAnswer4 = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer4").child("answerText").getValue(String.class);
                                    actualQuestionAnswer4Correct = dataSnapshot.child(String.valueOf(questionIDsPerRound[actualQuestionNumber-1])).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class);

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
                        // Push of new IDs of the asked questions to the user DB
                        // At the end of the activity because then the questions were really asked
                        //usedSessionIDsLocal.add(0);
                        usersDatabaseReference.child("usedSessionIDs").setValue(usedSessionIDsLocal);

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