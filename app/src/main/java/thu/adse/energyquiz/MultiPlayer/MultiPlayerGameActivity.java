package thu.adse.energyquiz.MultiPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import thu.adse.energyquiz.R;

/**
 * This class is responsible for the game logic of the multiplayer game.
 * @author Sebastian Steinhauser
 */

public class MultiPlayerGameActivity extends AppCompatActivity {

    DatabaseReference lobbyDbRef, usersDbRef, dbRef, questionsDbRef;
    ArrayList<Long> questionIDsForThisRound = new ArrayList<>();
    String player1ID, player2ID, questionTitle, answer1, answer2, answer3, answer4;
    private TextView textViewNumberQuestionsProgress, textViewQuestion, textViewAnswer1, textViewAnswer2, textViewAnswer3, textViewAnswer4, textViewConfirmNext;
    private MaterialCardView cardViewMultiPlayerAnswer1, cardViewMultiPlayerAnswer2, cardViewMultiPlayerAnswer3, cardViewMultiPlayerAnswer4, cardViewMultiPlayerSubmitAnswer;
    private CardView  cardViewMultiPlayerGameBack;
    private boolean answer1Chosen, answer2Chosen, answer3Chosen, answer4Chosen, switchConfirmNextButton;
    boolean answer1IsCorrect, answer2IsCorrect, answer3IsCorrect, answer4IsCorrect, creatorIsLoggedIn;
    public static boolean bothPlayersFinished = false, abortGame=false;
    FirebaseUser currentUser;
    FirebaseAuth auth;
    int numberCorrectAnswersRound = 0, currentQuestionNumber, numberQuestionsPerRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MultiplayerGameActivity", "onCreate: MultiplayerHasStarted");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_game);

        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");
        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef = FirebaseDatabase.getInstance().getReference();
        questionsDbRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        auth = FirebaseAuth.getInstance();


        dbRef.child("Lobbies").get().addOnCompleteListener(task -> {
            DataSnapshot snapshot = task.getResult();
            checkIfCreatorIsLoggedIn(snapshot);

            for (DataSnapshot dataSnapshot : snapshot.child("full").child(player1ID).child("questionsForThisRound").getChildren()) {
                questionIDsForThisRound.add(dataSnapshot.getValue(Long.class));
            }
            numberQuestionsPerRound = questionIDsForThisRound.size();
        });


        switchConfirmNextButton = false;
        textViewNumberQuestionsProgress = findViewById(R.id.textViewNumberQuestionsProgress);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewAnswer1 = findViewById(R.id.textViewAnswer1);
        textViewAnswer2 = findViewById(R.id.textViewAnswer2);
        textViewAnswer3 = findViewById(R.id.textViewAnswer3);
        textViewAnswer4 = findViewById(R.id.textViewAnswer4);
        textViewConfirmNext = findViewById(R.id.textViewConfirmNext);

        cardViewMultiPlayerAnswer1 = findViewById(R.id.cardViewMultiPlayerAnswer1);
        cardViewMultiPlayerAnswer2 = findViewById(R.id.cardViewMultiPlayerAnswer2);
        cardViewMultiPlayerAnswer3 = findViewById(R.id.cardViewMultiPlayerAnswer3);
        cardViewMultiPlayerAnswer4 = findViewById(R.id.cardViewMultiPlayerAnswer4);
        cardViewMultiPlayerSubmitAnswer = findViewById(R.id.cardViewMultiPlayerSubmitAnswer);


        dbRef.child("Questions").get().addOnCompleteListener(task -> {

            if (!bothPlayersFinished) {
                DataSnapshot snapshot = task.getResult();
                currentQuestionNumber = 1;
                getQuestionsFromDB(snapshot, (currentQuestionNumber - 1));
                loadQuestion(currentQuestionNumber);
            }

        });


        setDefaultColors();
        answer1Chosen = false;
        answer2Chosen = false;
        answer3Chosen = false;
        answer4Chosen = false;
        cardViewMultiPlayerAnswer1.setOnClickListener(v -> answer1ChosenCheck());

        cardViewMultiPlayerAnswer2.setOnClickListener(v -> answer2ChosenCheck());

        cardViewMultiPlayerAnswer3.setOnClickListener(v -> answer3ChosenCheck());

