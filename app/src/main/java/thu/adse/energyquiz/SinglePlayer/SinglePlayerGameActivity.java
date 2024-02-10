package thu.adse.energyquiz.SinglePlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

// Activity to play a singleplayer game
public class SinglePlayerGameActivity extends AppCompatActivity {

    private TextView numberQuestionsProgress_textview, question_textview, answer1_textview, answer2_textview, answer3_textview, answer4_textview, confirm_next_textview;
    private MaterialCardView cardViewSinglePlayerAnswer1, cardViewSinglePlayerAnswer2, cardViewSinglePlayerAnswer3, cardViewSinglePlayerAnswer4, cardViewSinglePlayerSubmitAnswer;
    private boolean answer1_choosen, answer2_choosen, answer3_choosen,answer4_choosen, switchConfirmNextButton;
    private int numberQuestionsPerRound, counterRandomQuestions, actualQuestionNumber, numberCorrectAnswersRound, counterUsedSessionIDs;
    private long countQuestionChilds;
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

        // L.B.: Checks for back-button press, changes activities accordingly and applies custom transition
        CardView cardViewSinglePlayerGameBack = findViewById(R.id.cardViewSinglePlayerGameBack);
        cardViewSinglePlayerGameBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
            finish(); // L.B.: needs to be called BEFORE the navigation implementation. Else onLeaveThisActivity will be called AFTER onStartNewActivity -> wrong animation
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");

