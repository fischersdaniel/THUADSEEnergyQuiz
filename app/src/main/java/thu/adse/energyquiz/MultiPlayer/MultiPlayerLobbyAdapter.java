package thu.adse.energyquiz.MultiPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import thu.adse.energyquiz.R;

/**
 * This class creates a adapter for the multiplayer lobby screen.
 * It sets the text of the view holder to the corresponding lobby data.
 * It also sets the on click listener for the view holder.
 * @author Sebastian Steinhauser
 */
public class MultiPlayerLobbyAdapter extends RecyclerView.Adapter<MultiPlayerLobbyAdapter.LobbyViewHolder> {
    private final RecyclerViewInterfaceMultiPlayerLobby recyclerViewInterface;
    Context context;
    ArrayList<MultiPlayerLobby> lobbyList;

    /**
     * Constructor for MultiPlayerLobbyAdapter.
     * @author Sebastian Steinhauser
     *
     * @param recyclerViewInterface The interface for handling item clicks.
     * @param context The context of the adapter.
     * @param lobbyList The list of multiplayer lobbies.
     */
    public MultiPlayerLobbyAdapter(RecyclerViewInterfaceMultiPlayerLobby recyclerViewInterface, Context context, ArrayList<MultiPlayerLobby> lobbyList) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.lobbyList = lobbyList;
    }

    @NonNull
    @Override
    public MultiPlayerLobbyAdapter.LobbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.item_multi_player_lobby,parent,false);

        return new LobbyViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiPlayerLobbyAdapter.LobbyViewHolder holder, int position) {
        MultiPlayerLobby lobby= lobbyList.get(position);
        holder.textViewNumberQuestionsPerRound.setText(String. valueOf( lobby.numberQuestionsPerRound ));
        holder.textViewLobbyCreatorUserName.setText(lobby.userNameCreator);

        holder.itemView.setOnClickListener(view -> {
            if (recyclerViewInterface != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(pos, lobby);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lobbyList.size();
    }

    public static class LobbyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewLobbyCreatorUserName, textViewNumberQuestionsPerRound;

        public LobbyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLobbyCreatorUserName = itemView.findViewById(R.id.textViewLobbyCreatorUserName);
            textViewNumberQuestionsPerRound = itemView.findViewById(R.id.textViewNumberQuestionsPerRound);
        }
    }
}
