package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivityQuestionCatalog extends AppCompatActivity implements RecyclerViewInterfaceQuestionCatalog {

    RecyclerView recyclerView;
    DatabaseReference database;
    QuestionAdapterQuestionCatalog questionAdapter;
    ArrayList<QuestionQuestionCatalog> list;
    Dialog dialog;
    Button buttonToNewQuestionActivity, buttonDialogEdit, buttonDialogDelete, buttonDialogCancel;



    public QuestionQuestionCatalog selectedQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_question_catalog);

        recyclerView = findViewById(R.id.recyclerViewQuestionDb);
        database = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Questions");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        questionAdapter = new QuestionAdapterQuestionCatalog(this, list, this);
        recyclerView.setAdapter(questionAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    AnswerQuestionCatalog answer1 = new AnswerQuestionCatalog(questionSnapshot.child("answers").child("answer1").child("answerText").getValue(String.class),
                            Boolean.TRUE.equals(questionSnapshot.child("answers").child("answer1").child("correctAnswer").getValue(Boolean.class)));
                    AnswerQuestionCatalog answer2 = new AnswerQuestionCatalog(questionSnapshot.child("answers").child("answer2").child("answerText").getValue(String.class),
                            Boolean.TRUE.equals(questionSnapshot.child("answers").child("answer2").child("correctAnswer").getValue(Boolean.class)));
                    AnswerQuestionCatalog answer3 = new AnswerQuestionCatalog(questionSnapshot.child("answers").child("answer3").child("answerText").getValue(String.class)
                            , Boolean.TRUE.equals(questionSnapshot.child("answers").child("answer3").child("correctAnswer").getValue(Boolean.class)));
                    AnswerQuestionCatalog answer4 = new AnswerQuestionCatalog(questionSnapshot.child("answers").child("answer4").child("answerText").getValue(String.class)
                            , Boolean.TRUE.equals(questionSnapshot.child("answers").child("answer4").child("correctAnswer").getValue(Boolean.class)));

                    QuestionQuestionCatalog question = new QuestionQuestionCatalog(questionSnapshot.child("questionTitle").getValue(String.class), questionSnapshot.getKey(),answer1, answer2, answer3, answer4);

                    list.add(question);

                }

                questionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        buttonToNewQuestionActivity = findViewById(R.id.buttonToNewQuestion);
        buttonToNewQuestionActivity.setOnClickListener(view -> openNewQuestion());
        dialog = new Dialog(MainActivityQuestionCatalog.this);
        dialog.setContentView(R.layout.edit_delete_dialog_box_question_catalog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_delete_dialog_drawable_question_catalog));
        dialog.setCancelable(true);

        buttonDialogEdit = dialog.findViewById(R.id.buttonDialogEdit);
        buttonDialogDelete = dialog.findViewById(R.id.buttonDialogDelete);
        buttonDialogCancel = dialog.findViewById(R.id.buttonDialogCancel);

        buttonDialogCancel.setOnClickListener(view -> dialog.dismiss());

        buttonDialogEdit.setOnClickListener(view -> {
            openEditQuestion(selectedQuestion);
            dialog.dismiss();
        });

        buttonDialogDelete.setOnClickListener(view -> {
            deleteQuestion(selectedQuestion);
            dialog.dismiss();
            //Option: Dialog "Sind Sie sicher, dass Sie die Frage löschen wollen?" einfügen
        });
    }
    public void openEditQuestion(QuestionQuestionCatalog question) {
        Intent intent = new Intent(MainActivityQuestionCatalog.this, EditQuestionQuestionCatalog.class);
        intent.putExtra("selectedQuestion", question);
        startActivity(intent);
    }

    public void openNewQuestion() {
        Intent intent = new Intent(MainActivityQuestionCatalog.this, NewQuestionQuestionCatalog.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position, QuestionQuestionCatalog question) {
        selectedQuestion=question;
        dialog.show();
    }

    public void deleteQuestion(QuestionQuestionCatalog question) {
        database.child(question.getKey()).removeValue();
    }
}
