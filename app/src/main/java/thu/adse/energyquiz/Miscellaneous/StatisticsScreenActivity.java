package thu.adse.energyquiz.Miscellaneous;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import thu.adse.energyquiz.R;

public class StatisticsScreenActivity extends AppCompatActivity {

    private TextView textViewStatisticsRankCalculated;
    private TextView textViewStatisticsPointsDB;
    private TextView textViewStatisticsCorrectAnswersDB;
    private TextView textViewStatisticsTotalAnswersDB;
    private TextView textViewQuoteCalculated;
    private TextView textViewStatisticsNextRankCalculated;
    private DatabaseReference usersDatabaseReference;
    private PieChart pieChart1Statistics;
    private List <PieEntry> pieEntryList;

    private Integer score;
    private Integer nextRank;
    private String rank;
    private String userId;

    public Integer totalCorrectAnswers;
    public Integer totalAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_screen);

        CardView cardViewStatisticsBack = findViewById(R.id.cardViewStatisticsBack);
        cardViewStatisticsBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        pieEntryList = new ArrayList<>();

        textViewStatisticsRankCalculated = findViewById(R.id.textViewStatisticsRankCalculated);
        textViewStatisticsPointsDB = findViewById(R.id.textViewStatisticsPointsDB);
        textViewStatisticsCorrectAnswersDB = findViewById(R.id.textViewStatisticsCorrectAnswersDB);
        textViewStatisticsTotalAnswersDB = findViewById(R.id.textViewStatisticsTotalAnswersDB);
        textViewQuoteCalculated = findViewById(R.id.textViewQuoteCalculated);
        textViewStatisticsNextRankCalculated = findViewById(R.id.textViewStatisticsNextRankCalculated);
        pieChart1Statistics = findViewById(R.id.pieChart1Statistics);

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
                    score = dataSnapshot.child("score").getValue(Integer.class);
                    totalCorrectAnswers = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                    totalAnswers = dataSnapshot.child("totalAnswers").getValue(Integer.class);
                    rank = dataSnapshot.child("rank").getValue(String.class);

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
                        Log.d("3 totalCorrectAnswers ondata", "Der Wert von totalCorrectAnswers ist: " + totalCorrectAnswers);
                        Log.d("3 totalAnswers ondata", "Der Wert von totalAnswers ist: " + totalAnswers);
                        setValues();
                        setUpChart();
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

    private void setUpChart() {
        Log.d("0 setupchart", "im setupchart");
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Richtig und Falsch beantwortete Fragen");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieData.setValueTextSize(12f);
        pieChart1Statistics.setData(pieData);
        pieChart1Statistics.invalidate();
        pieChart1Statistics.getDescription().setEnabled(false);
    }

    private void setValues() {
        Log.d("0 totalCorrectAnswers", "Der Wert von totalCorrectAnswers ist: " + totalCorrectAnswers);// Do nothing
        if (totalCorrectAnswers != null) {
            pieEntryList.add(new PieEntry(totalCorrectAnswers,"Richtig"));
            Log.d("0 totalCorrectAnswers if", "Der Wert von totalCorrectAnswers ist: " + totalCorrectAnswers);// Do nothing
        } else {
            Log.d("0 totalCorrectAnswers else", "Der Wert von totalCorrectAnswers ist: " + totalCorrectAnswers);// Do nothing
        }
        if (totalAnswers != null && totalCorrectAnswers != null) {
            Log.d("0 totalAnswers", "Der Wert von totalAnswers ist: " + totalAnswers);
            pieEntryList.add(new PieEntry(totalAnswers-totalCorrectAnswers,"Falsch"));
        } else {
            Log.d("0 totalAnswers if", "Der Wert von totalAnswers ist: " + totalAnswers);/// Do nothing
            Log.d("0 totalCorrectAnswers else", "Der Wert von totalCorrectAnswers ist: " + totalCorrectAnswers);// Do nothing
        }
    }
}