        if (currentUser != null) {
            userID = currentUser.getUid();
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

        actualQuestionNumber = 0;
        numberCorrectAnswersRound = 0;
        switchConfirmNextButton = false;

        // L.B.: Initialize/find UI elements
        numberQuestionsProgress_textview = findViewById(R.id.numberQuestionsProgress_textview);
        question_textview = findViewById(R.id.question_textview);
        answer1_textview = findViewById(R.id.answer1_textview);
        answer2_textview = findViewById(R.id.answer2_textview);
        answer3_textview = findViewById(R.id.answer3_textview);
        answer4_textview = findViewById(R.id.answer4_textview);
        confirm_next_textview = findViewById(R.id.confirm_next_textview);

        cardViewSinglePlayerAnswer1 = findViewById(R.id.cardViewSinglePlayerAnswer1);
        cardViewSinglePlayerAnswer2 = findViewById(R.id.cardViewSinglePlayerAnswer2);
        cardViewSinglePlayerAnswer3 = findViewById(R.id.cardViewSinglePlayerAnswer3);
        cardViewSinglePlayerAnswer4 = findViewById(R.id.cardViewSinglePlayerAnswer4);
        cardViewSinglePlayerSubmitAnswer = findViewById(R.id.cardViewSinglePlayerSubmitAnswer);

        // Get the user DB
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //when the User DB is loaded, pull the question DB
                    questionsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Questions");
                    questionsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotQuestions) {
                            if (dataSnapshotQuestions.exists()) {
                                // get the number of questions in the question DB (childs of question DB)
                                countQuestionChilds = dataSnapshotQuestions.getChildrenCount();
                                Log.d("current User", "countQuestionChilds " + countQuestionChilds);

                                // Type indicator here is needed to read the array from the database
                                GenericTypeIndicator<ArrayList<Integer>> typeIndi = new GenericTypeIndicator<ArrayList<Integer>>() {
                                };
                                usedSessionIDsLocal = dataSnapshot.child("usedSessionIDs").getValue(typeIndi);
                                // delete the init of the array with item 0 because it is not part of the actual questionIDs
                                if(usedSessionIDsLocal.get(0) == 0){
                                    usedSessionIDsLocal.clear();
                                }
                                Log.d("current User", "usedSessionIDsLocal:" + usedSessionIDsLocal);
                                Log.d("current User", "usedSessionIDsLocal SIZE:" + usedSessionIDsLocal.size());

                                // Generate random question IDs within the question catalog
                                questionIDsPerRound = new int[numberQuestionsPerRound];
                                Random randomQuestionID = new Random();
                                counterRandomQuestions = 0;
                                int max = (int) countQuestionChilds;
                                Log.d("current User", "max = countQuestionChilds " + max);
                                int min = 1;
                                boolean IDchecked = true;
                                boolean IDUsedOncechecked;
                                // used for the dynamic check, if the big check could be made, if not use the small check
                                int counterNumberQuestionsPerRound = numberQuestionsPerRound;

                                while (counterRandomQuestions < numberQuestionsPerRound) {
                                    Log.d("current User", "counterRandomQuestions:" + counterRandomQuestions);
                                    // normal random function: [0, n-1], here: [1, n]
                                    // +1, because max is excluded
                                    int randID = randomQuestionID.nextInt((max - min) + 1) + min;
                                    Log.d("current User", "randID:" + randID);
                                    // Check, that it is a possible to solve problem and no endless repetition without the chance of solving the problem
                                    // dynamic check, if the big check could be made when there is a questionID left, if not use the small check
                                    if ((max - usedSessionIDsLocal.size()) >= 1) {
                                        counterNumberQuestionsPerRound --;
                                        Log.d("current User", "Big check");
                                        // Since app start asked questions should not be part of the round
                                        // randomQuestionID should not be within the user specific DB entry [array:int:usedSessionIDs]
                                        counterUsedSessionIDs = 0;
                                        Log.d("current User", "usedSessionIDsLocal SIZE:" + usedSessionIDsLocal.size());
                                        IDchecked = true;
                                        while (counterUsedSessionIDs < usedSessionIDsLocal.size()) {
                                            Log.d("current User", "counterUsedSessionIDs:" + counterUsedSessionIDs);
                                            if (usedSessionIDsLocal.get(counterUsedSessionIDs) == randID) {
                                                // do not use the randID because it was used since app start
                                                Log.d("current User", "IDchecked inside:" + IDchecked);
                                                IDchecked = false;
                                                break;
                                            }
                                            Log.d("current User", "IDchecked:" + IDchecked);
                                            counterUsedSessionIDs++;
                                        }
                                        IDUsedOncechecked = false;
                                    } else {
                                        Log.d("current User", "Small check");
                                        // every ID is okay, no checks if already used since app start
                                        // only check, if used in the round (no question more than one time per round)
                                        IDchecked = true;
                                        for (int counterCheckRound = 0; counterCheckRound < numberQuestionsPerRound; counterCheckRound++) {
                                            if (questionIDsPerRound[counterCheckRound] == randID) {
                                                IDchecked = false;
                                            }
                                        }
                                        // no need to add the questionID more than once to the usedSessionIDs
                                        IDUsedOncechecked = true;
                                    }

                                    if (true == IDchecked) {
                                        // check if the while search in the usedSessionIDsLocal was okay
                                        // a question is only one time per round allowed, so add it to the usedSessionIDsLocal to check next repeat if already used
                                        questionIDsPerRound[counterRandomQuestions] = randID;
                                        counterRandomQuestions++;
                                        // no need to add the questionID more than once to the usedSessionIDs
                                        if (false == IDUsedOncechecked) {
                                            usedSessionIDsLocal.add(randID);
                                        }

                                    }
                                }
                                Log.d("current User", "usedSessionIDs after while: " + usedSessionIDsLocal);

                                // Question DB first question child
                                actualQuestionNumber = 1;

                                actualQuestionTitle = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("questionTitle").getValue(String.class);
                                actualQuestionAnswer1 = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer1").child("answerText").getValue(String.class);
                                actualQuestionAnswer1Correct = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer2 = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer2").child("answerText").getValue(String.class);
                                actualQuestionAnswer2Correct = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer3 = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer3").child("answerText").getValue(String.class);
                                actualQuestionAnswer3Correct = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class);
                                actualQuestionAnswer4 = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer4").child("answerText").getValue(String.class);
                                actualQuestionAnswer4Correct = dataSnapshotQuestions.child(String.valueOf(questionIDsPerRound[actualQuestionNumber - 1])).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class);

