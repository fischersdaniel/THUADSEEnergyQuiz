package thu.adse.energyquiz.MultiPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import thu.adse.energyquiz.R;

/**
 * A class that creates a dialog window for waiting of a opponent in the multiplayer lobby screen.
 * @author Sebastian Steinhauser
 */
public class MultiPlayerCancelLobbyLoadingScreenAlert {

    Activity activity;
    AlertDialog dialog;
    CardView cardViewPopUpLobbyWaitingCancel;
    DatabaseReference lobbyDbRef  = FirebaseDatabase.getInstance().getReference().child("Lobbies");
    FirebaseAuth auth;

    /**
     * Constructor for initializing the MultiPlayerCancelLobbyLoadingScreenAlert object.
     * @author Sebastian Steinhauser
     *
     * @param myActivity The activity where the dialog window will be displayed.
     */
    MultiPlayerCancelLobbyLoadingScreenAlert(Activity myActivity){
        activity = myActivity;
    }

    /**
     * Starts the dialog window for the lobby screen.
     * @author Sebastian Steinhauser
     */
    void startLoadingScreenAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multi_player_wait_start, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        closeMultiPlayerLoadingScreenAlertDialog();
    }

    /**
     * Dismisses the dialog window for the lobby screen.
     * Removes the lobby from the database if the user cancels the lobby creation while in the waiting dialog.
     * @author Sebastian Steinhauser
     */
    void closeMultiPlayerLoadingScreenAlertDialog() {
        cardViewPopUpLobbyWaitingCancel = dialog.findViewById(R.id.cardViewPopUpLobbyWaitingCancel);
        cardViewPopUpLobbyWaitingCancel.setOnClickListener(view -> {
            dialog.dismiss();
            auth = FirebaseAuth.getInstance();
            FirebaseUser userCreator = auth.getCurrentUser();
            lobbyDbRef.child("open").child(userCreator.getUid()).removeValue();

        });

    }
}