        cardViewMultiPlayerAnswer4.setOnClickListener(v -> answer4ChosenCheck());

        cardViewMultiPlayerSubmitAnswer.setOnClickListener(v -> confirmChosenAnswers());

        cardViewMultiPlayerGameBack=findViewById(R.id.cardViewMultiPlayerGameBack);

        cardViewMultiPlayerGameBack.setOnClickListener(v -> {
            Log.d("MultiplayerGameActivity", "onClick: MultiplayerGameBack reached");


            abortGame();
        });
    }

    /**
     * Retrieves questions and answers from the database.
     * @author Sebastian Steinhauser
     *
     * @param snapshot The snapshot of the database.
     * @param i Index of the question.
     */
    public void getQuestionsFromDB(DataSnapshot snapshot, int i) {
        questionTitle = snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("questionTitle").getValue(String.class);
        answer1 = snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer1").child("answerText").getValue(String.class);
        answer1IsCorrect = Boolean.TRUE.equals(snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class));

        answer2 = snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer2").child("answerText").getValue(String.class);
        answer2IsCorrect = Boolean.TRUE.equals(snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class));

        answer3 = snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer3").child("answerText").getValue(String.class);
        answer3IsCorrect = Boolean.TRUE.equals(snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class));

        answer4 = snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer4").child("answerText").getValue(String.class);
        answer4IsCorrect = Boolean.TRUE.equals(snapshot.child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class));

    }

    /**
     * Loads the current question into the UI.
     * The number of the current question is displayed.
     * @author Sebastian Steinhauser
     *
     * @param currentQuestionNumber The number of the current question.
     */
    public void loadQuestion(int currentQuestionNumber) {
        textViewNumberQuestionsProgress.setText(Integer.toString(currentQuestionNumber) + " / " + Integer.toString(numberQuestionsPerRound));
        textViewQuestion.setText(questionTitle);
        textViewAnswer1.setText(answer1);
        textViewAnswer2.setText(answer2);
        textViewAnswer3.setText(answer3);
        textViewAnswer4.setText(answer4);
        textViewConfirmNext.setText(getString(R.string.confirm_button));
    }

    /**
     * Checks if the first answer is chosen and changes the color of the card view accordingly.
     * @author Sebastian Steinhauser
     */
    private void answer1ChosenCheck() {
        if (!switchConfirmNextButton) {
            answer1Chosen = !answer1Chosen;
            if (answer1Chosen) {
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }
    /**
     * Checks if the second answer is chosen and changes the color of the card view accordingly.
     * @author Sebastian Steinhauser
     */
    private void answer2ChosenCheck() {
        if (!switchConfirmNextButton) {
            answer2Chosen = !answer2Chosen;
            if (answer2Chosen) {
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }

    /**
     * Checks if the third answer is chosen and changes the color of the card view accordingly.
     * @author Sebastian Steinhauser
     */
    private void answer3ChosenCheck() {
        if (!switchConfirmNextButton) {
            answer3Chosen = !answer3Chosen;
            if (answer3Chosen) {
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }

    /**
     * Checks if the fourth answer is chosen and changes the color of the card view accordingly.
     * @author Sebastian Steinhauser
     */
    private void answer4ChosenCheck() {
        if (!switchConfirmNextButton) {
            answer4Chosen = !answer4Chosen;
            if (answer4Chosen) {
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }


    /**
     * Confirms the chosen answers and changes the color of the card views accordingly.
     * If all questions are answered, the waiting screen is started.
     * If not, the next question is loaded.
     * The number of correct answers is counted and written into the database.
     *
     * @author Sebastian Steinhauser
     */
    private void confirmChosenAnswers() {
        if (!switchConfirmNextButton) {
            switchConfirmNextButton = true;
            textViewConfirmNext.setText(getString(R.string.next_button));

            if (answer1IsCorrect) {
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.rightbg));
                if (answer1Chosen) {
                    cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
                } else {
                    cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                }
            } else {
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.wrongbg));
            }

            if (answer2IsCorrect) {
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.rightbg));
                if (answer2Chosen) {
                    cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
                } else {
                    cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                }
            } else {
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.wrongbg));
            }

            if (answer3IsCorrect) {
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.rightbg));
                if (answer3Chosen) {
                    cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
                } else {
                    cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                }
            } else {
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.wrongbg));
            }

            if (answer4IsCorrect) {
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.rightbg));
                if (answer4Chosen) {
                    cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
                } else {
                    cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                }
            } else {
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.wrongbg));
            }
            if (answer1Chosen == answer1IsCorrect & answer2Chosen == answer2IsCorrect & answer3Chosen == answer3IsCorrect & answer4Chosen == answer4IsCorrect) {
                numberCorrectAnswersRound = numberCorrectAnswersRound + 1;
            }
        } else {
            switchConfirmNextButton = false;
            if (currentQuestionNumber < numberQuestionsPerRound) {
                currentQuestionNumber++;

                textViewNumberQuestionsProgress.setText(String.valueOf(currentQuestionNumber) + " / " + String.valueOf(numberQuestionsPerRound));
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));

                cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
                cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));

                answer1Chosen = false;
                answer2Chosen = false;
                answer3Chosen = false;
                answer4Chosen = false;

                dbRef.child("Questions").get().addOnCompleteListener(task -> {
                    DataSnapshot snapshot = task.getResult();
                    getQuestionsFromDB(snapshot, (currentQuestionNumber - 1));
                    loadQuestion(currentQuestionNumber);

                });
            } else {

                MultiPlayerWaitingScreenAlert waitingAlert = new MultiPlayerWaitingScreenAlert(MultiPlayerGameActivity.this);

                Log.d("Waiting Screen", "confirmChosenAnswers: waiting Screen is started");
                writeCorrectAnswersInDB();
                waitingAlert.startWaitingScreenAlertDialog(player1ID, player2ID, MultiPlayerGameActivity.this, numberQuestionsPerRound, numberCorrectAnswersRound, creatorIsLoggedIn, questionIDsForThisRound);
            }
        }
    }


    /**
     * Checks if the creator is logged in and retrieves the player IDs from the database.
     * @author Sebastian Steinhauser
     *
     * @param snapshot The snapshot of the database.
     */
    public void checkIfCreatorIsLoggedIn(DataSnapshot snapshot) {
        currentUser = auth.getCurrentUser();
        Bundle extras = getIntent().getExtras();
        String lobbyName;
        if (extras == null) {
            lobbyName = currentUser.getUid();
        } else {
            lobbyName = extras.getString("lobbyname");
        }
        if (!currentUser.getUid().equals(lobbyName)) {
            player1ID = lobbyName;
            creatorIsLoggedIn = false;
        } else {
            player1ID = currentUser.getUid();
            creatorIsLoggedIn = true;
        }
        player2ID = snapshot.child("full").child(player1ID).child("userIDPlayer2").getValue(String.class);


    }

    /**
     * Aborts the game and returns to the lobby screen.
     * The game progress is not saved.
     * The user who is aborting is informed about his actions.
     * @author Sebastian Steinhauser
     */
    private void abortGame() {
        abortGame=true;
        lobbyDbRef.child("full").child(player1ID).child("abortGame").setValue(abortGame);
        Intent abortGameIntent = new Intent(MultiPlayerGameActivity.this, MultiPlayerLobbyScreen.class);
        Toast.makeText(MultiPlayerGameActivity.this, "Spiel abgebrochen. Fortschritt nicht gepseichert!", Toast.LENGTH_SHORT).show();
        Log.d("AbortGame", "Player Aborted the Game");
        startActivity(abortGameIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
    }

    /**
     * Sets the default colors of the card views.
     * @author Sebastian Steinhauser
     */
    private void setDefaultColors() {
        cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));

        cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
    }

    /**
     * Writes the number of correct answers into the database
     * @author Sebastian Steinhauser
     */
    private void writeCorrectAnswersInDB() {
        if (creatorIsLoggedIn) {
            lobbyDbRef.child("full").child(player1ID).child("correctAnswersPlayer1").setValue(numberCorrectAnswersRound);

        } else if (!creatorIsLoggedIn) {
            lobbyDbRef.child("full").child(player1ID).child("correctAnswersPlayer2").setValue(numberCorrectAnswersRound);

        }
    }
}