package com.jupiterjam;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class AsteroidUnitTest {

    private FrameLayout fakeLayout;
    private Context mockContext;
    private Player mockPlayer;

    private final int screenHeight = 1920;
    private final int startX = 1080;

    @Before
    public void setUp() {
        mockContext = Mockito.mock(Context.class);
        fakeLayout = new FrameLayout(mockContext);
        mockPlayer = Mockito.mock(Player.class);
    }

    @Test
    public void asteroidCanBeHitWithinBounds() {
        Asteroid asteroid = new Asteroid(fakeLayout, startX, screenHeight, mockPlayer);
        setAsteroidSizeAndPosition(asteroid, 100f, 200f, 225, 163);

        // Hit inside bounds (100,200) → (325,363)
        assertTrue(asteroid.isHit(150f, 250f));
    }

    @Test
    public void asteroidCannotBeHitOutsideBounds() {
        Asteroid asteroid = new Asteroid(fakeLayout, startX, screenHeight, mockPlayer);
        setAsteroidSizeAndPosition(asteroid, 100f, 200f, 225, 163);

        // Hit outside bounds
        assertFalse(asteroid.isHit(500f, 600f));
    }

    @Test
    public void asteroidIsDestroyedWhenTakingEnoughDamage() {
        Asteroid asteroid = new Asteroid(fakeLayout, startX, screenHeight, mockPlayer);
        asteroid.takeDamage(1);
        assertTrue(asteroid.isDestroyed());
    }

    @Test
    public void asteroidIsNotDestroyedIfNotEnoughDamage() {
        Asteroid asteroid = new Asteroid(fakeLayout, startX, screenHeight, mockPlayer);
        asteroid.setHealth(2);
        asteroid.takeDamage(1);
        assertFalse(asteroid.isDestroyed());
    }

    // Helper method to set ImageView position and size
    private void setAsteroidSizeAndPosition(Asteroid asteroid, float x, float y, int width, int height) {
        ImageView view = (ImageView) ((FrameLayout) asteroidLayoutFromAsteroid(asteroid)).getChildAt(0);
        view.setX(x);
        view.setY(y);
        view.layout(0, 0, width, height); // Set dimensions so getWidth()/getHeight() return correct values
    }

    // Extract asteroidLayout from private field using reflection
    private FrameLayout asteroidLayoutFromAsteroid(Asteroid asteroid) {
        try {
            java.lang.reflect.Field field = Asteroid.class.getDeclaredField("asteroidLayout");
            field.setAccessible(true);
            return (FrameLayout) field.get(asteroid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}