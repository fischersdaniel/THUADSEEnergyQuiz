package thu.adse.energyquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Lobbyscreen extends AppCompatActivity {

    ListView listView;
    Button buttonLobbyCreate;

    List<String> roomslist;

    String playerName = "";
    String playerID = "";
    String roomName = "";

    FirebaseDatabase database;
    private FirebaseAuth authLobby;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    DatabaseReference playerIDRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobbyscreen);
        database = FirebaseDatabase.getInstance();

        //get the player name and assign his room name to the player name
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //Get signed in UserID
            playerID = FirebaseAuth.getInstance().getUid();
    } else {
        // No user is signed in
    }
        roomName = playerName + " " + playerID;
        listView = findViewById(R.id.listView_Lobby);
        buttonLobbyCreate = findViewById(R.id.button2);
        //all existing available rooms
        roomslist = new ArrayList<>();

        buttonLobbyCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create room and add yourself as player1
                buttonLobbyCreate.setText("CREATING ROOM");
                buttonLobbyCreate.setEnabled(false);
                roomName = playerName + " " + playerID;
                roomRef = database.getReference("rooms/" + roomName +"/player1");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //join an existing room and add yourself as player2
                roomName = roomslist.get(position);
                roomRef = database.getReference("rooms/" + roomName + "/player2");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });
        //show if new room is available
        addRoomsEventListener();
    }

    private void addRoomEventListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //join the room
                buttonLobbyCreate.setText("CREATE ROOM");
                buttonLobbyCreate.setEnabled(true);
                /*Intent intent = new Intent(getApplicationContext(), Loginscreen.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                buttonLobbyCreate.setText("CREATE ROOM");
                buttonLobbyCreate.setEnabled(false);
                Toast.makeText(Lobbyscreen.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addRoomsEventListener(){
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //show List of rooms
                roomslist.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for (DataSnapshot datasnapshot: rooms){
                    roomslist.add(datasnapshot.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Lobbyscreen.this,
                            android.R.layout.simple_list_item_1, roomslist);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error - nothing happens
            }
        });
    }
}


//Unused Code
//playerIDRef.addValueEventListener(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {}
//@Override
//public void onCancelled(@NonNull DatabaseError error) {}