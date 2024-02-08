package thu.adse.energyquiz.QuestionCatalog;

import java.io.Serializable;

/**
 * This class used to represent a question RecyclerView of the question catalog.
 * @author Sebastian Steinhauser
 */
public class QuestionQuestionCatalog  implements Serializable {

    public String questionTitle,key;
    public AnswerQuestionCatalog answer1, answer2, answer3, answer4;

    /**
     * Constructor for the QuestionQuestionCatalog class.
     * @author Sebastian Steinhauser
     *
     * @param questionTitle the title of the question
     * @param key the key of the question
     * @param answer1 the first answer to the question
     * @param answer2 the second answer to the question
     * @param answer3 the third answer to the question
     * @param answer4 the fourth answer to the question
     */
    public QuestionQuestionCatalog(String questionTitle, String key, AnswerQuestionCatalog answer1, AnswerQuestionCatalog answer2, AnswerQuestionCatalog answer3, AnswerQuestionCatalog answer4) {
        this.questionTitle = questionTitle;
        this.key = key;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public String getKey() {
        return key;
    }
}
