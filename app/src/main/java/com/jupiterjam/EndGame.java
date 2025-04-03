package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game);
    }

    public void endGame(boolean result){

        TextView gameResult;
        TextView statCount;
        gameResult = findViewById(R.id.gameResult);
        statCount = findViewById(R.id.statCount);

        if(result){
            gameResult.setText(R.string.you_won);
            //player.addWin(); TODO: method in player class to add a win to players stats
            //statCount.setText("Wins " + player.wins);
            //shipResult.setImage(player.customization.getThemeImage());
        }
        else {
            gameResult.setText(R.string.game_over);
            //player.addLoss(); TODO: method in player class to add a loss to players stats
            //statCount.setText("Loses " + player.loses);
            //shipResult.setImage(explosion image);
        }
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }
}
