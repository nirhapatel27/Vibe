package com.cse110.flashbackmusicplayer;

import org.junit.Test;

import static org.junit.Assert.*;

public class DisplayNameTest {
    @Test
    public void getAnonName() throws Exception {

        String name = "";
        assertNotEquals(name, DisplayName.getAnonName(name));

        String name2 = "a";
        assertNotEquals(name2, DisplayName.getAnonName(name2));

        String name3 = "asdf";
        assertNotEquals(name3, DisplayName.getAnonName(name3));

        String name4 = "123456";
        assertNotEquals(name4, DisplayName.getAnonName(name4));

        assertNotEquals(DisplayName.getAnonName(name), DisplayName.getAnonName(name2));
        assertNotEquals(DisplayName.getAnonName(name3), DisplayName.getAnonName(name4));

        String name5 = "kate ala";
        assertEquals("viber_ ala", DisplayName.getAnonName(name5));

        String name6 = "ala unkown";
        assertEquals("fresh_kown", DisplayName.getAnonName(name6));

        String name7 = "mystica";
        assertEquals("walrus_tica", DisplayName.getAnonName(name7));

        String name8 = "a";
        assertEquals("fox_a", DisplayName.getAnonName(name8));

    }

}