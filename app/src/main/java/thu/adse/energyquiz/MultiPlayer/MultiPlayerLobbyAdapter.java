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

    /**
     * Method to create a new view holder for the adapter.
     * @author Sebastian Steinhauser
     *
     * @param parent The parent view group.
     * @param viewType The view type.
     */
    @NonNull
    @Override
    public MultiPlayerLobbyAdapter.LobbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.lobby_item_multi_player,parent,false);

        return new LobbyViewHolder(viewHolder);
    }

    /**
     * Method to bind the view holder to the adapter.
     * This method sets the text of the view holder to the corresponding lobby data. It also sets the on click listener for the view holder.
     * @author Sebastian Steinhauser
     *
     * @param holder The view holder.
     * @param position The position of the view holder.
     */
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

    /**
     * This method returns the size of the lobby list.
     * @author Sebastian Steinhauser
     */
    @Override
    public int getItemCount() {
        return lobbyList.size();
    }

    /**
     * View holder class for the adapter.
     * This class holds the view holder for the adapter.
     * It contains the text views for the lobby creator user name and the number of questions per round.
     * @author Sebastian Steinhauser
     */
    public static class LobbyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewLobbyCreatorUserName, textViewNumberQuestionsPerRound;

        public LobbyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLobbyCreatorUserName = itemView.findViewById(R.id.textViewLobbyCreatorUserName);
            textViewNumberQuestionsPerRound = itemView.findViewById(R.id.textViewNumberQuestionsPerRound);
        }
    }
}
