package com.jupiterjam;

import org.junit.Test;
import static org.junit.Assert.*;

public class AsteroidUnitTest {

    /**
     * Simple Asteroid placeholder class to simulate Asteroid function for testing.
     */
    public static class TestAsteroid {
        private int health = 1;
        private boolean destroyed = false;
        private float x = 100, y = 100; // position of "Bullet"
        private float width = 200, height = 150; // Parameters of AsteroidView

        public boolean isHit(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }

        public void takeDamage(int dmg) {
            health -= dmg;
            if (health <= 0) destroyed = true;
        }

        public void setHealth(int h) {
            health = h;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }

    @Test
    public void asteroidCanBeHitWithinBounds() {
        TestAsteroid a = new TestAsteroid();
        assertTrue(a.isHit(150f, 120f));
    }

    @Test
    public void asteroidCannotBeHitOutsideBounds() {
        TestAsteroid a = new TestAsteroid();
        assertFalse(a.isHit(350f, 400f));
    }

    @Test
    public void asteroidIsDestroyedWhenTakingEnoughDamage() {
        TestAsteroid a = new TestAsteroid();
        a.takeDamage(1);
        assertTrue(a.isDestroyed());
    }

    @Test
    public void asteroidIsNotDestroyedIfNotEnoughDamage() {
        TestAsteroid a = new TestAsteroid();
        a.setHealth(2);
        a.takeDamage(1);
        assertFalse(a.isDestroyed());
    }
}