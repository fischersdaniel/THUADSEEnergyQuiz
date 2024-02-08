package thu.adse.energyquiz.QuestionCatalog;

import java.io.Serializable;

/**
 * This class is used to represent an answer to a question in the question catalog.
 * It contains the answer text and a boolean to indicate if the answer is correct.
 * @author Sebastian Steinhauser
 */

public class AnswerQuestionCatalog implements Serializable {
    public String answerText;
    public boolean isCorrect;

    /**
     * Constructor for the AnswerQuestionCatalog class.
     * @author Sebastian Steinhauser
     *
     * @param answerText the text of the answer
     * @param isCorrect a boolean to indicate if the answer is correct
     */
    public AnswerQuestionCatalog(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }
}
