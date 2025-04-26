package managers;

import models.User;
import models.Mentor;
import models.Mentee;
import models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseManager provides an in-memory database implementation for the LinkUp application.
 * This class manages persistent storage of users, mentors, mentees, and groups.
 *
 * Note: This is a temporary in-memory implementation.
 * In a production environment, this would be replaced with a real database solution
 * (such as MySQ or something else) for data persistence across application restarts.
 *
 */

public class DatabaseManager {
    // In-memory storage
    private final Map<String, User> users;
    private final Map<String, Group> groups;

    public DatabaseManager() {
        this.users = new HashMap<>();
        this.groups = new HashMap<>();
    }

    // User operations
    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Mentor> getAllMentors() {
        List<Mentor> mentors = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Mentor) {
                mentors.add((Mentor) user);
            }
        }
        return mentors;
    }

    public List<Mentee> getAllMentees() {
        List<Mentee> mentees = new ArrayList<>();
        for (User user : users.values()) {
            if (user instanceof Mentee) {
                mentees.add((Mentee) user);
            }
        }
        return mentees;
    }

    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    // Group operations
    public void saveGroup(Group group) {
        groups.put(group.getId(), group);
    }

    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }

    public List<Group> getAllGroups() {
        return new ArrayList<>(groups.values());
    }

    public List<Group> getGroupsByMentorId(String mentorId) {
        List<Group> mentorGroups = new ArrayList<>();
        for (Group group : groups.values()) {
            if (group.getMentorId().equals(mentorId)) {
                mentorGroups.add(group);
            }
        }
        return mentorGroups;
    }

    public Group getGroupByMenteeId(String menteeId) {
        for (Group group : groups.values()) {
            if (group.getMembersIds().contains(menteeId)) {
                return group;
            }
        }
        return null;
    }

    // Method to clear all data (for testing)
    public void clearAll() {
        users.clear();
        groups.clear();
    }

    // Method to load sample data for testing
    public void loadSampleData(UserManager userManager, GroupManager groupManager) throws Exception {
        // Create sample mentors
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> mentorData = new HashMap<>();
            mentorData.put("name", "Mentor " + i);
            mentorData.put("email", "mentor" + i + "@student.monash");
            mentorData.put("age", 25 + i);
            mentorData.put("location", "Melbourne");

            List<String> interests = new ArrayList<>();
            interests.add("Programming");
            interests.add(i % 2 == 0 ? "Sports" : "Music");
            mentorData.put("interests", interests);

            mentorData.put("background", i % 2 == 0 ? "Australia" : "International");

            userManager.registerUser(mentorData, "mentor");
        }

        // Create sample mentees
        for (int i = 1; i <= 15; i++) {
            Map<String, Object> menteeData = new HashMap<>();
            menteeData.put("name", "Mentee " + i);
            menteeData.put("email", "mentee" + i + "@student.monash");
            menteeData.put("age", 18 + i % 7);
            menteeData.put("location", i % 3 == 0 ? "Clayton" : "Caulfield");

            List<String> interests = new ArrayList<>();
            if (i % 3 == 0) {
                interests.add("Programming");
                interests.add("AI");
            } else if (i % 3 == 1) {
                interests.add("Sports");
                interests.add("Music");
            } else {
                interests.add("Art");
                interests.add("Travel");
            }
            menteeData.put("interests", interests);

            menteeData.put("background", i % 5 == 0 ? "Australia" : "International");

            userManager.registerUser(menteeData, "mentee");
        }
    }
}