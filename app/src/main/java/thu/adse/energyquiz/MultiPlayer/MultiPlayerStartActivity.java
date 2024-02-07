package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thu.adse.energyquiz.R;

public class MultiPlayerStartActivity extends AppCompatActivity {

    private int numberQuestionsPerRound;
    FirebaseAuth auth;
    DatabaseReference lobbyDbRef, usersDbRef, questionsDbRef, dbRef;
    FirebaseUser UserCreator;
    public String userNameCreator, userIdCreator;
    List<Long> possibleQuestions = new ArrayList<>();
    List<Long> usedQuestions = new ArrayList<>();
    List<Long> allQuestions = new ArrayList<>();
    boolean playerJoined,gameHasStarted;
    private ValueEventListener lobbyEventListener, dbEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_start);
        CardView cardViewMultiPlayerStartBack = findViewById(R.id.cardViewMultiPlayerStartBack);
        CardView cardViewMultiPlayerStartPlayButton = findViewById(R.id.cardViewMultiPlayerStartPlayButton);
        CardView cardViewMultiPlayerStartPlus = findViewById(R.id.cardViewMultiPlayerStartPlus);
        CardView cardViewMultiPlayerStartMinus = findViewById(R.id.cardViewMultiPlayerStartMinus);
        TextView TextViewMultiPlayerStartNumberInput = findViewById(R.id.TextViewMultiPlayerStartNumberInput);

        lobbyDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Lobbies");
        usersDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users");
        questionsDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Questions");
        dbRef=FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        auth = FirebaseAuth.getInstance();

        UserCreator = auth.getCurrentUser();
        userIdCreator = UserCreator.getUid();

        cardViewMultiPlayerStartBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiPlayerLobbyScreen.class);
            startActivity(intent);
        });

        numberQuestionsPerRound = 5; //Standardwert Fragen pro Runde wird hier festgelegt

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberQuestionsPerRound = extras.getInt("numberQuestionsPerRound");
            TextViewMultiPlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
        }
        cardViewMultiPlayerStartPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberQuestionsPerRound++;
                TextViewMultiPlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
            }
        });

        cardViewMultiPlayerStartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberQuestionsPerRound > 1) {
                    numberQuestionsPerRound--;
                    TextViewMultiPlayerStartNumberInput.setText(String.valueOf(numberQuestionsPerRound));
                }
            }
        });


        cardViewMultiPlayerStartPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewLobby();
                MultiPlayerCancelLobbyLoadingScreenAlert loadingScreenAlert = new MultiPlayerCancelLobbyLoadingScreenAlert(MultiPlayerStartActivity.this);
                loadingScreenAlert.startLoadingScreenAlertDialog();
            }
        });

        dbRef.addListenerForSingleValueEvent(dbEventListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userNameCreator = snapshot.child("Users").child(userIdCreator).child("userName").getValue(String.class);
                    getUsedQuestionsFromUserDb(snapshot);
                    getAllQuestionIDs(snapshot.child("Questions"));
                    dbRef.removeEventListener(dbEventListener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
            lobbyDbRef.addValueEventListener(lobbyEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (checkIfPlayerJoined(snapshot)){
                        Log.d("MultiplayerStartActivity", "onDataChange: startMultiPlayerGameNow");
                        gameHasStarted =true;
                        lobbyDbRef.removeEventListener(lobbyEventListener);
                        startActivity(new Intent(MultiPlayerStartActivity.this, MultiPlayerGameActivity.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    private void createNewLobby() {
        String numberQuesttionsPerRoundString= String.valueOf(numberQuestionsPerRound);
        lobbyDbRef.child("open").child(userIdCreator).push();
        lobbyDbRef.child("open").child(userIdCreator).child("numberQuestionsPerRound").setValue(numberQuesttionsPerRoundString);
        lobbyDbRef.child("open").child(userIdCreator).child("userNameCreator").setValue(userNameCreator);
        getPossibleQuestions();
        Map<String, Long> questionsMap = new HashMap<>();
        for (Long i : possibleQuestions) {
            questionsMap.put(String.valueOf(i), i);
        }

        lobbyDbRef.child("open").child(userIdCreator).child("possibleQuestions").setValue(questionsMap);

    }

    private void getAllQuestionIDs(DataSnapshot snapshot) {
        for (DataSnapshot ds: snapshot.getChildren()){
            allQuestions.add(Long.parseLong(ds.getKey()));
        }

        possibleQuestions = allQuestions;

    }

    private void getUsedQuestionsFromUserDb(DataSnapshot snapshot){
        for (DataSnapshot ds: snapshot.child("Users").child(userIdCreator).child("usedSessionIDs").getChildren()){
            usedQuestions.add(Long.parseLong(ds.getValue().toString()));
        }
    }

    private void getPossibleQuestions(){
        for (Long i: usedQuestions){
            possibleQuestions.remove(i);
        }
    }

    private boolean checkIfPlayerJoined(DataSnapshot snapshot){
        playerJoined = false;
        if (snapshot.child("full").child(userIdCreator).exists()){
            playerJoined=true;
        }
        return playerJoined;
    }
}
