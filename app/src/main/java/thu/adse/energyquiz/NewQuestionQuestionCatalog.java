package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class NewQuestionQuestionCatalog extends AppCompatActivity {

    String newQuestionTitle;
    String newAnswer1;
    boolean newAnswer1IsCorrect;
    String newAnswer2;
    boolean newAnswer2IsCorrect;
    String newAnswer3;
    boolean newAnswer3IsCorrect;
    String newAnswer4;
    boolean newAnswer4IsCorrect;
    int newQuestionId=1;



    EditText editTextNewQuestion, editTextNewAnswer1, editTextNewAnswer2, editTextNewAnswer3, editTextNewAnswer4;
    Button buttonBackToCatalog, buttonSubmitNewQuestion;
    RadioButton radioButtonNewCorrectAnswer1, radioButtonNewCorrectAnswer2, radioButtonNewCorrectAnswer3, radioButtonNewCorrectAnswer4;

    DatabaseReference questionsDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question_question_catalog);

        editTextNewQuestion = findViewById(R.id.editTextNewQuestion);
        editTextNewAnswer1 = findViewById(R.id.editTextNewAnswer1);
        editTextNewAnswer2 = findViewById(R.id.editTextNewAnswer2);
        editTextNewAnswer3 = findViewById(R.id.editTextNewAnswer3);
        editTextNewAnswer4 = findViewById(R.id.editTextNewAnswer4);
        radioButtonNewCorrectAnswer1 = findViewById(R.id.radioButtonNewCorrectAnswer1);
        radioButtonNewCorrectAnswer2 = findViewById(R.id.radioButtonNewCorrectAnswer2);
        radioButtonNewCorrectAnswer3 = findViewById(R.id.radioButtonNewCorrectAnswer3);
        radioButtonNewCorrectAnswer4 = findViewById(R.id.radioButtonNewCorrectAnswer4);

        buttonBackToCatalog = findViewById(R.id.buttonBackToCatalog);
        buttonSubmitNewQuestion = findViewById(R.id.buttonSubmitNewQuestion);

        questionsDbRef = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Questions");
        buttonSubmitNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitNewQuestion();
            }

        });
        buttonBackToCatalog = findViewById(R.id.buttonBackToCatalog);
        buttonBackToCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToCatalog();
            }
        });

        questionsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                defineID(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void submitNewQuestion() {
        newQuestionTitle = editTextNewQuestion.getText().toString();
        newAnswer1 = editTextNewAnswer1.getText().toString();
        newAnswer2 = editTextNewAnswer2.getText().toString();
        newAnswer3 = editTextNewAnswer3.getText().toString();
        newAnswer4 = editTextNewAnswer4.getText().toString();
        newAnswer1IsCorrect = radioButtonNewCorrectAnswer1.isChecked();
        newAnswer2IsCorrect = radioButtonNewCorrectAnswer2.isChecked();
        newAnswer3IsCorrect = radioButtonNewCorrectAnswer3.isChecked();
        newAnswer4IsCorrect = radioButtonNewCorrectAnswer4.isChecked();

        questionsDbRef.child(Integer.toString(newQuestionId)).push();

        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer1").child("answerText").setValue(newAnswer1);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer2").child("answerText").setValue(newAnswer2);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer3").child("answerText").setValue(newAnswer3);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer4").child("answerText").setValue(newAnswer4);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer1").child("correctAnswer").setValue(newAnswer1IsCorrect);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer2").child("correctAnswer").setValue(newAnswer2IsCorrect);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer3").child("correctAnswer").setValue(newAnswer3IsCorrect);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("answers").child("answer4").child("correctAnswer").setValue(newAnswer4IsCorrect);
        questionsDbRef.child(Integer.toString(newQuestionId)).child("questionTitle").setValue(newQuestionTitle);

        Toast.makeText(NewQuestionQuestionCatalog.this, "Daten gespeichert", Toast.LENGTH_SHORT).show();
    }

    public void defineID(DataSnapshot dataSnapshot) {
        List<String> questionIDs = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            questionIDs.add(ds.getKey());
        }
        generateQuestionID(questionIDs);
    }

    public void backToCatalog(){
        Intent intent = new Intent(NewQuestionQuestionCatalog.this, MainActivityQuestionCatalog.class);
        startActivity(intent);
    }

    public void generateQuestionID(List<String> questionIDs){
        int idCount=1;
        newQuestionId =1;
        for (int i = 0; i < questionIDs.size(); i++) {
            if (!questionIDs.get(i).equals(Integer.toString(idCount))){
                newQuestionId = idCount;
            } else{
                newQuestionId = 0;
            }
            idCount++;
        }
        if (newQuestionId == 0){
            newQuestionId = questionIDs.size() + 1;
        }
    }


}