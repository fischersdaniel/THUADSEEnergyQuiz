package thu.adse.energyquiz.QuestionCatalog;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import thu.adse.energyquiz.HomeScreenActivity;
import thu.adse.energyquiz.R;

public class MainActivityQuestionCatalog<LoginDialogFragment> extends AppCompatActivity implements RecyclerViewInterfaceQuestionCatalog {

    RecyclerView recyclerView;
    DatabaseReference database;
    DatabaseReference databaseAdmin;
    DatabaseReference databaseRank;
    QuestionAdapterQuestionCatalog questionAdapter;
    ArrayList<QuestionQuestionCatalog> list;
    Dialog dialog;
    Button buttonToNewQuestionActivity, buttonBackToMenu, buttonDialogEdit, buttonDialogDelete, buttonDialogCancel;

    String adminpasswordDB;

    Boolean playerrankacess = false;
    Boolean playerrankreceived = false;

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

        buttonBackToMenu = findViewById(R.id.buttonBackToMenu);
        buttonBackToMenu.setOnClickListener(view -> backToMenu());

        dialog = new Dialog(MainActivityQuestionCatalog.this);
        dialog.setContentView(R.layout.edit_delete_dialog_box_question_catalog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.edit_delete_dialog_drawable_question_catalog));
        dialog.setCancelable(true);


        buttonDialogEdit = dialog.findViewById(R.id.buttonDialogEdit);
        buttonDialogDelete = dialog.findViewById(R.id.buttonDialogDelete);
        buttonDialogCancel = dialog.findViewById(R.id.buttonDialogCancel);

        buttonDialogCancel.setOnClickListener((View view) -> {
            dialog.dismiss();
        });

        buttonDialogEdit.setOnClickListener((View view) -> {
            CheckCataloguePermission(CatalogueChange.EDIT_QUESTION, selectedQuestion);
            // openEditQuestion(selectedQuestion);
            dialog.dismiss();
        });

        buttonDialogDelete.setOnClickListener(view -> {
            CheckCataloguePermission(CatalogueChange.DELETE_QUESTION, selectedQuestion);
            // deleteQuestion(selectedQuestion);
            dialog.dismiss();
            //Optional: Dialog: "Sind Sie sicher, dass Sie die Frage löschen wollen?"
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
        //Hier wird der Spielerrang abgefragt
        // read playerrank
        boolean adminRequired = true; // CheckPlayerRank(); // returns false if player rank to low
        /*CheckPlayerRank();
        while (playerrankreceived == false)
        {
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        playerrankreceived = false;
        adminRequired = playerrankacess;*/
        if (adminRequired = true)
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
    public void CheckPlayerRank(){
        //Hier werden lokale Variablen definiert
        String userId = "";

        //Hier werden Konstanten definiert
        final String ADMINRANK = "Experte";
        //Hier werden Datenbankinstanzen initialisiert
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Hier lesen wir den "rank" des Benutzers mit der angegebenen Benutzer-ID aus
        if (currentUser == null)
        {
            Toast.makeText(getApplicationContext(), "Bitte anmelden!", Toast.LENGTH_LONG).show(); // Es ist kein Benutzer angemeldet
            Log.d("current User", "Bitte Anmelden");
        }
        else
        {
            userId = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
            Log.d("current User", "succesfully getting userID:" + userId);
            //DB Path definieren

            databaseRank = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users/"); //.child("Users").child(userId);
            //databaseRank.child("Users").child(userId).child("Status").setValue("active");
            String finalUserId = userId;
            databaseRank.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String rank = snapshot.getValue().toString();
                            Log.d("UserRank", "UserRank:" + rank);
                        }
                        else
                        {
                            Log.d("UserRankError", "Keinen Datensatz gefunden");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("UserRankError", "Keinen Datensatz gefunden");
                    }
                });
            /*databaseRank = FirebaseDatabase.getInstance("https://energyquizdb-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference().child("Users").child(userId);
            //databaseRank.child("Test").setValue("Test");

            databaseRank.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Integer score = dataSnapshot.child("score").getValue(Integer.class);
                        Integer totalCorrectAnswers = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                        Integer totalAnswers = dataSnapshot.child("totalAnswers").getValue(Integer.class);
                        String rank = dataSnapshot.child("rank").getValue(String.class);

                        if (score != null) {

                            if (score >= 0 && score < 20)
                            {
                                rank = getString(R.string.userRank_0);
                                Log.d("UserRank", rank);
                            }
                            else if (score < 50)
                            {
                                rank = getString(R.string.userRank_1);
                                Log.d("UserRank", rank);
                            }
                            else if (score < 100)
                            {
                                rank = getString(R.string.userRank_2);
                                Log.d("UserRank", rank);
                            }
                            else if (score < 200)
                            {
                                rank = getString(R.string.userRank_3);
                                Log.d("UserRank", rank);
                            }
                            else if (score < 500)
                            {
                                rank = getString(R.string.userRank_4);
                                Log.d("UserRank", rank);
                            }
                            else
                            {
                                rank = getString(R.string.userRank_5);
                                Log.d("UserRank", rank);
                            }
                            //usersDatabaseReference.child("rank").setValue(rank);

                        }
                        else
                        {
                            Log.d("UserRankError", "Keinen Datensatz gefunden");
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
                }
            });*/
        }

    }
}


/*databaseRank = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


            //Hier wird der Rank des aktuell angemeldeten Users ausgelesen
            databaseRank.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        String userIdRank = dataSnapshot.child("rank").getValue(String.class);
                        Log.d("Userrank", "Rang des Benutzers: " + userIdRank);
                        if (userIdRank.equals(ADMINRANK)){
                            playerrankacess = true;
                            playerrankreceived = true;
                        }
                        else {
                            playerrankacess = false;
                            playerrankreceived = true;
                        }
                    }
                    else
                    {
                        Log.d("Error", "Kein Nutzer gefunden");
                        playerrankacess = false;
                        playerrankreceived = true;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("Error", "Kein Nutzer gefunden"+ databaseError.getMessage());
                    playerrankacess = false;
                    playerrankreceived = true;
                }
            });
        }*/