package thu.adse.energyquiz.MultiPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import thu.adse.energyquiz.R;

public class MultiPlayerWaitingScreenAlert {
    Activity activity;
    AlertDialog dialog;
//    Button buttonWaitingScreenAbort;
    CardView cardViewPopUpResultsWaitingCancel;
    DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(), lobbyDbRef;

    public static boolean waitingAlertIsActive = false;
    ValueEventListener dblistener;

    MultiPlayerWaitingScreenAlert(Activity myActivity) {
        activity = myActivity;
    }

    void startWaitingScreenAlertDialog(String player1ID, String player2ID, Context context, int numberQuestionsPerRound, int numberCorrectAnswersRound, boolean creatorIsLoggedIn, ArrayList<Long> questionIDsForThisRound) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multi_player_waiting_results_v2, null));
        builder.setCancelable(true);
        dialog = builder.create();
        waitingAlertIsActive = true;
        setFinishedState(player1ID, creatorIsLoggedIn, player2ID);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        checkIfOpponentsAreFinished(player1ID, player2ID, context, numberQuestionsPerRound, numberCorrectAnswersRound, creatorIsLoggedIn, questionIDsForThisRound);
    }

    void checkIfOpponentsAreFinished(String player1ID, String player2ID, Context context, int numberQuestionsPerRound, int numberCorrectAnswersRound, boolean creatorIsLoggedIn, ArrayList<Long> questionIDsForThisRound) {

        dBRef.addValueEventListener(dblistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Lobbies").child("full").child(player1ID).child(player1ID + "isFinished").getValue(Boolean.class) &&
                        snapshot.child("Lobbies").child("full").child(player1ID).child(player2ID + "isFinished").getValue(Boolean.class)) {
                    waitingAlertIsActive = false;
                    dialog.dismiss();
                    Intent resultScreenIntent = new Intent(context, MultiPlayerResultActivity.class);
                    resultScreenIntent.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                    resultScreenIntent.putExtra("player1ID", player1ID);
                    resultScreenIntent.putExtra("player2ID", player2ID);
                    resultScreenIntent.putExtra("numberCorrectAnswersRound", numberCorrectAnswersRound);
                    resultScreenIntent.putExtra("creatorIsLoggedIn", creatorIsLoggedIn);
                    resultScreenIntent.putExtra("usedQuestionIDs", questionIDsForThisRound);

                    dBRef.removeEventListener(dblistener);
                    context.startActivity(resultScreenIntent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void stopWaitingScreenAlertDialog() {
        dialog.dismiss();
    }

    private void setFinishedState(String player1ID, boolean creatorIsLoggedIn, String player2ID) {
        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");

        if (creatorIsLoggedIn) {
            lobbyDbRef.child("full").child(player1ID).child(player1ID + "isFinished").setValue(true);
        } else {
            lobbyDbRef.child("full").child(player1ID).child(player2ID + "isFinished").setValue(true);
        }
    }

    private void checkIfBothArefinished(DataSnapshot snapshot, String player1ID, String player2ID) {
        if (snapshot.child("full").child(player1ID).child(player1ID + "isFinished").getValue(Boolean.class) &&
                snapshot.child("full").child(player1ID).child(player2ID + "isFinished").getValue(Boolean.class)) {
            MultiPlayerGameActivity.bothPlayersFinished = true;
            Log.d("bothPlayersAreFinished", "checkIfBothArefinished: " + MultiPlayerGameActivity.bothPlayersFinished);
        } else if (snapshot.child("full").child(player1ID).child(player1ID + "isFinished").getValue(Boolean.class) ||
                snapshot.child("full").child(player1ID).child(player2ID + "isFinished").getValue(Boolean.class)) {
            Log.d("playerFinished", "checkIfBothArefinished: One Player Is finished and waiting for the other");
            MultiPlayerGameActivity.onePlayerisFinished = true;
        } else {
            Log.d("playerFinished", "checkIfBothArefinished: no one is finished yet (hopefully never reached");
            MultiPlayerGameActivity.onePlayerisFinished = false;
            MultiPlayerGameActivity.bothPlayersFinished = false;
        }
    }
}
