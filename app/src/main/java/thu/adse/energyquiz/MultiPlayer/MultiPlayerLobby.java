package thu.adse.energyquiz.MultiPlayer;

import java.util.ArrayList;
/**
 * A Class to represent a MultiPlayerLobby.
 * @author Sebastian Steinhauser
 */
public class MultiPlayerLobby {
    String userNameCreator,numberQuestionsPerRound, userIDCreator;
    ArrayList<Long> possibleQuestionsList;

    /**
     * Constructor for initializing the MultiPlayerLobby object.
     * @param numberQuestionsPerRound The number of questions per round.
     * @param userNameCreator The name of the user who created the lobby.
     * @param userIDCreator The ID of the user who created the lobby.
     * @param possibleQuestionsList The list of possible questions for the lobby.
     */
    public MultiPlayerLobby(String numberQuestionsPerRound, String userNameCreator, String userIDCreator,ArrayList<Long> possibleQuestionsList) {
        this.numberQuestionsPerRound = numberQuestionsPerRound;
        this.userNameCreator = userNameCreator;
        this.possibleQuestionsList = possibleQuestionsList;
        this.userIDCreator=userIDCreator;
    }
}
