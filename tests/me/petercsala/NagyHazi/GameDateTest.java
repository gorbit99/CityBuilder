package me.petercsala.NagyHazi;

import org.junit.Assert;
import org.junit.Test;

public class GameDateTest {

    @Test
    public void getDateString() {
        GameDate date = new GameDate();
        date.advance(11);
        String str = date.getDateString();

        Assert.assertEquals("2000.05.29", str);
    }
}