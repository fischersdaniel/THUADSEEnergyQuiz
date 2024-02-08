package thu.adse.energyquiz.QuestionCatalog;

import java.io.Serializable;

/**
 * A class to represent an answer to a question
 * This class is used to represent an answer to a question in the quiz
 * It contains the answer text and a boolean to indicate if the answer is correct
 * @author Sebastian Steinhauser
 */

public class AnswerQuestionCatalog implements Serializable {
    public String answerText;
    public boolean isCorrect;

    public AnswerQuestionCatalog(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }
}
