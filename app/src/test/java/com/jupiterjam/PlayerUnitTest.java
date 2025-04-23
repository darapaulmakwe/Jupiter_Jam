package com.jupiterjam;
import android.widget.ImageView;
import android.widget.ProgressBar;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class PlayerUnitTest {
    private Player player;

    @Before
    public void setUp() {
        player = new Player(null, null, null);
    }

    @Test
    public void testInitialHealth() {
        assertEquals(100, player.getHealth());
    }

    @Test
    public void testGotHitReducesHealth() {
        player.gotHit();
        assertEquals(95, player.getHealth());
    }

    @Test
    public void testHealIncreasesHealth() {
        player.gotHit(); player.gotHit(); player.gotHit(); // down to 85
        player.heal(); // +30 → 115, but capped at 100
        assertEquals(100, player.getHealth());
    }

    @Test
    public void testBulletSpeedNormal() {
        Bullet bullet = player.shoot();
        assertEquals(-20, bullet.getSpeed());
    }

    @Test
    public void testBulletSpeedBoosted() {
        player.activateBulletBoost();
        Bullet bullet = player.shoot();
        assertEquals(-50, bullet.getSpeed());
    }

    @Test
    public void testFlameModeActivation() {
        player.activateFlameMode();
        assertTrue(player.getFlameModeStatus());
    }
}
