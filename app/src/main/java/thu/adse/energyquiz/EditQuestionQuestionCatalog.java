package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditQuestionQuestionCatalog extends AppCompatActivity {

    String editQuestionTitle,editTextAnswer1,editTextAnswer2,editTextAnswer3,editTextAnswer4, key;

    boolean editAnswer1IsCorrect,editAnswer2IsCorrect,editAnswer3IsCorrect,editAnswer4IsCorrect;

    EditText editTextEditQuestion, editTextEditAnswer1, editTextEditAnswer2, editTextEditAnswer3, editTextEditAnswer4;
    CheckBox radioButtonEditCorrectAnswer1, radioButtonEditCorrectAnswer2, radioButtonEditCorrectAnswer3, radioButtonEditCorrectAnswer4;

    Button buttonBackToCatalog, buttonSubmitEditQuestion;

    DatabaseReference questionsDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_question_catalog);
        QuestionQuestionCatalog selectedQuestion = (QuestionQuestionCatalog) getIntent().getSerializableExtra("selectedQuestion");

        editTextEditQuestion = findViewById(R.id.editTextEditQuestion);
        editTextEditAnswer1 = findViewById(R.id.editTextEditAnswer1);
        editTextEditAnswer2 = findViewById(R.id.editTextEditAnswer2);
        editTextEditAnswer3 = findViewById(R.id.editTextEditAnswer3);
        editTextEditAnswer4 = findViewById(R.id.editTextEditAnswer4);
        radioButtonEditCorrectAnswer1 = findViewById(R.id.radioButtonEditCorrectAnswer1);
        radioButtonEditCorrectAnswer2 = findViewById(R.id.radioButtonEditCorrectAnswer2);
        radioButtonEditCorrectAnswer3 = findViewById(R.id.radioButtonEditCorrectAnswer3);
        radioButtonEditCorrectAnswer4 = findViewById(R.id.radioButtonEditCorrectAnswer4);

        editTextEditQuestion.setText(selectedQuestion.questionTitle);
        editTextEditAnswer1.setText(selectedQuestion.answer1.answerText);
        editTextEditAnswer2.setText(selectedQuestion.answer2.answerText);
        editTextEditAnswer3.setText(selectedQuestion.answer3.answerText);
        editTextEditAnswer4.setText(selectedQuestion.answer4.answerText);

        radioButtonEditCorrectAnswer1.setChecked(selectedQuestion.answer1.isCorrect);
        radioButtonEditCorrectAnswer2.setChecked(selectedQuestion.answer2.isCorrect);
        radioButtonEditCorrectAnswer3.setChecked(selectedQuestion.answer3.isCorrect);
        radioButtonEditCorrectAnswer4.setChecked(selectedQuestion.answer4.isCorrect);

        buttonSubmitEditQuestion = findViewById(R.id.buttonSubmitEditQuestion);
        buttonBackToCatalog = findViewById(R.id.buttonBackToCatalog);
        questionsDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Questions");

        buttonSubmitEditQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEditQuestion(selectedQuestion);
            }
        });

        buttonBackToCatalog.setOnClickListener(v -> {
            finish();
        });
    }
    private void submitEditQuestion(QuestionQuestionCatalog selectedQuestion) {
        editQuestionTitle = editTextEditQuestion.getText().toString();
        editTextAnswer1 = editTextEditAnswer1.getText().toString();
        editTextAnswer2 = editTextEditAnswer2.getText().toString();
        editTextAnswer3 = editTextEditAnswer3.getText().toString();
        editTextAnswer4 = editTextEditAnswer4.getText().toString();

        editAnswer1IsCorrect = radioButtonEditCorrectAnswer1.isChecked();
        editAnswer2IsCorrect = radioButtonEditCorrectAnswer2.isChecked();
        editAnswer3IsCorrect = radioButtonEditCorrectAnswer3.isChecked();
        editAnswer4IsCorrect = radioButtonEditCorrectAnswer4.isChecked();

        key = selectedQuestion.getKey();

        questionsDbRef.child(key).child("questionTitle").setValue(editQuestionTitle);
        questionsDbRef.child(key).child("answers").child("answer1").child("answerText").setValue(editTextAnswer1);
        questionsDbRef.child(key).child("answers").child("answer2").child("answerText").setValue(editTextAnswer2);
        questionsDbRef.child(key).child("answers").child("answer3").child("answerText").setValue(editTextAnswer3);
        questionsDbRef.child(key).child("answers").child("answer4").child("answerText").setValue(editTextAnswer4);
        questionsDbRef.child(key).child("answers").child("answer1").child("correctAnswer").setValue(editAnswer1IsCorrect);
        questionsDbRef.child(key).child("answers").child("answer2").child("correctAnswer").setValue(editAnswer2IsCorrect);
        questionsDbRef.child(key).child("answers").child("answer3").child("correctAnswer").setValue(editAnswer3IsCorrect);
        questionsDbRef.child(key).child("answers").child("answer4").child("correctAnswer").setValue(editAnswer4IsCorrect);

        Toast.makeText(EditQuestionQuestionCatalog.this, "Daten gespeichert", Toast.LENGTH_SHORT).show();
    }

}