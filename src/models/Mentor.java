package models;

import java.util.ArrayList;
import java.util.List;

public class Mentor extends User {
    private final List<String> menteeGroups;
    private int rewardPoints;

    public Mentor(String name, String email, int age, String location, List<String> interests, String background) {
        super(name, email, age, location, interests, background);
        this.menteeGroups = new ArrayList<>();
        this.rewardPoints = 0;
    }

    public List<String> getMenteeGroups() {
        return menteeGroups;
    }

    public void addMenteeGroup(String groupId) {
        this.menteeGroups.add(groupId);
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void receiveFeedback(int points) {
        this.rewardPoints += points;
    }
}