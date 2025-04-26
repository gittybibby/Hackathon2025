package questionnaire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class UserQuestionnaire {
    // Change from main method to a utility method that returns data
    public static Map<String, Object> collectUserData() {
        Scanner scanner = new Scanner(System.in);
        UserProfile user = new UserProfile();

        System.out.println("📝 Welcome to the LinkUp Questionnaire 📝");

        // Collect basic info
        System.out.print("Enter your name: ");
        user.name = scanner.nextLine();

        System.out.print("Enter your age: ");
        user.age = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline

        System.out.print("Enter your location: ");
        user.location = scanner.nextLine();

        // Hobbies
        System.out.print("What are your hobbies/interests? (comma-separated): ");
        user.hobbies = scanner.nextLine();
        // Convert comma-separated hobbies to a list
        List<String> interestsList = new ArrayList<>(Arrays.asList(user.hobbies.split("\\s*,\\s*")));

        // Background info
        System.out.print("What is your country of origin or preferred language? ");
        user.background = scanner.nextLine();

        // Role: Mentor or Mentee
        System.out.print("Would you like to be a Mentor or Mentee? ");
        user.role = scanner.nextLine();

        // Output summary
        System.out.println("\n✅ Thanks for completing the questionnaire!");
        System.out.println("📄 Your Profile Summary:");
        System.out.println(user);

        // Convert to a format compatible with your UserManager
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.name);
        userData.put("email", user.name.toLowerCase().replace(' ', '.') + "@student.monash"); // Generate email
        userData.put("age", user.age);
        userData.put("location", user.location);
        userData.put("interests", interestsList);
        userData.put("background", user.background);

        // Don't close the scanner here if it's used elsewhere
        // scanner.close();

        return userData;
    }

    // You can keep the original main method for testing
    public static void main(String[] args) {
        Map<String, Object> userData = collectUserData();
        System.out.println("Data ready for registration: " + userData);
    }
}

class UserProfile {
    String name;
    int age;
    String location;
    String hobbies;
    String background;
    String role;

    @Override
    public String toString() {
        return String.format(
                "👤 Name: %s\n🎂 Age: %d\n📍 Location: %s\n🎯 Interests: %s\n🌏 Background: %s\n💡 Role: %s",
                name, age, location, hobbies, background, role
        );
    }
}