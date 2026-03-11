package com.mhtracker.model;

public class Session 
{

    private static String loggedInUsername;

    public static void setLoggedInUsername(String username) 
    {
        loggedInUsername = username;
    }

    public static String getLoggedInUsername() 
    {
        return loggedInUsername;
    }

    public static void clear() 
    {
        loggedInUsername = null;
    }
}
