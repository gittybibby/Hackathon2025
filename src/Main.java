import managers.UserManager;
import managers.GroupManager;
import managers.DatabaseManager;
import models.User;
import models.Mentor;
import models.Mentee;
import models.Group;
import services.MatchingAlgorithm;
import dashboards.UserDashboard;
import dashboards.MentorDashboard;
import questionnaire.UserQuestionnaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize managers
            UserManager userManager = new UserManager();
            GroupManager groupManager = new GroupManager();
            DatabaseManager dbManager = new DatabaseManager();

            // Load sample data for testing
            dbManager.loadSampleData(userManager, groupManager);

            // Create matching algorithm
            MatchingAlgorithm matchingAlgorithm = new MatchingAlgorithm(userManager, groupManager);

            // Create dashboards
            UserDashboard userDashboard = new UserDashboard(userManager, groupManager);
            MentorDashboard mentorDashboard = new MentorDashboard(userManager, groupManager);

            // Main menu
            Scanner scanner = new Scanner(System.in);
            User currentUser = null;

            while (true) {
                System.out.println("\n===== SKILL SWAP PLATFORM =====");
                if (currentUser == null) {
                    // User not logged in
                    System.out.println("1. Take the questionnaire to register");
                    System.out.println("2. Manual registration");
                    System.out.println("3. Login with existing account");
                    System.out.println("4. Run matching algorithm");
                    System.out.println("5. Exit");
                    System.out.print("Select an option (1-5): ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1:
                            // Use questionnaire for registration
                            Map<String, Object> userData = UserQuestionnaire.collectUserData();
                            String role = userData.containsKey("role") ?
                                    userData.get("role").toString().toLowerCase() : "mentee";

                            // Determine role based on questionnaire input
                            if (role.contains("mentor")) {
                                currentUser = userManager.registerUser(userData, "mentor");
                            } else {
                                currentUser = userManager.registerUser(userData, "mentee");
                            }

                            System.out.println("User registered: " + currentUser.getName());
                            break;

                        case 2:
                            // Manual registration
                            Map<String, Object> manualUserData = collectManualUserData(scanner);
                            System.out.print("Register as mentor or mentee? ");
                            String userRole = scanner.nextLine().toLowerCase();

                            currentUser = userManager.registerUser(manualUserData, userRole);
                            System.out.println("User registered: " + currentUser.getName());
                            break;

                        case 3:
                            // Login with existing account
                            System.out.print("Enter email: ");
                            String email = scanner.nextLine();

                            System.out.print("Enter PIN (use any value for testing): ");
                            String pin = scanner.nextLine();

                            currentUser = userManager.login(email, pin);
                            if (currentUser != null) {
                                System.out.println("Login successful for: " + currentUser.getName());
                            } else {
                                System.out.println("Login failed");
                            }
                            break;

                        case 4:
                            // Run matching algorithm
                            System.out.println("Running matching algorithm...");
                            List<Group> formedGroups = matchingAlgorithm.formGroups();
                            System.out.println("Created " + formedGroups.size() + " groups");
                            displayAllGroups(groupManager);
                            break;

                        case 5:
                            System.out.println("Goodbye!");
                            return;

                        default:
                            System.out.println("Invalid option selected.");
                            break;
                    }
                } else {
                    // User is logged in - show dashboard
                    if (currentUser instanceof Mentor) {
                        // Using dashboard display approach
                        mentorDashboard.displayConsoleDashboard(currentUser.getId(), scanner);
                    } else if (currentUser instanceof Mentee) {
                        // Using dashboard display approach
                        userDashboard.displayConsoleDashboard(currentUser.getId(), scanner);
                    }

                    // Add option to logout
                    System.out.println("\n1. Logout");
                    System.out.println("2. Exit");
                    System.out.print("Select an option: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (choice == 1) {
                        currentUser = null;
                        System.out.println("Logged out successfully");
                    } else if (choice == 2) {
                        System.out.println("Goodbye!");
                        return;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method for manual data entry
    private static Map<String, Object> collectManualUserData(Scanner scanner) {
        java.util.Map<String, Object> userData = new java.util.HashMap<>();

        System.out.print("Enter name: ");
        userData.put("name", scanner.nextLine());

        System.out.print("Enter email (must end with @student.monash): ");
        userData.put("email", scanner.nextLine());

        System.out.print("Enter age: ");
        userData.put("age", scanner.nextInt());
        scanner.nextLine(); // Consume newline

        System.out.print("Enter location: ");
        userData.put("location", scanner.nextLine());

        List<String> interests = new ArrayList<>();
        System.out.print("Enter first interest: ");
        interests.add(scanner.nextLine());
        System.out.print("Enter second interest: ");
        interests.add(scanner.nextLine());
        userData.put("interests", interests);

        System.out.print("Enter background: ");
        userData.put("background", scanner.nextLine());

        return userData;
    }

    // For displaying all groups
    private static void displayAllGroups(GroupManager groupManager) {
        List<Group> allGroups = groupManager.getAllGroups();
        System.out.println("\n===== ALL GROUPS =====");

        if (allGroups.isEmpty()) {
            System.out.println("No groups have been formed yet");
        } else {
            for (Group group : allGroups) {
                System.out.println("\nGroup: " + group.getName() + " (ID: " + group.getId() + ")");
                System.out.println("Mentor ID: " + group.getMentorId());

                List<String> memberIds = group.getMembersIds();
                if (memberIds.isEmpty()) {
                    System.out.println("No mentees in this group");
                } else {
                    System.out.println("Mentee IDs:");
                    for (String menteeId : memberIds) {
                        System.out.println("- " + menteeId);
                    }
                }
            }
        }
    }
}