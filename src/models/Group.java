package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class Group {
    private final String id;
    private String name;
    private final String mentorId;
    private final List<String> membersIds; // IDs of mentees
    private final List<Message> messages;  // For chat functionality

    // Constructor
    public Group(String mentorId, String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.mentorId = mentorId;
        this.membersIds = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    // Overloaded constructor with default name
    public Group(String mentorId) {
        this(mentorId, "Skill Swap Group");
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMentorId() {
        return mentorId;
    }

    public List<String> getMembersIds() {
        return membersIds;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // Method to add a mentee to the group
    public boolean addMember(String menteeId) {
        if (membersIds.size() < 5) { // Limit of 5 mentees per group
            membersIds.add(menteeId);
            return true;
        }
        return false; // Group is full
    }

    // Method to remove a mentee from the group
    public boolean removeMember(String menteeId) {
        return membersIds.remove(menteeId);
    }

    // Method to add a message to the group chat
    public void addMessage(String userId, String content) {
        Message message = new Message(userId, content);
        messages.add(message);
    }

    // Inner class for messages
    public static class Message {
        private final String userId;
        private final String content;
        private final Date timestamp;

        public Message(String userId, String content) {
            this.userId = userId;
            this.content = content;
            this.timestamp = new Date();
        }

        public String getUserId() {
            return userId;
        }

        public String getContent() {
            return content;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }
}