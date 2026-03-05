package com.mhtracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testSuccessfulLogin() {
        User user = new User("admin", "password");

        boolean result = user.authenticate("admin", "password");

        assertTrue(result);
    }

    @Test
    public void testFailedLogin() {
        User user = new User("admin", "password");

        boolean result = user.authenticate("admin", "wrongpass");

        assertFalse(result);
    }
}