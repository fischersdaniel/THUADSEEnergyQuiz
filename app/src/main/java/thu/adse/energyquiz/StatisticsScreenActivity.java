package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticsScreenActivity extends AppCompatActivity {

    private TextView textViewStatisticsRankCalculated;
    private TextView textViewStatisticsPointsDB;
    private TextView textViewStatisticsCorrectAnswersDB;
    private TextView textViewStatisticsTotalAnswersDB;
    private TextView textViewQuoteCalculated;
    private TextView textViewStatisticsNextRankCalculated;
    private DatabaseReference usersDatabaseReference;

    private Integer nextRank;
    private String rank;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_screen);

        CardView cardViewStatisticsBack = findViewById(R.id.cardViewStatisticsBack);
        cardViewStatisticsBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        textViewStatisticsRankCalculated = findViewById(R.id.textViewStatisticsRankCalculated);
        textViewStatisticsPointsDB = findViewById(R.id.textViewStatisticsPointsDB);
        textViewStatisticsCorrectAnswersDB = findViewById(R.id.textViewStatisticsCorrectAnswersDB);
        textViewStatisticsTotalAnswersDB = findViewById(R.id.textViewStatisticsTotalAnswersDB);
        textViewQuoteCalculated = findViewById(R.id.textViewQuoteCalculated);
        textViewStatisticsNextRankCalculated = findViewById(R.id.textViewStatisticsNextRankCalculated);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d("current User", "Test");

        if (currentUser != null) {
            userId = currentUser.getUid();  // Verwende die UID (z. B. speichere sie in einer Variable)
            Log.d("current User", "succesfully getting userID:" + userId);
        } else {
            textViewStatisticsNextRankCalculated.setText("Bitte Anmelden");   // Es ist kein Benutzer angemeldet
            Log.d("current User", "Bitte Anmelden");
        }


//        String userId = "uS1tAnKecagUmZOCXjcq5KlSws72";
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer score = dataSnapshot.child("score").getValue(Integer.class);
                    Integer totalCorrectAnswers = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                    Integer totalAnswers = dataSnapshot.child("totalAnswers").getValue(Integer.class);
                    String rank = dataSnapshot.child("rank").getValue(String.class);

                    if (score != null) {
                        textViewStatisticsPointsDB.setText(String.valueOf(score + " pt."));

                        if (score >= 0 && score < 20) {
                            rank = getString(R.string.userRank_0);
                            nextRank = 20;
                        }
                        else if (score < 50) {
                            rank = getString(R.string.userRank_1);
                            nextRank = 50;
                        }
                        else if (score < 100) {
                            rank = getString(R.string.userRank_2);
                            nextRank = 100;
                        }
                        else if (score < 200) {
                            rank = getString(R.string.userRank_3);
                            nextRank = 200;
                        }
                        else if (score < 500) {
                            rank = getString(R.string.userRank_4);
                            nextRank = 500;
                        }
                        else{
                            rank = getString(R.string.userRank_5);
                            nextRank = 500; // nicht möglich
                        }
                        textViewStatisticsRankCalculated.setText(rank);
                        textViewStatisticsNextRankCalculated.setText(nextRank + " pt.");
                        //usersDatabaseReference.child("rank").setValue(rank);

                    } else {
                        textViewStatisticsPointsDB.setText("N/A");
                    }

                    if (totalCorrectAnswers != null) {
                        textViewStatisticsCorrectAnswersDB.setText(String.valueOf(totalCorrectAnswers));
                    } else {
                        textViewStatisticsCorrectAnswersDB.setText("N/A");
                    }

                    if (totalAnswers != null) {
                        textViewStatisticsTotalAnswersDB.setText(String.valueOf(totalAnswers));
                    } else {
                        textViewStatisticsTotalAnswersDB.setText("N/A");
                    }

                    if (totalCorrectAnswers != null && totalAnswers != null) {
                        double quote = (double) totalCorrectAnswers / totalAnswers * 100;
                        textViewQuoteCalculated.setText(String.format("%.1f%%", quote));
                    } else {
                        textViewQuoteCalculated.setText("N/A / Null");
                    }
                }

                else {
                        textViewStatisticsPointsDB.setText("ID nicht gefunden");
                        textViewStatisticsCorrectAnswersDB.setText("ID nicht gefunden");
                        textViewStatisticsTotalAnswersDB.setText("ID nicht gefunden");
                        textViewQuoteCalculated.setText("ID nicht gefunden");
                        textViewStatisticsRankCalculated.setText("ID nicht gefunden");
                        textViewStatisticsNextRankCalculated.setText("ID nicht gefunden");
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });
    }
}


