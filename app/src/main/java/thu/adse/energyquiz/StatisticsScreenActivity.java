package thu.adse.energyquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

public class StatisticsScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_screen);

        CardView cardViewStatisticsBack = findViewById(R.id.cardViewStatisticsBack);
        cardViewStatisticsBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        });
    }
}