package thu.adse.energyquiz.QuestionCatalog;

import java.io.Serializable;

import thu.adse.energyquiz.QuestionCatalog.AnswerQuestionCatalog;

public class QuestionQuestionCatalog  implements Serializable {

    public String questionTitle,key;
    public AnswerQuestionCatalog answer1, answer2, answer3, answer4;


    public QuestionQuestionCatalog(String questionTitle, String key, AnswerQuestionCatalog answer1, AnswerQuestionCatalog answer2, AnswerQuestionCatalog answer3, AnswerQuestionCatalog answer4) {
        this.questionTitle = questionTitle;
        this.key = key;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public AnswerQuestionCatalog getAnswer1() {
        return answer1;
    }

    public AnswerQuestionCatalog getAnswer2() {
        return answer2;
    }

    public AnswerQuestionCatalog getAnswer3() {
        return answer3;
    }

    public AnswerQuestionCatalog getAnswer4() {
        return answer4;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public void setAnswer1(AnswerQuestionCatalog answer1) {
        this.answer1 = answer1;
    }

    public void setAnswer2(AnswerQuestionCatalog answer2) {
        this.answer2 = answer2;
    }

    public void setAnswer3(AnswerQuestionCatalog answer3) {
        this.answer3 = answer3;
    }

    public void setAnswer4(AnswerQuestionCatalog answer4) {
        this.answer4 = answer4;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
