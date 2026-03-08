package model;

public class UserProfile {

    private String displayName;

    public UserProfile() {
        this.displayName = "";
    }

    public UserProfile(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}