                                // set the textviews with the data for the first question
                                numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
                                question_textview.setText(actualQuestionTitle);
                                answer1_textview.setText(actualQuestionAnswer1);
                                answer2_textview.setText(actualQuestionAnswer2);
                                answer3_textview.setText(actualQuestionAnswer3);
                                answer4_textview.setText(actualQuestionAnswer4);
                                confirm_next_textview.setText(getString(R.string.confirm_button));
                            } else {
                                //not found
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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });

        // set the text views with the default data
        numberQuestionsProgress_textview.setText(String.valueOf(actualQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
        confirm_next_textview.setText(getString(R.string.confirm_button));

        // L.B.: Reset all cardView background colors to white for next round
        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));

        // L.B.: Reset all cardView border strokes to zero for next round
        cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
        cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
        cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
        cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));

        answer1_choosen = false;
        answer2_choosen = false;
        answer3_choosen = false;
        answer4_choosen = false;


        //------------

        cardViewSinglePlayerAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer1_choosen = !answer1_choosen;

                    // L.B.: Visualizing selected answer 1 by toggling cardView Background color between white/grey and toggles border stroke on/off
                    if(answer1_choosen){
                        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.selectedbg));
                        cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                    }
                    else{
                        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                    }
                }
            }
        });
        cardViewSinglePlayerAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer2_choosen = !answer2_choosen;

                    // L.B.: Visualizing selected answer 2 by toggling cardView Background color between white/grey and toggles border stroke on/off
                    if(answer2_choosen){
                        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.selectedbg));
                        cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                    }
                    else{
                        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                    }
                }
            }
        });
        cardViewSinglePlayerAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer3_choosen = !answer3_choosen;

                    // L.B.: Visualizing selected answer 3 by toggling cardView Background color between white/grey and toggles border stroke on/off
                    if(answer3_choosen){
                        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.selectedbg));
                        cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                    }
                    else{
                        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                    }
                }
            }
        });
        cardViewSinglePlayerAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if in the answer choosing phase, else no action if clicked
                if (false == switchConfirmNextButton) {
                    answer4_choosen = !answer4_choosen;

                    // L.B.: Visualizing selected answer 4 by toggling cardView Background color between white/grey and toggles border stroke on/off
                    if(answer4_choosen){
                        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.selectedbg));
                        cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                    }
                    else{
                        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                    }
                }
            }
        });
        cardViewSinglePlayerSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // first click confirms the choosen answers and shows if the answers are right
                // second click (else) loads the next question or goes to the result screen
                if (false == switchConfirmNextButton) {
                    switchConfirmNextButton = true;
                    confirm_next_textview.setText(getString(R.string.next_button)); // L.B.: Changes Button after submitting answers

                    // L.B.: Checks if answer 1 has been chosen correctly and changes cardView background color accordingly to indicate right/wrong
                    if (actualQuestionAnswer1Correct == true) {
                        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.rightbg));
                        if (answer1_choosen == true) {
                            cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                        } else {
                            cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        }
                    } else {
                        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.wrongbg));
                    }

                    // L.B.: Checks if answer 2 has been chosen correctly and changes cardView background color accordingly to indicate right/wrong
                    if (actualQuestionAnswer2Correct == true) {
                        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.rightbg));
                        if (answer2_choosen == true) {
                            cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                        } else {
                            cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        }
                    } else {
                        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.wrongbg));
                    }

                    // L.B.: Checks if answer 3 has been chosen correctly and changes cardView background color accordingly to indicate right/wrong
                    if (actualQuestionAnswer3Correct == true) {
                        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.rightbg));
                        if (answer3_choosen == true) {
                            cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                        } else {
                            cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        }
                    } else {
                        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.wrongbg));
                    }

                    // L.B.: Checks if answer 4 has been chosen correctly and changes cardView background color accordingly to indicate right/wrong
                    if (actualQuestionAnswer4Correct == true) {
                        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.rightbg));
                        if (answer4_choosen == true) {
                            cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeon));
                        } else {
                            cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        }
                    } else {
                        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.wrongbg));
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

                        // L.B.: Reset all cardView background colors to white for next round
                        cardViewSinglePlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));
                        cardViewSinglePlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(SinglePlayerGameActivity.this, R.color.white));

                        // L.B.: Reset all cardView border strokes to zero for next round
                        cardViewSinglePlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        cardViewSinglePlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        cardViewSinglePlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));
                        cardViewSinglePlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(SinglePlayerGameActivity.this, R.color.strokeoff));

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
                                    confirm_next_textview.setText(getString(R.string.confirm_button));
                                }
                                else{
                                    question_textview.setText("Not found");
                                    answer1_textview.setText("Not found");
                                    answer2_textview.setText("Not found");
                                    answer3_textview.setText("Not found");
                                    answer4_textview.setText("Not found");
                                    confirm_next_textview.setText(getString(R.string.confirm_button));
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
                        Log.d("current User", "To be pushed usedSessionIDs in user DB: " + usedSessionIDsLocal);
                        usersDatabaseReference.child("usedSessionIDs").setValue(usedSessionIDsLocal, (error, ref) -> {
                            // this part works like a on-complete-listener because the data has to be pushed completely
                            Log.d("current User", "Successfully pushed usedSessionIDs in user DB: " + usedSessionIDsLocal);
                            // this is used because a too early switch to the new activity causes sometimes some data not to be pushed
                        });
                        //Pass the results of the round to the next activity (result screen)
                        Intent intent_next_activity = new Intent(SinglePlayerGameActivity.this, SinglePlayerResultActivity.class);
                        intent_next_activity.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                        intent_next_activity.putExtra("numberCorrectAnswersRound", numberCorrectAnswersRound);
                        startActivity(intent_next_activity);
                    }
                }
            }
        });
    }
}