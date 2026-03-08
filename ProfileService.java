package service;

import model.UserProfile;
import java.io.*;

public class ProfileService {

    private static final String FILE_NAME = "profile.txt";

    public void saveProfile(UserProfile profile) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(FILE_NAME))) {

            writer.write(profile.getDisplayName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserProfile loadProfile() {

        UserProfile profile = new UserProfile();

        File file = new File(FILE_NAME);

        if (!file.exists())
            return profile;

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(FILE_NAME))) {

            String name = reader.readLine();

            if (name != null)
                profile.setDisplayName(name);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return profile;
    }
}