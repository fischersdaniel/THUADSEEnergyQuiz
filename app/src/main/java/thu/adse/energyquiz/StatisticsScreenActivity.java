package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;

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
    private DatabaseReference databaseReference;

    private String rank;
    private int nextRank;

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

        String userId = "1";
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer score = dataSnapshot.child("score").getValue(Integer.class);
                    Integer totalCorrectAnswers = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                    Integer totalAnswers = dataSnapshot.child("totalAnswers").getValue(Integer.class);
                    String rank = dataSnapshot.child("Rank").getValue(String.class);

                    /*
                    if (score != null) {
                        textViewStatisticsPointsDB.setText(String.valueOf(score + " pt."));

                        String rank;
                        Integer nextRank;
                        if (score >= 1 && score < 500) {
                            rank = "Anfänger";
                            nextRank = 500;
                        } else if (score >= 500 && score < 1000) {
                            rank = "Halb-Profi";
                            nextRank = 1000;
                        } else {
                            rank = "Experte";
                            nextRank = 99999;
                        }
                        textViewStatisticsRankCalculated.setText(rank);
                        textViewStatisticsNextRankCalculated.setText(nextRank + " pt.");
                        databaseReference.child("Rank").setValue(rank);

                    } else {
                        textViewStatisticsPointsDB.setText("N/A");
                    }
                    */

                    // Check rank on the basis of the scoring system / user score
                    if(score < 20){
                        nextRank = 20;
                    }
                    else if(score < 50){
                        nextRank = 50;
                    }
                    else if(score < 100){
                        nextRank = 100;
                    }
                    else if(score < 200){
                        nextRank = 200;
                    }
                    else if(score < 500){
                        nextRank = 500;
                    }
                    else{
                        nextRank = 500; // not possible
                    }

                    textViewStatisticsPointsDB.setText(String.valueOf(score + " pt."));
                    textViewStatisticsRankCalculated.setText(rank);
                    textViewStatisticsNextRankCalculated.setText(nextRank + " pt.");


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
                        textViewStatisticsPointsDB.setText("ID not found");
                        textViewStatisticsCorrectAnswersDB.setText("ID not found");
                        textViewStatisticsTotalAnswersDB.setText("ID not found");
                        textViewQuoteCalculated.setText("ID not found");
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hier können bei Bedarf Aktionen für den Fall eines Abbruchs durchgeführt werden
            }
        });
    }
}


