package thu.adse.energyquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class MainActivityQuestionCatalog<LoginDialogFragment> extends AppCompatActivity implements RecyclerViewInterfaceQuestionCatalog {

    RecyclerView recyclerView;
    DatabaseReference database;
    DatabaseReference databaseAdmin;
    QuestionAdapterQuestionCatalog questionAdapter;
    ArrayList<QuestionQuestionCatalog> list;
    Dialog dialog;
    Button buttonToNewQuestionActivity, buttonBackToMenu, buttonDialogEdit, buttonDialogDelete, buttonDialogCancel;

    String adminpasswordDB;

    public QuestionQuestionCatalog selectedQuestion;
    enum CatalogueChange
    {
        ADD_QUESTION,
        EDIT_QUESTION,
        DELETE_QUESTION
    }

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
        buttonToNewQuestionActivity.setOnClickListener(view ->
        {
            CheckCataloguePermission(CatalogueChange.ADD_QUESTION, null);
        });

        dialog = new Dialog(MainActivityQuestionCatalog.this);
        dialog.setContentView(R.layout.edit_delete_dialog_box_question_catalog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_delete_dialog_drawable_question_catalog));
        dialog.setCancelable(true);

        buttonBackToMenu = findViewById(R.id.buttonBackToMenu);
        buttonBackToMenu.setOnClickListener(view -> backToMenu());



        buttonDialogEdit = dialog.findViewById(R.id.buttonDialogEdit);
        buttonDialogDelete = dialog.findViewById(R.id.buttonDialogDelete);
        buttonDialogCancel = dialog.findViewById(R.id.buttonDialogCancel);

        buttonDialogCancel.setOnClickListener(view -> dialog.dismiss());

        buttonDialogEdit.setOnClickListener(view -> {
            CheckCataloguePermission(CatalogueChange.EDIT_QUESTION, selectedQuestion);
            // openEditQuestion(selectedQuestion);
            dialog.dismiss();
        });

        buttonDialogDelete.setOnClickListener(view -> {
            CheckCataloguePermission(CatalogueChange.DELETE_QUESTION, selectedQuestion);
            // deleteQuestion(selectedQuestion);
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
    public void backToMenu(){
        Intent intent = new Intent(MainActivityQuestionCatalog.this, HomeScreenActivity.class);
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

    public void ReadadminpasswordDB(CatalogueChange requestedChange, QuestionQuestionCatalog question){
        //Hier wird der Playerrank oder das Admin Passwort abgefragt
        //DB Path definieren
        databaseAdmin = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Adminpassword");
        databaseAdmin.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot adminsnapshot)
            {
                if (adminsnapshot.exists())
                {
                    adminpasswordDB = adminsnapshot.getValue().toString();
                    Log.d("myTag", adminpasswordDB);
                    showStringInputDialog(MainActivityQuestionCatalog.this::onAdminPasswordEntered, requestedChange, question);
                };

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.e("myTag", "Error reading data: " + error.getMessage());
            }
        });
    }
    public void onAdminPasswordEntered(String adminpasswordUser, CatalogueChange requestedChange, QuestionQuestionCatalog question){
        if(adminpasswordUser != null)
        {
            Log.d("myTag", adminpasswordUser);

            if(adminpasswordUser.equals(adminpasswordDB))
            {
                switch(requestedChange)
                {
                    case EDIT_QUESTION:
                        openEditQuestion(question);
                        break;

                    case ADD_QUESTION:
                        openNewQuestion();
                        break;

                    case DELETE_QUESTION:
                        deleteQuestion(question);
                        break;
                }
            }

            else
            {
                Toast.makeText(getApplicationContext(), "Passwort falsch", Toast.LENGTH_SHORT).show();
                showStringInputDialog(MainActivityQuestionCatalog.this::onAdminPasswordEntered, requestedChange, question);
            }
        }
    }
    public interface inputTextCallback{
        void onAdminPasswordEntered(String adminpasswordUser,final CatalogueChange requestedChange,final QuestionQuestionCatalog question);
    };

    // Method to create and display the pop-up window
    private void showStringInputDialog(final inputTextCallback Callback, final CatalogueChange requestedChange, final QuestionQuestionCatalog question)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        View dialogView = inflater.inflate(R.layout.dialog_signin_admin, null);
        final EditText editText = dialogView.findViewById(R.id.edit_text_input);

        // Set the dialog title, view, and buttons
        builder.setTitle("Bitte geben Sie das Adminpasswort ein")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputstring = editText.getText().toString();
                        // Due to Asychronous processes we need to wait for the String Input
                        Callback.onAdminPasswordEntered(inputstring, requestedChange, question);
                        // Process the adminpasswordUser as needed -> See buttonToNewQuestionActivity.setOnClickListener
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void CheckCataloguePermission(final CatalogueChange requestedChange, final QuestionQuestionCatalog question)
    {
        //Hier wird der Spielerrang noch abgefragt

        // read playerrank
        boolean adminRequired = true; //readPlayerRank() // returns false if player rank to low

        if (adminRequired)
        {
            ReadadminpasswordDB(requestedChange, question);
        }

        else
        {
            switch(requestedChange)
            {
                case EDIT_QUESTION:
                    openEditQuestion(question);
                    break;

                case ADD_QUESTION:
                    openNewQuestion();
                    break;

                case DELETE_QUESTION:
                    deleteQuestion(question);
                    break;
            }
        }
    }
}

