package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;

public class MultiPlayerLobbyScreen extends AppCompatActivity implements RecyclerViewInterfaceMultiPlayerLobby {

    DatabaseReference lobbyDbRef,usersDbRef, dbRef;
    RecyclerView recyclerViewLobbyscreen;
    MultiPlayerLobbyAdapter lobbyAdapter;
    ArrayList<MultiPlayerLobby> lobbyList;
    Button buttonCreateLobby, buttonBackToHome, buttonDialogYes, buttonDialogNo;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser JoinedUser;

     MultiPlayerLobby selectedLobby;
     String joinedUserID, userIDCreator;

    ArrayList<Long> possibleQuestions = new ArrayList<>(),usedQuestions = new ArrayList<>(),allQuestions = new ArrayList<>(),possibleQuestions2Players=new ArrayList<>(), randomizedQuestions = new ArrayList<>(), questionIDsForThisRound = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_lobby_screen);
        recyclerViewLobbyscreen = findViewById(R.id.recyclerViewLobbyscreen);

        recyclerViewLobbyscreen.setHasFixedSize(true);
        recyclerViewLobbyscreen.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();

        JoinedUser=auth.getCurrentUser();
        joinedUserID=JoinedUser.getUid();


        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");
        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef=FirebaseDatabase.getInstance().getReference();





        buttonCreateLobby = findViewById(R.id.buttonCreateLobby);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        dialog=new Dialog(MultiPlayerLobbyScreen.this);
        dialog.setContentView(R.layout.multi_player_lobby_screen_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_delete_dialog_drawable_question_catalog));
        dialog.setCancelable(false);

        buttonDialogYes = dialog.findViewById(R.id.buttonDialogYes);
        buttonDialogNo = dialog.findViewById(R.id.buttonDialogNo);



        buttonCreateLobby.setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiPlayerStartActivity.class);
            startActivity(intent);
        });

        buttonBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        lobbyList=new ArrayList<>();
        lobbyAdapter = new MultiPlayerLobbyAdapter(this, this, lobbyList);
        recyclerViewLobbyscreen.setAdapter(lobbyAdapter);





        lobbyDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lobbyList.clear();

                for (DataSnapshot lobbysnapshot: snapshot.child("open").getChildren()) {
                    for (DataSnapshot questionsnapshot: lobbysnapshot.child("possibleQuestions").getChildren()) {
                        possibleQuestions.add(questionsnapshot.getValue(Long.class));
                    }
                    MultiPlayerLobby lobby = new MultiPlayerLobby(lobbysnapshot.child("numberQuestionsPerRound").getValue(String.class), lobbysnapshot.child("userNameCreator").getValue(String.class), lobbysnapshot.getKey(), possibleQuestions);
                    lobbyList.add(lobby);
                }
                lobbyAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            getUsedQuestionsFromUserDb(snapshot);
            // Falls der Username des 2. Spielers benötigt wird, kann diese Abfrage hier über die User DB stattfinden.

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });





    }

    @Override
    public void onItemClick(int position, MultiPlayerLobby lobby) {
        selectedLobby = lobby;
        dialog.show();

        buttonDialogNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        buttonDialogYes.setOnClickListener(view -> {
            moveLobbyToFull(selectedLobby);
            startActivity(new Intent(MultiPlayerLobbyScreen.this, MultiPlayerGameActivity.class).putExtra("lobbyname",selectedLobby.userIDCreator));
        });

    }

    void moveLobbyToFull(MultiPlayerLobby lobby) {
        JoinedUser = auth.getCurrentUser();
        lobbyDbRef.child("full").child(lobby.userIDCreator).child("numberQuestionsPerRound").setValue(lobby.numberQuestionsPerRound);
        lobbyDbRef.child("full").child(lobby.userIDCreator).child("userNameCreator").setValue(lobby.userNameCreator);
        lobbyDbRef.child("full").child(lobby.userIDCreator).child("userIDPlayer2").setValue(JoinedUser.getUid());
        getPossibleQuestions(lobby.possibleQuestionsList);
        lobbyDbRef.child("full").child(lobby.userIDCreator).child("possibleQuestions").setValue(possibleQuestions2Players);
        getRandomizedQuestions(lobby.numberQuestionsPerRound);
        lobbyDbRef.child("full").child(lobby.userIDCreator).child("questionsForThisRound").setValue(questionIDsForThisRound);
        userIDCreator=lobby.userIDCreator;
        lobbyDbRef.child("open").child(lobby.userIDCreator).removeValue();

    }

    private void getUsedQuestionsFromUserDb(DataSnapshot snapshot){
        usedQuestions.clear();
        for (DataSnapshot ds: snapshot.child("Users").child(joinedUserID).child("usedSessionIDs").getChildren()){
            usedQuestions.add(Long.parseLong(ds.getValue().toString()));
        }
    }
    private void getPossibleQuestions(ArrayList<Long> possibleQuestions){
        possibleQuestions2Players=possibleQuestions;
        for (Long i: usedQuestions){
            possibleQuestions2Players.remove(i);
        }
    }
    public static void deletePossibleLobbyEntries(){
        FirebaseUser currentUser;
        DatabaseReference lobbyDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Lobbies");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        String currentUserID = currentUser.getUid();

        lobbyDbRef.child("open").child(currentUserID).removeValue();

        lobbyDbRef.child("full").child(currentUserID).removeValue();

    }

    public void getRandomizedQuestions(String numberQuestionsPerRound){
        randomizedQuestions.clear();
        randomizedQuestions=possibleQuestions2Players;
        Collections.shuffle(randomizedQuestions);

        for (int i = 0; i < Integer.parseInt(numberQuestionsPerRound); i++) {
               questionIDsForThisRound.add(randomizedQuestions.get(i));
        }
    }
}