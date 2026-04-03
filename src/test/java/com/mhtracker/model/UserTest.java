package com.mhtracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @BeforeEach
    public void setup() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:file:memdb_user?mode=memory&cache=shared");
        Database.useTestConnection(conn);
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");
        Database.initialize();

        conn.createStatement().execute("DELETE FROM users;");
    }

    @Test
    public void testSuccessfulLoginOnUserModel() {
        User user = new User("admin", "password");

        boolean result = user.authenticate("admin", "password");

        assertTrue(result);
    }

    @Test
    public void testFailedLoginOnUserModel() {
        User user = new User("admin", "password");

        boolean result = user.authenticate("admin", "wrongpass");

        assertFalse(result);
    }

    @Test
    public void testCreateAndLoginWithUserDAO() {
        boolean created = UserDAO.createUser("testuser", "123");
        assertTrue(created);

        boolean loginSuccess = UserDAO.authenticate("testuser", "123");
        assertTrue(loginSuccess);

        boolean loginFail = UserDAO.authenticate("testuser", "wrong");
        assertFalse(loginFail);
    }

    @Test
    public void testUsernameExists() {
        UserDAO.createUser("existingUser", "abc");

        assertTrue(UserDAO.usernameExists("existingUser"));
        assertFalse(UserDAO.usernameExists("newUser"));
    }

    @Test
    public void testDuplicateUsernameNotAllowed() {
        boolean firstCreate = UserDAO.createUser("sameuser", "123");
        boolean secondCreate = UserDAO.createUser("sameuser", "456");

        assertTrue(firstCreate);
        assertFalse(secondCreate);
    }
}