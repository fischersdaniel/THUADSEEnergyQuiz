package thu.adse.energyquiz.QuestionCatalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import thu.adse.energyquiz.R;

/**
 * This class is used to add a new question to the question catalog.
 * @author Sebastian Steinhauser
 */

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
    CardView cardViewCatalogAddQuestionBack, cardViewButtonSubmitNewQuestion;
    CheckBox radioButtonNewCorrectAnswer1, radioButtonNewCorrectAnswer2, radioButtonNewCorrectAnswer3, radioButtonNewCorrectAnswer4;

    DatabaseReference questionsDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_new_question_v2);

        editTextNewQuestion = findViewById(R.id.editTextNewQuestion);
        editTextNewAnswer1 = findViewById(R.id.editTextNewAnswer1);
        editTextNewAnswer2 = findViewById(R.id.editTextNewAnswer2);
        editTextNewAnswer3 = findViewById(R.id.editTextNewAnswer3);
        editTextNewAnswer4 = findViewById(R.id.editTextNewAnswer4);
        radioButtonNewCorrectAnswer1 = findViewById(R.id.radioButtonNewCorrectAnswer1);
        radioButtonNewCorrectAnswer2 = findViewById(R.id.radioButtonNewCorrectAnswer2);
        radioButtonNewCorrectAnswer3 = findViewById(R.id.radioButtonNewCorrectAnswer3);
        radioButtonNewCorrectAnswer4 = findViewById(R.id.radioButtonNewCorrectAnswer4);

        cardViewCatalogAddQuestionBack = findViewById(R.id.cardViewCatalogAddQuestionBack);
        cardViewButtonSubmitNewQuestion = findViewById(R.id.cardViewButtonSubmitNewQuestion);

        questionsDbRef = FirebaseDatabase.getInstance().getReference().child("Questions");

        findViewById(android.R.id.content).setFocusableInTouchMode(true);

        cardViewButtonSubmitNewQuestion.setOnClickListener(view -> {
            submitNewQuestion();
            Intent intent = new Intent(NewQuestionQuestionCatalog.this, MainActivityQuestionCatalog.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition

        });

        cardViewCatalogAddQuestionBack = findViewById(R.id.cardViewCatalogAddQuestionBack);
        cardViewCatalogAddQuestionBack.setOnClickListener(view -> backToCatalog());
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

    /**
     * Method to submit the new question to the database.
     * @author Sebastian Steinhauser
     */
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
        startActivity(new Intent(NewQuestionQuestionCatalog.this, MainActivityQuestionCatalog.class));


    }

    /**
     * The method gets the data snapshot from the database and generates the new question IDs.
     * @author Sebastian Steinhauser
     *
     * @param dataSnapshot The data snapshot from the database.
     */
    public void defineID(DataSnapshot dataSnapshot) {
        List<String> questionIDs = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            questionIDs.add(ds.getKey());
        }
        generateQuestionID(questionIDs);
    }

    /**
     * Method to go back to the question catalog.
     * @author Sebastian Steinhauser
     */
    public void backToCatalog(){
        Intent intent = new Intent(NewQuestionQuestionCatalog.this, MainActivityQuestionCatalog.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
    }

    /**
     * The method generates the new question ID based on the existing question IDs.
     * It iterates through the list of question IDs and uses the first available ID.
     * @author Sebastian Steinhauser
     *
     * @param questionIDs The list of question IDs.
     */
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

    /**
     * Method to detect when the user touches the screen outside of the keyboard.
     * @param ev the touch screen event
     *
     * @return true if the event was handled, false otherwise
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Method to hide the keyboard.
     * @param activity The activity where the keyboard will be hidden.
     */
    private void hideKeyboard(Activity activity) {

        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            findViewById(android.R.id.content).clearFocus();
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }


}