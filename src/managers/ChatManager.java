package managers;

import models.Group;
import models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatManager - In-memory implementation of a chat management system
 *
 * NOTE: This is a simplified in-memory implementation for demonstration purposes only.
 * In a production environment, this would be integrated with:
 *  1. A persistent database storage system
 *  2. Real-time communication using WebSockets, Socket.IO, or similar technology
 *  3. Proper message queue handling for scalability
 *
 * For actual implementation, consider:
 * - Using Socket.IO or Java WebSockets for real-time message delivery
 * - Implementing proper error handling and retry mechanisms
 * - Adding message persistence to a database
 * - Supporting message status (delivered, read, etc.)
 */

public class ChatManager {
    private final UserManager userManager;
    private final GroupManager groupManager;

    // Store unread message counts for each user
    private final Map<String, Map<String, Integer>> unreadMessages; // userId -> groupId -> count

    public ChatManager(UserManager userManager, GroupManager groupManager) {
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.unreadMessages = new HashMap<>();
    }

    /**
     * Send a message to a group
     */
    public boolean sendGroupMessage(String senderId, String groupId, String content) {
        // Check if sender exists
        User sender = userManager.getUserById(senderId);
        if (sender == null) {
            return false;
        }

        // Check if group exists
        Group group = groupManager.getGroupById(groupId);
        if (group == null) {
            return false;
        }

        // Check if sender is a member or the mentor of the group
        boolean isMember = group.getMembersIds().contains(senderId);
        boolean isMentor = group.getMentorId().equals(senderId);

        if (!isMember && !isMentor) {
            return false;
        }

        // Add message to group
        group.addMessage(senderId, content);

        // Update unread message count for all members except sender
        updateUnreadMessageCounts(groupId, senderId);

        return true;
    }

    /**
     * Mark messages as read for a user in a group
     */
    public void markMessagesAsRead(String userId, String groupId) {
        if (unreadMessages.containsKey(userId)) {
            unreadMessages.get(userId).put(groupId, 0);
        }
    }

    /**
     * Get the number of unread messages for a user in a group
     */
    public int getUnreadMessageCount(String userId, String groupId) {
        if (unreadMessages.containsKey(userId) && unreadMessages.get(userId).containsKey(groupId)) {
            return unreadMessages.get(userId).get(groupId);
        }
        return 0;
    }

    /**
     * Get all messages for a group
     */
    public List<Group.Message> getGroupMessages(String groupId) {
        Group group = groupManager.getGroupById(groupId);
        if (group == null) {
            return new ArrayList<>();
        }

        return group.getMessages();
    }

    /**
     * Helper method to update unread message counts when a new message is sent
     */
    private void updateUnreadMessageCounts(String groupId, String senderId) {
        Group group = groupManager.getGroupById(groupId);
        if (group == null) {
            return;
        }

        // Update count for all members
        for (String memberId : group.getMembersIds()) {
            if (!memberId.equals(senderId)) {
                incrementUnreadCount(memberId, groupId);
            }
        }

        // Update count for mentor if not the sender
        if (!group.getMentorId().equals(senderId)) {
            incrementUnreadCount(group.getMentorId(), groupId);
        }
    }

    /**
     * Helper method to increment unread count
     */
    private void incrementUnreadCount(String userId, String groupId) {
        // Ensure maps exist
        if (!unreadMessages.containsKey(userId)) {
            unreadMessages.put(userId, new HashMap<>());
        }

        Map<String, Integer> userCounts = unreadMessages.get(userId);
        if (!userCounts.containsKey(groupId)) {
            userCounts.put(groupId, 0);
        }

        // Increment count
        int currentCount = userCounts.get(groupId);
        userCounts.put(groupId, currentCount + 1);
    }
}