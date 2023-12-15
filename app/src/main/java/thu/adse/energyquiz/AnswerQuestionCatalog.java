package thu.adse.energyquiz;

import java.io.Serializable;

public class AnswerQuestionCatalog implements Serializable {
    public String answerText;
    public boolean isCorrect;

    public AnswerQuestionCatalog(String answerText, boolean isCorrect) {
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
