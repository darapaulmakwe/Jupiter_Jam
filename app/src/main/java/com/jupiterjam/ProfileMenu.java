package com.jupiterjam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileMenu extends AppCompatActivity {

    private EditText userName;
    private TextView currentTheme;
    private ImageView themeImage;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupProfileView();

    }

    /**
     * Setting up the main layout of the profile menu.
     * Calls methods to handle each subsection of the menu, including setting up a listener
     * to activate the theme menu when the button is pressed.
     */
    private void setupProfileView(){
        setContentView(R.layout.profile_menu);

        prefs = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);

        handleUsername();

        // Button and listener to change to theme menu
        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setOnClickListener(v -> {
            setContentView(R.layout.theme_menu);
            setupThemeSelection();
        });

        displayWins();
        displayDeaths();
    }

    /**
     * Method handles saving the name the user enters when focus on the field is lost, ie the
     * user taps off keyboard, or leaves the current screen.
     * Note: this name is currently not used anywhere in particular.
     */
    private void handleUsername(){
        userName = findViewById(R.id.usernameInput);

        // Load existing name if it exists
        String savedName = prefs.getString("userName", "");
        userName.setText(savedName);

        // Save the name when focus is lost
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String name = userName.getText().toString().trim();
                    prefs.edit().putString("userName", name).apply();
                }
            }
        });
    }

    /**
     * Sets up listeners for the buttons to change the theme according to the parameters
     * passed. Initializes views for displaying the theme image and name. Loads already
     * obtained preferences or default resources and displays them initially.
     */
    private void setupThemeSelection() {
        themeImage = findViewById(R.id.ship);
        currentTheme = findViewById(R.id.currentTheme);

        // Set up listeners to change the users desired rocket theme based on what button is pressed
        findViewById(R.id.themeButton1).setOnClickListener(v ->
                changeTheme(R.drawable.jupiter_ship, "Jupiter"));

        findViewById(R.id.themeButton2).setOnClickListener(v ->
                changeTheme(R.drawable.earth_ship, "Earth"));

        findViewById(R.id.themeButton3).setOnClickListener(v ->
                changeTheme(R.drawable.mars_ship, "Mars"));

        findViewById(R.id.themeButton4).setOnClickListener(v ->
                changeTheme(R.drawable.neptune_ship, "Neptune"));

        findViewById(R.id.themeButton5).setOnClickListener(v ->
                changeTheme(R.drawable.saturn_ship, "Saturn"));

        findViewById(R.id.themeButton6).setOnClickListener(v ->
                changeTheme(R.drawable.moon_ship, "Moon"));

        // Load saved theme
        int savedThemeDrawable = prefs.getInt("selectedTheme", R.drawable.jupiter_ship);
        String savedThemeName = prefs.getString("selectedThemeName", "Jupiter");
        themeImage.setImageResource(savedThemeDrawable);
        currentTheme.setText(savedThemeName);
    }

    /**
     * Changes the players ship theme based on the parameters passed by which button is pressed.
     * Saves users preferences to be used in gameplay and when they return to the app each time.
     * @param themeDrawable : drawable the player has chosen to use as their avatar.
     * @param name : name of the theme to be displayed when choosing between themes.
     */
    private void changeTheme(int themeDrawable, String name) {
        // Update theme preview
        themeImage.setImageResource(themeDrawable);

        // Display updated theme name
        currentTheme = findViewById(R.id.currentTheme);
        currentTheme.setText(name);

        // Save to users SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selectedTheme", themeDrawable);
        editor.putString("selectedThemeName", name);
        editor.apply();
    }

    /**
     *  Both methods below are to display the users current stats of wins and loses(deaths).
     *  Gets players stats from their Shared Preferences that have been updated in the Gameplay class.
     */
    private void displayWins() {
        TextView playerWins = findViewById(R.id.playerWins);
        int totalWins = prefs.getInt("wins", 0);
        playerWins.setText("Wins: " + totalWins);
    }
    private void displayDeaths() {
        TextView playerDeaths = findViewById(R.id.playerDeaths);
        int totalDeaths = prefs.getInt("deaths", 0);
        playerDeaths.setText("Deaths: " + totalDeaths);
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }
    public void backToProfile(View myView) {
        setupProfileView();
    }


}
