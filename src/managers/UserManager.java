package managers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.User;
import models.Mentor;
import models.Mentee;

/**
 * UserManager handles user account management for the LinkUp application.
 * This class provides functionality for user registration, authentication, and retrieval.
 */

public class UserManager {
    private List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    public User registerUser(Map<String, Object> userData, String role) throws Exception {
        String email = (String) userData.get("email");

        // Check if email is a Monash email
        if (!email.endsWith("@student.monash")) {
            throw new Exception("Only Monash student emails are allowed");
        }

        // Create appropriate user type
        User user;
        if (role.equals("mentor")) {
            user = new Mentor(
                    (String) userData.get("name"),
                    email,
                    (Integer) userData.get("age"),
                    (String) userData.get("location"),
                    (List<String>) userData.get("interests"),
                    (String) userData.get("background")
            );
        } else {
            user = new Mentee(
                    (String) userData.get("name"),
                    email,
                    (Integer) userData.get("age"),
                    (String) userData.get("location"),
                    (List<String>) userData.get("interests"),
                    (String) userData.get("background")
            );
        }

        users.add(user);
        return user;
    }

    public User login(String email, String pin) {
        // Simple authentication (in a real app, use secure methods)
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                // In a real app, verify PIN/password here
                return user;
            }
        }
        return null;
    }

    public User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}