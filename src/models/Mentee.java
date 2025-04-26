package models;

import java.util.List;

public class Mentee extends User {
    private String mentorId;

    public Mentee(String name, String email, int age, String location, List<String> interests, String background) {
        super(name, email, age, location, interests, background);
        this.mentorId = null;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public void giveFeedback(Mentor mentor, int points) {
        mentor.receiveFeedback(points);
    }
}