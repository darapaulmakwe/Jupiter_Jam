package com.jupiterjam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndGame extends AppCompatActivity {

    private ImageView shipResult;
    private SharedPreferences prefs;
    private TextView gameResult;
    private TextView statCount;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game);

        prefs = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);
        Intent intent = getIntent();

        // Game result via boolean passed from GamePlay class.
        boolean playerWon = intent.getBooleanExtra("result", false);
        displayEndScreenResults(playerWon);
    }

    private void displayEndScreenResults(boolean playerWon) {
        gameResult = findViewById(R.id.gameResult);
        statCount = findViewById(R.id.statCount);
        shipResult = findViewById(R.id.shipResult);

        if (playerWon){
            gameResult.setText(R.string.you_won);
            int wins = prefs.getInt("wins", 0);
            statCount.setText("Enemies Defeated : " + wins);

            // Display players current ship
            int rocketResId = prefs.getInt("selectedTheme", R.drawable.jupiter_ship);
            shipResult.setImageResource(rocketResId);

        }
        else {
            gameResult.setText(R.string.game_over);
            statCount.setText("Your ship exploded!");
            shipResult.setImageResource(R.drawable.explosion);
        }
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }
}
