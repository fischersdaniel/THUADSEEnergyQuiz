package thu.adse.energyquiz.MultiPlayer;

import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MultiPlayerLobby { ;
    String userNameCreator,numberQuestionsPerRound, userIDCreator;
    ArrayList<Long> possibleQuestionsList;


    public MultiPlayerLobby(String numberQuestionsPerRound, String userNameCreator, String userIDCreator,ArrayList<Long> possibleQuestionsList) {
        this.numberQuestionsPerRound = numberQuestionsPerRound;
        this.userNameCreator = userNameCreator;
        this.possibleQuestionsList = possibleQuestionsList;
        this.userIDCreator=userIDCreator;
    }

    public String getNumberQuestionsPerRound() {
        return numberQuestionsPerRound;
    }

    public void setNumberQuestionsPerRound(String numberQuestionsPerRound) {
        this.numberQuestionsPerRound = numberQuestionsPerRound;
    }

    public String getUserNameCreator() {
        return userNameCreator;
    }

    public void setUserCreator(String UserNameCreator) {
        this.userNameCreator = UserNameCreator;
    }
}
