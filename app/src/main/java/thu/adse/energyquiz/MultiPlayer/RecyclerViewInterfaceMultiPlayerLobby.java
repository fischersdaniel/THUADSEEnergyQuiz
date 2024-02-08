package thu.adse.energyquiz.MultiPlayer;

/**
 * Interface for the RecyclerView in the MultiPlayerLobbyActivity
 * This interface is used to handle the click events on the RecyclerView items
 * @author Sebastian Steinhauser
 */
public interface RecyclerViewInterfaceMultiPlayerLobby {
    void onItemClick(int position,MultiPlayerLobby lobby);
}
