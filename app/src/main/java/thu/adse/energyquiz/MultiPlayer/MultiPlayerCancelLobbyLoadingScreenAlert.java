package thu.adse.energyquiz.MultiPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import thu.adse.energyquiz.R;

public class MultiPlayerCancelLobbyLoadingScreenAlert {

    Activity activity;
    AlertDialog dialog;
    Button buttonLoadingScreenCancelLobby;
    DatabaseReference lobbyDbRef  = FirebaseDatabase.getInstance().getReference().child("Lobbies");
    FirebaseAuth auth;


    MultiPlayerCancelLobbyLoadingScreenAlert(Activity myActivity){
        activity = myActivity;
    }

    void startLoadingScreenAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_multi_player_loading_screen, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        closeMultiPlayerLoadingScreenAlertDialog();
    }

    void closeMultiPlayerLoadingScreenAlertDialog() {
        buttonLoadingScreenCancelLobby = dialog.findViewById(R.id.buttonLoadingScreenCancelLobby);
        buttonLoadingScreenCancelLobby.setOnClickListener(view -> {
            dialog.dismiss();
            auth = FirebaseAuth.getInstance();
            FirebaseUser userCreator = auth.getCurrentUser();
            lobbyDbRef.child("open").child(userCreator.getUid()).removeValue();

        });

    }
}
