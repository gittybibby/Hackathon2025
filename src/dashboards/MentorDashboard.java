package dashboards;

import models.Mentor;
import models.Mentee;
import models.User;
import models.Group;
import managers.UserManager;
import managers.GroupManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MentorDashboard {
    private final UserManager userManager;
    private final GroupManager groupManager;

    public MentorDashboard(UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    /**
     * Get all groups associated with this mentor
     */
    public List<Group> getMentorGroups(String mentorId) {
        return groupManager.getGroupsByMentorId(mentorId);
    }

    /**
     * Get all mentees in a specific group
     */
    public List<Mentee> getGroupMentees(String groupId) {
        Group group = groupManager.getGroupById(groupId);
        if (group == null) {
            return new ArrayList<>();
        }

        List<Mentee> mentees = new ArrayList<>();
        for (String menteeId : group.getMembersIds()) {
            // Get mentee by ID
            User user = userManager.getUserById(menteeId);
            if (user instanceof Mentee) {
                mentees.add((Mentee) user);
            }
        }

        return mentees;
    }

    /**
     * Get all mentees assigned to a mentor across all groups
     */
    public List<Mentee> getAllMentees(String mentorId) {
        List<Mentee> allMentees = new ArrayList<>();
        List<Group> groups = getMentorGroups(mentorId);

        for (Group group : groups) {
            allMentees.addAll(getGroupMentees(group.getId()));
        }

        return allMentees;
    }

    /**
     * Get mentee contact information
     */
    public MenteeContactInfo getMenteeContactInfo(String menteeId) {
        User user = userManager.getUserById(menteeId);
        if (!(user instanceof Mentee)) {
            return null;
        }

        Mentee mentee = (Mentee) user;
        return new MenteeContactInfo(
                mentee.getName(),
                mentee.getEmail(),
                // You might want to add phone number to the Mentee class
                "", // phone number would go here
                mentee.getLocation(),
                mentee.getInterests(),
                mentee.getBackground()
        );
    }

    /**
     * Send a message to a specific group
     */
    public boolean sendMessageToGroup(String groupId, String mentorId, String message) {
        Group group = groupManager.getGroupById(groupId);
        if (group == null || !group.getMentorId().equals(mentorId)) {
            return false;
        }

        group.addMessage(mentorId, message);
        return true;
    }

    /**
     * Get all messages for a specific group
     */
    public List<Group.Message> getGroupMessages(String groupId) {
        Group group = groupManager.getGroupById(groupId);
        if (group == null) {
            return new ArrayList<>();
        }

        return group.getMessages();
    }

    /**
     * Display a console-based dashboard for a mentor
     */
    public void displayConsoleDashboard(String mentorId, Scanner scanner) {
        Mentor mentor = (Mentor) userManager.getUserById(mentorId);
        if (mentor == null) {
            System.out.println("User not found");
            return;
        }

        System.out.println("\n===== MENTOR DASHBOARD =====");
        System.out.println("Name: " + mentor.getName());
        System.out.println("Email: " + mentor.getEmail());
        System.out.println("Location: " + mentor.getLocation());
        System.out.println("Reward Points: " + mentor.getRewardPoints());

        // List all groups
        List<Group> mentorGroups = getMentorGroups(mentorId);
        System.out.println("\nYour Groups:");

        if (mentorGroups.isEmpty()) {
            System.out.println("You don't have any groups yet");
        } else {
            for (Group group : mentorGroups) {
                System.out.println("\nGroup: " + group.getName() + " (ID: " + group.getId() + ")");

                List<Mentee> groupMentees = getGroupMentees(group.getId());
                if (groupMentees.isEmpty()) {
                    System.out.println("No mentees in this group");
                } else {
                    System.out.println("Mentees:");
                    for (Mentee mentee : groupMentees) {
                        System.out.println("- " + mentee.getName() + " (" + mentee.getEmail() + ")");
                    }

                    // Display mentee count (from your group member's code)
                    int menteeCount = groupMentees.size();
                    System.out.println("Current Mentees: " + menteeCount);
                }
            }
        }

        // Option to update profile
        System.out.println("\nDo you want to update your profile? (yes/no):");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("yes")) {
            System.out.print("Enter new location: ");
            String location = scanner.nextLine();

            Map<String, Object> profileData = new java.util.HashMap<>();
            profileData.put("location", location);

            mentor.updateProfile(profileData);
            System.out.println("Profile updated successfully");
        }

        // Option to send message to a group
        if (!mentorGroups.isEmpty()) {
            System.out.println("\nWould you like to send a message to one of your groups? (yes/no)");
            String messageInput = scanner.nextLine();
            if (messageInput.equalsIgnoreCase("yes")) {
                System.out.println("Select a group to message:");
                for (int i = 0; i < mentorGroups.size(); i++) {
                    System.out.println((i + 1) + ". " + mentorGroups.get(i).getName());
                }

                int groupChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (groupChoice > 0 && groupChoice <= mentorGroups.size()) {
                    Group selectedGroup = mentorGroups.get(groupChoice - 1);

                    System.out.println("Enter your message:");
                    String message = scanner.nextLine();

                    sendMessageToGroup(selectedGroup.getId(), mentorId, message);
                    System.out.println("Message sent to " + selectedGroup.getName());
                } else {
                    System.out.println("Invalid group selection");
                }
            }
        }
    }

    /**
     * Helper class to represent mentee contact information
     */
    public static class MenteeContactInfo {
        private final String name;
        private final String email;
        private final String phoneNumber;
        private final String location;
        private List<String> interests;
        private final String background;

        public MenteeContactInfo(String name, String email, String phoneNumber,
                                 String location, List<String> interests, String background) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.location = location;
            this.interests = interests;
            this.background = background;
        }

        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getLocation() { return location; }
        public List<String> getInterests() { return interests; }
        public String getBackground() { return background; }
    }
}