package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
//import android.widget.Button;

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

/**
 * This class is the MultiPlayerLobbyScreen class. It is used to display the lobbies for the multiplayer game and to join a lobby.
 * It also sets the necessary values for the game to start and moves the lobby from open to full.
 * @author Sebastian Steinhauser
 */

public class MultiPlayerLobbyScreen extends AppCompatActivity implements RecyclerViewInterfaceMultiPlayerLobby {

    DatabaseReference lobbyDbRef,usersDbRef, dbRef;
    RecyclerView recyclerViewLobbyscreen;
    MultiPlayerLobbyAdapter lobbyAdapter;
    ArrayList<MultiPlayerLobby> lobbyList;
    CardView cardViewMultiPlayerLobbyBack, cardViewMultiPlayerLobbyCreateGame, cardViewPopUpLobbyJoinYes, cardViewPopUpLobbyJoinNo;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser JoinedUser;

     MultiPlayerLobby selectedLobby;
     String joinedUserID, userIDCreator;

    ArrayList<Long> possibleQuestions = new ArrayList<>(),usedQuestions = new ArrayList<>(),possibleQuestions2Players=new ArrayList<>(), randomizedQuestions = new ArrayList<>(), questionIDsForThisRound = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_lobby);
        recyclerViewLobbyscreen = findViewById(R.id.recyclerViewLobbyscreen);

        recyclerViewLobbyscreen.setHasFixedSize(true);
        recyclerViewLobbyscreen.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();

        JoinedUser=auth.getCurrentUser();
        joinedUserID=JoinedUser.getUid();


        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");
        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef=FirebaseDatabase.getInstance().getReference();





        cardViewMultiPlayerLobbyCreateGame = findViewById(R.id.cardViewMultiPlayerLobbyCreateGame);
        cardViewMultiPlayerLobbyBack = findViewById(R.id.cardViewMultiPlayerLobbyBack);

        dialog=new Dialog(MultiPlayerLobbyScreen.this);
        dialog.setContentView(R.layout.dialog_multi_player_join_game);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        cardViewPopUpLobbyJoinYes = dialog.findViewById(R.id.cardViewPopUpLobbyJoinYes);
        cardViewPopUpLobbyJoinNo = dialog.findViewById(R.id.cardViewPopUpLobbyJoinNo);



        cardViewMultiPlayerLobbyCreateGame.setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiPlayerStartActivity.class);
            startActivity(intent);
        });

        cardViewMultiPlayerLobbyBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
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

        cardViewPopUpLobbyJoinNo.setOnClickListener(view -> dialog.dismiss());

        cardViewPopUpLobbyJoinYes.setOnClickListener(view -> {
            moveLobbyToFull(selectedLobby);
            startActivity(new Intent(MultiPlayerLobbyScreen.this, MultiPlayerGameActivity.class).putExtra("lobbyname",selectedLobby.userIDCreator));
        });

    }

    /**
     * This method moves the lobby from open to full and sets the necessary values for the game to start
     * @author Sebastian Steinhauser
     *
     * @param lobby The lobby is moved from open to full
     */
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
        lobbyDbRef.child("full").child(userIDCreator).child(userIDCreator+"isFinished").setValue(false);
        lobbyDbRef.child("full").child(userIDCreator).child(JoinedUser.getUid()+"isFinished").setValue(false);
        lobbyDbRef.child("full").child(userIDCreator).child("abortGame").setValue(false);
        lobbyDbRef.child("open").child(lobby.userIDCreator).removeValue();

    }

    /**
     * This method gets the used questions from the user database and stores them in an array list
     * @author Sebastian Steinhauser
     *
     * @param snapshot The snapshot of the user database
     */
    private void getUsedQuestionsFromUserDb(DataSnapshot snapshot){
        usedQuestions.clear();
        for (DataSnapshot ds: snapshot.child("Users").child(joinedUserID).child("usedSessionIDs").getChildren()){
            usedQuestions.add(Long.parseLong(ds.getValue().toString()));
        }
    }

    /**
     * This method gets the possible questions for the game and stores them in an array list. It also removes the used questions from the possible questions list
     * @author Sebastian Steinhauser
     *
     * @param possibleQuestions The possible questions for the game
     */
    private void getPossibleQuestions(ArrayList<Long> possibleQuestions){
        possibleQuestions2Players=possibleQuestions;
        for (Long i: usedQuestions){
            possibleQuestions2Players.remove(i);
        }
    }

    /**
     * This method deletes the possible lobby entries from the database.
     * It is called when the user is joining the lobbyScreen again after a game has finished.
     * @author Sebastian Steinhauser
     */
    public static void deletePossibleLobbyEntries(){
        FirebaseUser currentUser;
        DatabaseReference lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        String currentUserID = currentUser.getUid();

        lobbyDbRef.child("open").child(currentUserID).removeValue();
        lobbyDbRef.child("full").child(currentUserID).removeValue();

    }

    /**
     * This method gets the randomized questions IDs for the game and stores them in an array list.
     * @author Sebastian Steinhauser
     *
     * @param numberQuestionsPerRound The number of questions per round
     */
    public void getRandomizedQuestions(String numberQuestionsPerRound){
        randomizedQuestions.clear();
        randomizedQuestions=possibleQuestions2Players;
        Collections.shuffle(randomizedQuestions);

        for (int i = 0; i < Integer.parseInt(numberQuestionsPerRound); i++) {
               questionIDsForThisRound.add(randomizedQuestions.get(i));
        }
    }
}