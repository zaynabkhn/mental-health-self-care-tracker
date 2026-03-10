package com.mhtracker.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/* A Data Access Object (DAO) is spoecifically for "talking" to the SQLite database.
* The User.java model shows what User objects contain, but UserDAO handles more complex
* tasks. 
*/
public class UserDAO 
{

    public static boolean authenticate(String username, String password) 
    {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true if a matching user exists

        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    //ADD createUser() HERE LATER!
}
