package thu.adse.energyquiz.QuestionCatalog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import thu.adse.energyquiz.Miscellaneous.HomeScreenActivity;
import thu.adse.energyquiz.R;
import thu.adse.energyquiz.UserManagement.LoginActivity;

/**
 * This class is used to display the questions in the question catalog and to add, edit or delete questions.
 * @author Sebastian Steinhauser
 * To edit or create a new question, the user must have a certain rank or enter the admin password.
 * @author Johannes Klever
 */

public class MainActivityQuestionCatalog extends AppCompatActivity implements RecyclerViewInterfaceQuestionCatalog {

    RecyclerView recyclerView;
    DatabaseReference database;
    DatabaseReference databaseAdmin;
    DatabaseReference databaseRank;
    QuestionAdapterQuestionCatalog questionAdapter;
    ArrayList<QuestionQuestionCatalog> list;
    Dialog dialog;
    CardView cardViewMainCatalogBack, cardViewMainCatalogAddQuestion ;
    CardView cardViewPopUpEDEdit, cardViewPopUpEDDelete, cardViewPopUpEDCancel;

    String adminpasswordDB;
    public Integer ADMINSCORE = 100;
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
        setContentView(R.layout.activity_catalog_main);

        recyclerView = findViewById(R.id.recyclerViewQuestionDb);
        database = FirebaseDatabase.getInstance().getReference("Questions");
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


        cardViewMainCatalogAddQuestion = findViewById(R.id.cardViewMainCatalogAddQuestion);


        cardViewMainCatalogAddQuestion.setOnClickListener(view ->
                CheckCataloguePermission(CatalogueChange.ADD_QUESTION, null));

        cardViewMainCatalogBack = findViewById(R.id.cardViewMainCatalogBack);
        cardViewMainCatalogBack.setOnClickListener(view -> backToMenu());


        dialog = new Dialog(MainActivityQuestionCatalog.this);
        dialog.setContentView(R.layout.dialog_catalog_choose_action);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        cardViewPopUpEDEdit = dialog.findViewById(R.id.cardViewPopUpEDEdit);
        cardViewPopUpEDDelete = dialog.findViewById(R.id.cardViewPopUpEDDelete);
        cardViewPopUpEDCancel = dialog.findViewById(R.id.cardViewPopUpEDCancel);

        cardViewPopUpEDCancel.setOnClickListener((View view) -> dialog.dismiss());

        cardViewPopUpEDEdit.setOnClickListener((View view) -> {
            CheckCataloguePermission(CatalogueChange.EDIT_QUESTION, selectedQuestion);
            dialog.dismiss();
        });

