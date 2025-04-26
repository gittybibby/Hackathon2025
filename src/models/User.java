package models;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private final String id;
    private String name;
    private String email;
    private int age;
    private String location;
    private List<String> interests;
    private String background;
    private String groupId;

    public User(String name, String email, int age, String location, List<String> interests, String background) {
        this.id = generateUniqueId();
        this.name = name;
        this.email = email;
        this.age = age;
        this.location = location;
        this.interests = interests;
        this.background = background;
        this.groupId = null;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    // Helper method to generate unique IDs
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public void updateProfile(Map<String, Object> profileData) {
        // Update profile information
        if (profileData.containsKey("name")) this.name = (String) profileData.get("name");
        if (profileData.containsKey("location")) this.location = (String) profileData.get("location");
        // Update other fields as needed
    }
}