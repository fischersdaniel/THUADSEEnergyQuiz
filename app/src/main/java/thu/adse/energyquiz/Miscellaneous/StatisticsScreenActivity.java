package thu.adse.energyquiz.Miscellaneous;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import thu.adse.energyquiz.R;
import thu.adse.energyquiz.SinglePlayer.SinglePlayerGameActivity;

public class StatisticsScreenActivity extends AppCompatActivity {

    // UI elements
    private TextView textViewStatisticsRankCalculated;
    private TextView textViewStatisticsPointsDB;
    private TextView textViewStatisticsCorrectAnswersDB;
    private TextView textViewStatisticsTotalAnswersDB;
    private TextView textViewQuoteCalculated;
    private TextView textViewStatisticsNextRankCalculated;
    private DatabaseReference usersDatabaseReference;
    private PieChart pieChart1Statistics;
    private List <PieEntry> pieEntryList;

    // User statistics variables
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

        // Back button click listener
        CardView cardViewStatisticsBack = findViewById(R.id.cardViewStatisticsBack);
        cardViewStatisticsBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });

        // Initialize PieChart data
        pieEntryList = new ArrayList<>();

        // Find UI elements
        textViewStatisticsRankCalculated = findViewById(R.id.textViewStatisticsRankCalculated);
        textViewStatisticsPointsDB = findViewById(R.id.textViewStatisticsPointsDB);
        textViewStatisticsCorrectAnswersDB = findViewById(R.id.textViewStatisticsCorrectAnswersDB);
        textViewStatisticsTotalAnswersDB = findViewById(R.id.textViewStatisticsTotalAnswersDB);
        textViewQuoteCalculated = findViewById(R.id.textViewQuoteCalculated);
        textViewStatisticsNextRankCalculated = findViewById(R.id.textViewStatisticsNextRankCalculated);
        pieChart1Statistics = findViewById(R.id.pieChart1Statistics);

        // Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();  // Use the UID (e.g., store it in a variable)
            Log.d("current User", "succesfully getting userID:" + userId);
        } else {
            Log.d("Current User", "No current User - Signup first");
        }

        // Database reference setup
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        // ValueEventListener to listen for changes in the user's statistics in the Firebase database
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user statistics from the database
                    score = dataSnapshot.child("score").getValue(Integer.class);
                    totalCorrectAnswers = dataSnapshot.child("totalCorrectAnswers").getValue(Integer.class);
                    totalAnswers = dataSnapshot.child("totalAnswers").getValue(Integer.class);
                    rank = dataSnapshot.child("rank").getValue(String.class);

                    // Update UI based on retrieved user statistics
                    if (score != null) {
                        textViewStatisticsPointsDB.setText(String.valueOf(score + " pkt."));

                        // Determine and calculate user rank and next rank based on the user score
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
                            nextRank = 500; // not possible
                        }
                        textViewStatisticsRankCalculated.setText(rank);
                        textViewStatisticsNextRankCalculated.setText(nextRank + " pkt.");

                    } else {
                        textViewStatisticsPointsDB.setText(R.string.NA);
                    }

                    // Display total correct answers, total answers
                    if (totalCorrectAnswers != null) {
                        textViewStatisticsCorrectAnswersDB.setText(String.valueOf(totalCorrectAnswers));
                    } else {
                        textViewStatisticsCorrectAnswersDB.setText(R.string.NA);
                    }

                    if (totalAnswers != null) {
                        textViewStatisticsTotalAnswersDB.setText(String.valueOf(totalAnswers));
                    } else {
                        textViewStatisticsTotalAnswersDB.setText(R.string.NA);
                    }

                    // Determine and calculate user answer accuracy
                    if (totalCorrectAnswers != null && totalAnswers != null) {
                        double quote = (double) totalCorrectAnswers / totalAnswers * 100;
                        textViewQuoteCalculated.setText(String.format(Locale.GERMANY, "%.1f%%", quote));

                        // Update PieChart and display it
                        setValues();
                        setUpChart();
                    } else {
                        textViewQuoteCalculated.setText(R.string.NA);
                    }
                }

                // User data empty or not found in the database
                else {
                        textViewStatisticsPointsDB.setText(R.string.IDNF);
                        textViewStatisticsCorrectAnswersDB.setText(R.string.IDNF);
                        textViewStatisticsTotalAnswersDB.setText(R.string.IDNF);
                        textViewQuoteCalculated.setText(R.string.IDNF);
                        textViewStatisticsRankCalculated.setText(R.string.IDNF);
                        textViewStatisticsNextRankCalculated.setText(R.string.IDNF);
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Actions to be performed in case of cancellation
            }
        });
    }

    // Set up the PieChart with the user's correct and incorrect answers
    private void setUpChart() {
        Log.d("0 setupchart", "im setupchart");
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
        PieData pieData = new PieData(pieDataSet);

        // Create Data Array and Define colors for correct and incorrect answers
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(StatisticsScreenActivity.this, R.color.rightbg)); // R.color.greenColor sollte in Ihren Ressourcen definiert sein
        colors.add(ContextCompat.getColor(StatisticsScreenActivity.this, R.color.wrongbg)); // R.color.redColor sollte in Ihren Ressourcen definiert sein

        // Set up PieChart properties
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(ContextCompat.getColor(StatisticsScreenActivity.this, R.color.white));
        pieData.setValueTextSize(12f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieChart1Statistics.setData(pieData);
        pieChart1Statistics.invalidate();
        pieChart1Statistics.getDescription().setEnabled(false);

        // Set up legend for the PieChart
        Legend legend = pieChart1Statistics.getLegend();
        legend.setDrawInside(false);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

    }

    // Populate the PieChart with the user's correct and incorrect answers
    private void setValues() {

        if (totalCorrectAnswers != null) {
            pieEntryList.add(new PieEntry(totalCorrectAnswers,"Richtig"));
        } else {
            // Do nothing
        }

        if (totalAnswers != null && totalCorrectAnswers != null) {
            pieEntryList.add(new PieEntry(totalAnswers-totalCorrectAnswers,"Falsch"));
        } else {
            // Do nothing
        }
    }
}