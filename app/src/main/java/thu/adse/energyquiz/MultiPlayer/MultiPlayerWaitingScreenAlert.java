package thu.adse.energyquiz.MultiPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import thu.adse.energyquiz.R;

/**
 * The class MultiPlayerWaitingScreenAlert is used to show a waiting screen alert dialog for the multiplayer game
 * when the game is started and the players are waiting for the results of the other player.
 * @author Sebastian Steinhauser
 */
public class MultiPlayerWaitingScreenAlert {
    Activity activity;
    AlertDialog dialog;
    DatabaseReference dBRef = FirebaseDatabase.getInstance().getReference(), lobbyDbRef;

    public static boolean waitingAlertIsActive = false;
    ValueEventListener dbListener;

    MultiPlayerWaitingScreenAlert(Activity myActivity) {
        activity = myActivity;
    }

    /**
     * Start the waiting screen alert dialog for the multiplayer game.
     * This dialog is shown when the game is started and the players are waiting for the results of the other player.
     * The dialog is dismissed when both players are finished with the game.
     * If one player aborts the game, the dialog is dismissed and the result screen is started with the information that the game was aborted.
     * @author Sebastian Steinhauser
     *
     * @param player1ID the ID of the player who created the lobby
     * @param player2ID the ID of the player who joined the lobby
     * @param context the context of the activity
     * @param numberQuestionsPerRound the number of questions per round
     * @param numberCorrectAnswersRound the number of correct answers in the round
     * @param creatorIsLoggedIn boolean to check if the creator of the lobby is logged in
     * @param questionIDsForThisRound the IDs of the questions used in this round
     */
    void startWaitingScreenAlertDialog(String player1ID, String player2ID, Context context, int numberQuestionsPerRound, int numberCorrectAnswersRound, boolean creatorIsLoggedIn, ArrayList<Long> questionIDsForThisRound) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multi_player_wait_result, null));
        builder.setCancelable(true);
        dialog = builder.create();
        waitingAlertIsActive = true;
        setFinishedState(player1ID, creatorIsLoggedIn, player2ID);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        checkIfOpponentsAreFinished(player1ID, player2ID, context, numberQuestionsPerRound, numberCorrectAnswersRound, creatorIsLoggedIn, questionIDsForThisRound);
    }

    /**
     * Check if the opponents are finished with the game and start the result screen if they are finished
     * @author Sebastian Steinhauser
     *
     * @param player1ID the ID of the player who created the lobby
     * @param player2ID the ID of the player who joined the lobby
     * @param context the context of the activity
     * @param numberQuestionsPerRound the number of questions per round
     * @param numberCorrectAnswersRound the number of correct answers in the round
     * @param creatorIsLoggedIn boolean to check if the creator of the lobby is logged in
     * @param questionIDsForThisRound the IDs of the questions used in this round
     */
    void checkIfOpponentsAreFinished(String player1ID, String player2ID, Context context, int numberQuestionsPerRound, int numberCorrectAnswersRound, boolean creatorIsLoggedIn, ArrayList<Long> questionIDsForThisRound) {

        dBRef.addValueEventListener(dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Lobbies").child("full").child(player1ID).child(player1ID + "isFinished").getValue(Boolean.class) &&
                        snapshot.child("Lobbies").child("full").child(player1ID).child(player2ID + "isFinished").getValue(Boolean.class)) {
                    Log.d("checkIfOpponentsAreFinished", "onDataChange: both Players finished");
                    waitingAlertIsActive = false;
                    dialog.dismiss();
                    Intent resultScreenIntent = new Intent(context, MultiPlayerResultActivity.class);
                    resultScreenIntent.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                    resultScreenIntent.putExtra("player1ID", player1ID);
                    resultScreenIntent.putExtra("player2ID", player2ID);
                    resultScreenIntent.putExtra("numberCorrectAnswersRound", numberCorrectAnswersRound);
                    resultScreenIntent.putExtra("creatorIsLoggedIn", creatorIsLoggedIn);
                    resultScreenIntent.putExtra("usedQuestionIDs", questionIDsForThisRound);

                    dBRef.removeEventListener(dbListener);
                    context.startActivity(resultScreenIntent);
                }else if (snapshot.child("Lobbies").child("full").child(player1ID).child("abortGame").getValue(Boolean.class)){
                    Log.d("checkIfOpponentsAreFinished", "onDataChange: other Player aborted Game");
                    if (creatorIsLoggedIn){
                        dBRef.child("Lobbies").child("full").child(player1ID).child("correctAnswersPlayer2").setValue("Spielabbruch");
                        Log.d("dbCall", "set correctAnswersPlayer2 on Spiel abgebrochen");
                    }else {
                        dBRef.child("Lobbies").child("full").child(player1ID).child("correctAnswersPlayer1").setValue("Spielabbruch");
                        Log.d("dbCall", "set correctAnswersPlayer1 on Spiel abgebrochen");

                    }

                    waitingAlertIsActive= false;
                    dialog.dismiss();
                    Intent resultScreenIntent= new Intent(context, MultiPlayerResultActivity.class);
                    resultScreenIntent.putExtra("numberQuestionsPerRound", numberQuestionsPerRound);
                    resultScreenIntent.putExtra("player1ID", player1ID);
                    resultScreenIntent.putExtra("player2ID", player2ID);
                    resultScreenIntent.putExtra("numberCorrectAnswersRound", numberCorrectAnswersRound);
                    resultScreenIntent.putExtra("creatorIsLoggedIn", creatorIsLoggedIn);
                    resultScreenIntent.putExtra("usedQuestionIDs", questionIDsForThisRound);
                    resultScreenIntent.putExtra("abortGame", snapshot.child("Lobbies").child("full").child(player1ID).child("abortGame").getValue(Boolean.class));

                    dBRef.removeEventListener(dbListener);
                    Log.d("intent", "start Result Screen");

                    context.startActivity(resultScreenIntent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Set the finished state of the game to true for the player who is still logged in
     * @author Sebastian Steinhauser
     *
     * @param player1ID the ID of the player who created the lobby
     * @param creatorIsLoggedIn boolean to check if the creator of the lobby is logged in
     * @param player2ID the ID of the player who joined the lobby
     */
    private void setFinishedState(String player1ID, boolean creatorIsLoggedIn, String player2ID) {
        lobbyDbRef = FirebaseDatabase.getInstance().getReference().child("Lobbies");

        if (creatorIsLoggedIn) {
            lobbyDbRef.child("full").child(player1ID).child(player1ID + "isFinished").setValue(true);
        } else {
            lobbyDbRef.child("full").child(player1ID).child(player2ID + "isFinished").setValue(true);
        }
    }


}
