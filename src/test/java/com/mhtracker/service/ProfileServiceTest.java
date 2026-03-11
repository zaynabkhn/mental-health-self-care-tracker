package com.mhtracker.service;

import com.mhtracker.model.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileServiceTest {

    private static final String FILE_NAME = "profile.txt";

    @AfterEach
    public void cleanup() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSaveAndLoadProfile() {
        ProfileService service = new ProfileService();
        UserProfile profile = new UserProfile("Zainab");

        service.saveProfile(profile);
        UserProfile loadedProfile = service.loadProfile();

        assertEquals("Zainab", loadedProfile.getDisplayName());
    }

    @Test
    public void testLoadProfileWhenFileDoesNotExist() {
        ProfileService service = new ProfileService();

        UserProfile loadedProfile = service.loadProfile();

        assertEquals("", loadedProfile.getDisplayName());
    }
}