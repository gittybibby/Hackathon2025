package dashboards;

import models.User;
import models.Mentee;
import models.Mentor;
import models.Group;
import managers.UserManager;
import managers.GroupManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserDashboard {
    private final UserManager userManager;
    private final GroupManager groupManager;

    public UserDashboard(UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    /**
     * Get the group a mentee belongs to
     */
    public Group getMenteeGroup(String menteeId) {
        return groupManager.getGroupByMenteeId(menteeId);
    }

    /**
     * Get the mentor information for a mentee
     */
    public Mentor getMenteeMentor(String menteeId) {
        Mentee mentee = (Mentee) userManager.getUserById(menteeId);
        if (mentee == null) {
            return null;
        }

        // Get mentee's mentor ID
        String mentorId = mentee.getMentorId();
        if (mentorId == null) {
            return null;
        }

        // Get mentor by ID
        User user = userManager.getUserById(mentorId);
        if (user instanceof Mentor) {
            return (Mentor) user;
        }

        return null;
    }

    /**
     * Get all mentees in the same group as the current mentee
     */
    public List<Mentee> getGroupMates(String menteeId) {
        Group group = getMenteeGroup(menteeId);
        if (group == null) {
            return new ArrayList<>();
        }

        List<Mentee> groupMates = new ArrayList<>();
        for (String memberId : group.getMembersIds()) {
            // Skip the current mentee
            if (memberId.equals(menteeId)) {
                continue;
            }

            // Get mentee by ID
            User user = userManager.getUserById(memberId);
            if (user instanceof Mentee) {
                groupMates.add((Mentee) user);
            }
        }

        return groupMates;
    }

    /**
     * Send a message to the group
     */
    public boolean sendMessageToGroup(String menteeId, String message) {
        Group group = getMenteeGroup(menteeId);
        if (group == null) {
            return false;
        }

        group.addMessage(menteeId, message);
        return true;
    }

    /**
     * Get all messages for the mentee's group
     */
    public List<Group.Message> getGroupMessages(String menteeId) {
        Group group = getMenteeGroup(menteeId);
        if (group == null) {
            return new ArrayList<>();
        }

        return group.getMessages();
    }

    /**
     * Update mentee profile
     */
    public boolean updateMenteeProfile(String menteeId, String name, String location, List<String> interests) {
        User user = userManager.getUserById(menteeId);
        if (!(user instanceof Mentee)) {
            return false;
        }

        if (name != null) {
            user.setName(name);
        }

        if (location != null) {
            user.setLocation(location);
        }

        if (interests != null) {
            user.setInterests(interests);
        }

        return true;
    }

    /**
     * Find mentees with similar interests
     */
    public List<Mentee> findSimilarMentees(String menteeId) {
        User currentUser = userManager.getUserById(menteeId);
        if (!(currentUser instanceof Mentee)) {
            return new ArrayList<>();
        }

        Mentee currentMentee = (Mentee) currentUser;
        List<String> currentInterests = currentMentee.getInterests();

        List<Mentee> similarMentees = new ArrayList<>();
        List<User> allUsers = userManager.getAllUsers();

        for (User user : allUsers) {
            // Skip if not a mentee or if it's the current user
            if (!(user instanceof Mentee) || user.getId().equals(menteeId)) {
                continue;
            }

            Mentee otherMentee = (Mentee) user;
            List<String> otherInterests = otherMentee.getInterests();

            // Count common interests
            int commonInterests = 0;
            for (String interest : currentInterests) {
                if (otherInterests.contains(interest)) {
                    commonInterests++;
                }
            }

            // Add to similar mentees if they have at least one common interest
            if (commonInterests > 0) {
                similarMentees.add(otherMentee);
            }
        }

        return similarMentees;
    }

    /**
     * Give feedback to mentor
     */
    public boolean giveFeedbackToMentor(String menteeId, int points) {
        Mentee mentee = (Mentee) userManager.getUserById(menteeId);
        if (mentee == null) {
            return false;
        }

        String mentorId = mentee.getMentorId();
        if (mentorId == null) {
            return false;
        }

        User user = userManager.getUserById(mentorId);
        if (!(user instanceof Mentor)) {
            return false;
        }

        Mentor mentor = (Mentor) user;
        mentee.giveFeedback(mentor, points);
        return true;
    }

    /**
     * Display a console-based dashboard for a mentee
     */
    public void displayConsoleDashboard(String menteeId, Scanner scanner) {
        Mentee mentee = (Mentee) userManager.getUserById(menteeId);
        if (mentee == null) {
            System.out.println("User not found");
            return;
        }

        System.out.println("\n===== MENTEE DASHBOARD =====");
        System.out.println("Name: " + mentee.getName());
        System.out.println("Email: " + mentee.getEmail());
        System.out.println("Location: " + mentee.getLocation());

        // Display interests
        System.out.println("Interests: " + String.join(", ", mentee.getInterests()));

        // Show group details
        Group group = getMenteeGroup(menteeId);
        if (group != null) {
            System.out.println("\nYour Group: " + group.getName() + " (ID: " + group.getId() + ")");

            // Show mentor details
            Mentor mentor = getMenteeMentor(menteeId);
            if (mentor != null) {
                System.out.println("Your Mentor: " + mentor.getName() + " (" + mentor.getEmail() + ")");
            }

            // Show group mates
            List<Mentee> groupMates = getGroupMates(menteeId);
            if (groupMates.isEmpty()) {
                System.out.println("You have no group mates yet");
            } else {
                System.out.println("Your Group Mates:");
                for (Mentee mate : groupMates) {
                    System.out.println("- " + mate.getName() + " (" + mate.getEmail() + ")");
                }
            }

            // Option to give feedback to mentor
            System.out.println("\nWould you like to give feedback to your mentor? (yes/no)");
            String feedbackInput = scanner.nextLine();
            if (feedbackInput.equalsIgnoreCase("yes")) {
                System.out.print("Rate your mentor (1-5): ");
                int rating = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (rating >= 1 && rating <= 5) {
                    giveFeedbackToMentor(menteeId, rating);
                    System.out.println("Feedback submitted. Thank you!");
                } else {
                    System.out.println("Invalid rating. Please use a scale of 1-5.");
                }
            }
        } else {
            System.out.println("You are not assigned to any group yet");
        }

        // Option to update interests
        System.out.println("\nDo you want to update your interests? (yes/no):");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("yes")) {
            System.out.println("Enter new interests (comma-separated):");
            String interestsInput = scanner.nextLine();
            String[] interestsArray = interestsInput.split(",");

            List<String> interests = new ArrayList<>();
            for (String interest : interestsArray) {
                interests.add(interest.trim());
            }

            updateMenteeProfile(menteeId, null, null, interests);
            System.out.println("Interests updated successfully");
        }
    }
}