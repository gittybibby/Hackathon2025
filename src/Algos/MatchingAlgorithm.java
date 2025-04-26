package services;

import models.User;
import models.Mentor;
import models.Mentee;
import models.Group;
import managers.UserManager;
import managers.GroupManager;

import java.util.*;
import java.util.stream.Collectors;

public class MatchingAlgorithm {
    private final UserManager userManager;
    private final GroupManager groupManager;

    public MatchingAlgorithm(UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    // Calculate match score between mentee and mentor based on similarities
    public int calculateMatchScore(Mentee mentee, Mentor mentor) {
        int score = 0;

        // Check for overlapping interests
        List<String> menteeInterests = mentee.getInterests();
        List<String> mentorInterests = mentor.getInterests();

        // Count common interests
        int commonInterestsCount = 0;
        for (String interest : menteeInterests) {
            if (mentorInterests.contains(interest)) {
                commonInterestsCount++;
            }
        }

        // Add 2 points per common interest
        score += commonInterestsCount * 2;

        // Check for shared background (country of origin)
        if (mentee.getBackground().equals(mentor.getBackground())) {
            score += 3; // Add 3 points for same background
        }

        // Check for same location
        if (mentee.getLocation().equals(mentor.getLocation())) {
            score += 2; // Add 2 points for same location
        }

        return score;
    }

    // Form groups based on matching scores
    public List<Group> formGroups() {
        List<Group> formedGroups = new ArrayList<>();

        // Get all users
        List<User> allUsers = userManager.getAllUsers();

        // Separate mentors and mentees
        List<Mentor> mentors = new ArrayList<>();
        List<Mentee> mentees = new ArrayList<>();

        for (User user : allUsers) {
            if (user instanceof Mentor) {
                mentors.add((Mentor) user);
            } else if (user instanceof Mentee) {
                mentees.add((Mentee) user);
            }
        }

        // Keep track of assigned mentees
        Set<String> assignedMenteeIds = new HashSet<>();

        // For each mentor, find best matching mentees
        for (Mentor mentor : mentors) {
            // Create a new group for this mentor
            Group group = groupManager.createGroup(mentor.getId());

            // Calculate match scores for all unassigned mentees
            List<MenteeMatch> matches = new ArrayList<>();

            for (Mentee mentee : mentees) {
                // Skip already assigned mentees
                if (assignedMenteeIds.contains(mentee.getId())) {
                    continue;
                }

                int score = calculateMatchScore(mentee, mentor);
                matches.add(new MenteeMatch(mentee, score));
            }

            // Sort by match score (highest to lowest)
            matches.sort(Comparator.comparing(MenteeMatch::getScore).reversed());

            // Assign top 5 mentees to this mentor's group
            int count = 0;
            for (MenteeMatch match : matches) {
                if (count >= 5) {
                    break; // Maximum 5 mentees per group
                }

                Mentee mentee = match.getMentee();
                group.addMember(mentee.getId());
                mentee.setMentorId(mentor.getId());
                mentee.setGroupId(group.getId());
                assignedMenteeIds.add(mentee.getId());
                count++;
            }

            // Add mentor to their group
            mentor.addMenteeGroup(group.getId());

            // Add the group to our list of formed groups
            formedGroups.add(group);
        }

        return formedGroups;
    }

    // Helper class to store mentee-score pairs
    private static class MenteeMatch {
        private final Mentee mentee;
        private final int score;

        public MenteeMatch(Mentee mentee, int score) {
            this.mentee = mentee;
            this.score = score;
        }

        public Mentee getMentee() {
            return mentee;
        }

        public int getScore() {
            return score;
        }
    }
}