        cardViewPopUpEDDelete.setOnClickListener(view -> {
            CheckCataloguePermission(CatalogueChange.DELETE_QUESTION, selectedQuestion);
            dialog.dismiss();
        });
    }

    /**
     * This method is used to open the activity to edit a question.
     * @author Sebastian Steinhauser
     *
     * @param question The question that should be edited.
     */
    public void openEditQuestion(QuestionQuestionCatalog question) {
        Intent intent = new Intent(MainActivityQuestionCatalog.this, EditQuestionQuestionCatalog.class);
        intent.putExtra("selectedQuestion", question);
        startActivity(intent);
    }

    /**
     * This method is used to open the activity to add a new question to the question catalog.
     * @author Sebastian Steinhauser
     */
    public void openNewQuestion() {
        Intent intent = new Intent(MainActivityQuestionCatalog.this, NewQuestionQuestionCatalog.class);
        startActivity(intent);
    }

    /**
     * This method is used to return to the home screen activity.
     * @author Sebastian Steinhauser
     */
    public void backToMenu(){
        Intent intent = new Intent(MainActivityQuestionCatalog.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // L.B.: apply custom transition
    }

    /**
     * This method is used to display the dialog window for editing or deleting a question.
     * It is called when the user clicks on a question in the question catalog.
     * @author Sebastian Steinhauser
     *
     * @param position The position of the question in the question catalog.
     * @param question The question that was clicked on.
     */
    @Override
    public void onItemClick(int position, QuestionQuestionCatalog question) {
        selectedQuestion=question;
        dialog.show();
    }

    /**
     * This method is used to delete a question from the question catalog.
     * @author Sebastian Steinhauser
     *
     * @param question The question that should be deleted.
     */
    public void deleteQuestion(QuestionQuestionCatalog question) {
        database.child(question.getKey()).removeValue();
    }

    public void ReadadminpasswordDB(CatalogueChange requestedChange, QuestionQuestionCatalog question){
        //The Admin password defined in the DB is read
        //DB Path is defined
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.e("myTag", "Error reading data: " + error.getMessage());
            }
        });
    }
    public void onAdminPasswordEntered(String adminpasswordUser, CatalogueChange requestedChange, QuestionQuestionCatalog question){
        //the method checks the entered adminpassword. If entered correctly the corresponding case will be triggered (edit, add or delete).
        //If the password is wrong, the user will be noticed via a toast.
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
    }

    // Method to create and display the pop-up window
    private void showStringInputDialog(final inputTextCallback Callback, final CatalogueChange requestedChange, final QuestionQuestionCatalog question)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        View dialogView = inflater.inflate(R.layout.dialog_catalog_admin_login, null);
        final EditText editText = dialogView.findViewById(R.id.edit_text_input);

        // Set the dialog title, view, and buttons
        builder.setTitle("Erst ab " + ADMINSCORE + " Punkten verfügbar. Bitte Passwort eingeben:")
                .setView(dialogView)
                .setPositiveButton("Bestätigen", (dialog, which) -> {
                    String inputstring = editText.getText().toString();
                    // Due to Asychronous processes we need to wait for the String Input
                    Callback.onAdminPasswordEntered(inputstring, requestedChange, question);
                    // Process the adminpasswordUser as needed -> See buttonToNewQuestionActivity.setOnClickListener
                })
                .setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());

        // L.B.: Create and show the AlertDialog and make buttons visible by setting color to black
        AlertDialog dialog = builder.create();
        dialog.show();
        Button bpos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button bneg = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bpos.setTextColor(Color.BLACK);
        bneg.setTextColor(Color.BLACK);
    }

    public void CheckCataloguePermission(final CatalogueChange requestedChange, final QuestionQuestionCatalog question)
    {
       // CheckPlayerRank(); returns false if player rank to low
        CheckPlayerRank(requestedChange, question);
    }
    public void CheckPlayerRank(CatalogueChange requestedChange, QuestionQuestionCatalog question){
        //local defnition of variables
        String userId;

        /* DB is initalized */
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if the user is not logged in, a toast will inform the user to log in
        if (currentUser == null)
        {
            Toast.makeText(MainActivityQuestionCatalog.this, getString(R.string.userNotLoggedIn), Toast.LENGTH_SHORT).show();
            Log.d("current User", "Bitte Anmelden");
            startActivity(new Intent(MainActivityQuestionCatalog.this, LoginActivity.class));
            return;
        }
        else
        {
            //if the user is logged in, the userId is saved
            userId = currentUser.getUid();
            Log.d("current User", "succesfully getting userID:" + userId);
        }
        databaseRank = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseRank.addListenerForSingleValueEvent(new ValueEventListener() {
            //It is checked if the userrank is equivalent to the adminrank
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Integer score = snapshot.child("score").getValue(Integer.class);
                    Log.d("UserRank", "UserRank:" + score);

                    if (score >= (ADMINSCORE)){
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
                    else {
                        //Rank is not high enough, adminpassword required
                        ReadadminpasswordDB(requestedChange, question);
                    }
                }
                else
                {
                    //if no user snapshot can be gernerated the adminpassword shall be triggered
                    Log.d("UserRankError", "Keinen Datensatz gefunden");
                    //adminpassword required
                    ReadadminpasswordDB(requestedChange, question);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserRankError", "Keinen Datensatz gefunden");
                //Adminpassword will be read and triggered
                ReadadminpasswordDB(requestedChange, question);
            }
        });
    }
}