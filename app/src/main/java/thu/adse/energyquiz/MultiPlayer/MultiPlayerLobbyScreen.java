package thu.adse.energyquiz.MultiPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import thu.adse.energyquiz.HomeScreenActivity;
import thu.adse.energyquiz.R;

public class MultiPlayerLobbyScreen extends AppCompatActivity implements RecyclerViewInterfaceMultiPlayerLobby {

    DatabaseReference lobbyDbRef;
    RecyclerView recyclerViewLobbyscreen;
    MultiPlayerLobbyAdapter lobbyAdapter;
    ArrayList<MultiPlayerLobby> lobbyList;
    Button buttonCreateLobby, buttonBackToHome, buttonDialogYes, buttonDialogNo;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser JoinedUser;

    MultiPlayerLobby selectedLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_lobby_screen);
        recyclerViewLobbyscreen = findViewById(R.id.recyclerViewLobbyscreen);

        recyclerViewLobbyscreen.setHasFixedSize(true);
        recyclerViewLobbyscreen.setLayoutManager(new LinearLayoutManager(this));


        lobbyDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Lobbies").child("open");

        lobbyList=new ArrayList<>();
        lobbyAdapter = new MultiPlayerLobbyAdapter(this, this, lobbyList);
        recyclerViewLobbyscreen.setAdapter(lobbyAdapter);

        lobbyDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lobbyList.clear();

                for (DataSnapshot lobbysnapshot: snapshot.getChildren()) {
                    ArrayList<Long> possibleQuestions = new ArrayList<>();
                    for (DataSnapshot questionsnapshot: lobbysnapshot.child("possibleQuestions").getChildren()) {
                        possibleQuestions.add(questionsnapshot.getValue(Long.class));
                    }
                    MultiPlayerLobby lobby = new MultiPlayerLobby(lobbysnapshot.child("numberQuestionsPerRound").getValue(String.class), lobbysnapshot.child("userNameCreator").getValue(String.class), possibleQuestions);
                    lobbyList.add(lobby);
                }
                lobbyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        buttonCreateLobby = findViewById(R.id.buttonCreateLobby);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);

        dialog=new Dialog(MultiPlayerLobbyScreen.this);
        dialog.setContentView(R.layout.multi_player_lobby_screen_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_delete_dialog_drawable_question_catalog));
        dialog.setCancelable(false);

        buttonDialogYes = dialog.findViewById(R.id.buttonDialogYes);
        buttonDialogNo = dialog.findViewById(R.id.buttonDialogNo);

        buttonDialogNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        buttonDialogYes.setOnClickListener(view -> {
            addPlayerToLobby(selectedLobby);
        });




        buttonCreateLobby.setOnClickListener(view -> {
            Intent intent = new Intent(this, MultiPlayerStartActivity.class);
            startActivity(intent);
        });

        buttonBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });









    }

    @Override
    public void onItemClick(int position, MultiPlayerLobby lobby) {
        selectedLobby = lobby;
        dialog.show();
    }

    void addPlayerToLobby(MultiPlayerLobby lobby) {
        moveLobbyToFull(lobby);
    }

    void moveLobbyToFull(MultiPlayerLobby lobby) {
        JoinedUser = auth.getCurrentUser();
        lobbyDbRef.child("full").child(lobby.userNameCreator).child("numberQuestionsPerRound").setValue(lobby.numberQuestionsPerRound);
        lobbyDbRef.child("full").child(lobby.userNameCreator).child("userNameCreator").setValue(lobby.userNameCreator);
        lobbyDbRef.child("full").child(lobby.userNameCreator).child("userIDPlayer2").setValue(JoinedUser.getUid());
        lobbyDbRef.child("full").child(lobby.userNameCreator).child("possibleQuestions").setValue(lobby.possibleQuestionsList);
        lobbyDbRef.child("open").child(lobby.userNameCreator).removeValue();
    }
}