package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import thu.adse.energyquiz.R;


public class MultiPlayerGameActivity extends AppCompatActivity {


    DatabaseReference lobbyDbRef,usersDbRef, dbRef, questionsDbRef;
    ArrayList<Long> questionIDsForThisRound=new ArrayList<>();
    String player1ID,player2ID, questionTitle,answer1,answer2,answer3,answer4;
    private TextView textViewNumberQuestionsProgress, textViewQuestion, textViewAnswer1, textViewAnswer2, textViewAnswer3, textViewAnswer4, textViewConfirmNext;
    private MaterialCardView cardViewMultiPlayerAnswer1, cardViewMultiPlayerAnswer2, cardViewMultiPlayerAnswer3, cardViewMultiPlayerAnswer4, cardViewMultiPlayerSubmitAnswer, cardViewMultiPlayerGameBack;
    private boolean answer1Chosen, answer2Chosen, answer3Chosen, answer4Chosen, switchConfirmNextButton;
    boolean answer1IsCorrect,answer2IsCorrect,answer3IsCorrect,answer4IsCorrect, creatorIsLoggedIn, playerIsFinished;
    ;
    MultiPlayerLobby selectedLobby;
    FirebaseUser currentUser;
    FirebaseAuth auth;
    int numberCorrectAnswersRound=0, currentQuestionNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_game);

        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");
        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef=FirebaseDatabase.getInstance().getReference();
        questionsDbRef=FirebaseDatabase.getInstance().getReference().child("Questions");
        auth = FirebaseAuth.getInstance();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkIfCreatorIsLoggedIn(snapshot);
                //player1ID=getIntent().getStringExtra("lobbyname");
                //player2ID=snapshot.child("full").child(player1ID).child("userIDPlayer2").getValue(String.class);

                //checkIfCreatorIsLoggedIn(selectedLobby,snapshot);

                for (DataSnapshot dataSnapshot: snapshot.child("Lobbies").child("full").child(player1ID).child("questionsForThisRound").getChildren()) {
                    questionIDsForThisRound.add(dataSnapshot.getValue(Long.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switchConfirmNextButton=false;
        textViewNumberQuestionsProgress =findViewById(R.id.textViewNumberQuestionsProgress);
        textViewQuestion=findViewById(R.id.textViewQuestion);
        textViewAnswer1=findViewById(R.id.textViewAnswer1);
        textViewAnswer2=findViewById(R.id.textViewAnswer2);
        textViewAnswer3=findViewById(R.id.textViewAnswer3);
        textViewAnswer4=findViewById(R.id.textViewAnswer4);
        textViewConfirmNext=findViewById(R.id.textViewConfirmNext);

        cardViewMultiPlayerAnswer1=findViewById(R.id.cardViewMultiPlayerAnswer1);
        cardViewMultiPlayerAnswer2=findViewById(R.id.cardViewMultiPlayerAnswer2);
        cardViewMultiPlayerAnswer3=findViewById(R.id.cardViewMultiPlayerAnswer3);
        cardViewMultiPlayerAnswer4=findViewById(R.id.cardViewMultiPlayerAnswer4);
        cardViewMultiPlayerSubmitAnswer=findViewById(R.id.cardViewMultiPlayerSubmitAnswer);
        //cardViewMultiPlayerGameBack=findViewById(R.id.cardViewMultiPlayerGameBack);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentQuestionNumber=1;

                getQuestionsFromDB(snapshot, currentQuestionNumber-1);
                loadQuestion(currentQuestionNumber);
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setDefaultColors();
        answer1Chosen = false;
        answer2Chosen = false;
        answer3Chosen = false;
        answer4Chosen = false;
        cardViewMultiPlayerAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer1ChosenCheck();
            }
        });

        cardViewMultiPlayerAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer2ChosenCheck();
            }
        });

        cardViewMultiPlayerAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer3ChosenCheck();
            }
        });

        cardViewMultiPlayerAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer4ChosenCheck();
            }
        });

        cardViewMultiPlayerSubmitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChosenAnswers();

            }
        });
        /*
        cardViewMultiPlayerGameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abortGame(); //FIXME: Spielabrruch funktioniert nicht. Warum?
            }
        });*/


    }

    public void getQuestionsFromDB(DataSnapshot snapshot, int i){
        questionTitle=snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("questionTitle").getValue(String.class);
        answer1=snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer1").child("answerText").getValue(String.class);
        answer1IsCorrect=Boolean.TRUE.equals(snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class));

        answer2=snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer2").child("answerText").getValue(String.class);
        answer2IsCorrect=Boolean.TRUE.equals(snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class));

        answer3=snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer3").child("answerText").getValue(String.class);
        answer3IsCorrect=Boolean.TRUE.equals(snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class));

        answer4=snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer4").child("answerText").getValue(String.class);
        answer4IsCorrect=Boolean.TRUE.equals(snapshot.child("Questions").child(String.valueOf(questionIDsForThisRound.get(i))).child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class));


    }
    public void loadQuestion(int currentQuestionNumber){
        textViewNumberQuestionsProgress.setText(Integer.toString(currentQuestionNumber) + " / " + Integer.toString(questionIDsForThisRound.size()));
        textViewQuestion.setText(questionTitle);
        textViewAnswer1.setText(answer1);
        textViewAnswer2.setText(answer2);
        textViewAnswer3.setText(answer3);
        textViewAnswer4.setText(answer4);
        textViewConfirmNext.setText(getString(R.string.confirm_button));
    }

    private void answer1ChosenCheck(){
        if (!switchConfirmNextButton){
            answer1Chosen =!answer1Chosen;
            if (answer1Chosen){
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }

    private void answer2ChosenCheck(){
        if (!switchConfirmNextButton){
            answer2Chosen =!answer2Chosen;
            if (answer2Chosen){
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }

    private void answer3ChosenCheck(){
        if (!switchConfirmNextButton){
            answer3Chosen =!answer3Chosen;
            if (answer3Chosen){
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }

    private void answer4ChosenCheck(){
        if (!switchConfirmNextButton){
            answer4Chosen =!answer4Chosen;
            if (answer4Chosen){
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.selectedbg));
                cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeon));
            } else {
                cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
                cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.strokeoff));
            }
        }
    }
    
    
    private void confirmChosenAnswers(){
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
            if(answer1Chosen==answer1IsCorrect & answer2Chosen==answer2IsCorrect & answer3Chosen==answer3IsCorrect & answer4Chosen==answer4IsCorrect){
                numberCorrectAnswersRound=numberCorrectAnswersRound+1;
            }
        }else {
            switchConfirmNextButton = false;

            if (currentQuestionNumber <= questionIDsForThisRound.size()) {
                currentQuestionNumber++;

                textViewNumberQuestionsProgress.setText(String.valueOf(currentQuestionNumber) + " / " + String.valueOf(questionIDsForThisRound.size()));
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

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        getQuestionsFromDB(snapshot, currentQuestionNumber-1);
                        loadQuestion(currentQuestionNumber);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else { //FIXME: Wird nie betreten. Warum?

                playerIsFinished=true;
                lobbyDbRef.child(player1ID).child(auth.getCurrentUser().getUid() + "isFinished").setValue(playerIsFinished);
                Intent resultScreenIntent = new Intent(MultiPlayerGameActivity.this, MultiPlayerResultActivity.class);
                resultScreenIntent.putExtra("numberQuestionsPerRound", questionIDsForThisRound.size());
                resultScreenIntent.putExtra("player1ID", player1ID);
                resultScreenIntent.putExtra("player2ID", player2ID);
                resultScreenIntent.putExtra("numberCorrectAnswersRound",numberCorrectAnswersRound);
                resultScreenIntent.putExtra("creatorIsLoggedIn", creatorIsLoggedIn);
                resultScreenIntent.putExtra("usedQuestionIDs", questionIDsForThisRound);

                writeCorrectAnswersInDB();

                startActivity(resultScreenIntent); //FIXME: loading Screen
            }
        }


    }
    public void checkIfCreatorIsLoggedIn(DataSnapshot snapshot){
        currentUser = auth.getCurrentUser();
        Bundle extras = getIntent().getExtras();
        String lobbyname;
        if(extras == null) {
            lobbyname= currentUser.getUid();
        } else {
            lobbyname= extras.getString("lobbyname");
        }
        if (!currentUser.getUid().equals(lobbyname)) { //Creator is logged in
            player1ID=lobbyname;
            creatorIsLoggedIn=true;
        } else { //Joined User is logged in bc lobbyname exists
            player1ID=currentUser.getUid();
            creatorIsLoggedIn=false;
        }
        player2ID=snapshot.child("Lobbies").child("full").child(player1ID).child("userIDPlayer2").getValue(String.class);


    }
    
    public void abortGame(){
        lobbyDbRef.child("full").child(player1ID).removeValue();
        Intent abortGameIntent =new Intent(MultiPlayerGameActivity.this, MultiPlayerLobbyScreen.class);
        startActivity(abortGameIntent);
    }

    private void setDefaultColors(){
        cardViewMultiPlayerAnswer1.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer2.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer3.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));
        cardViewMultiPlayerAnswer4.setCardBackgroundColor(ContextCompat.getColor(MultiPlayerGameActivity.this, R.color.white));

        cardViewMultiPlayerAnswer1.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer2.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer3.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
        cardViewMultiPlayerAnswer4.setStrokeColor(ContextCompat.getColorStateList(MultiPlayerGameActivity.this, R.color.transparent));
    }

    private void writeCorrectAnswersInDB(){
        if (creatorIsLoggedIn){
            lobbyDbRef.child("full").child(player1ID).child("correctAnswersPlayer1").setValue(numberCorrectAnswersRound);

        } else if (!creatorIsLoggedIn) {
            lobbyDbRef.child("full").child(player1ID).child("correctAnswersPlayer2").setValue(numberCorrectAnswersRound);

        }
    }



}