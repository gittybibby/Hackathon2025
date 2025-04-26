package managers;

import models.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * GroupManager provides functionality for creating, retrieving, and managing groups
 * within the LinkUp application. This class maintains an in-memory collection of all
 * groups and provides methods to perform operations on them.
 *
 */

 public class GroupManager {
    private final List<Group> groups;

    public GroupManager() {
        this.groups = new ArrayList<>();
    }

    public Group createGroup(String mentorId, String name) {
        Group group = new Group(mentorId, name);
        groups.add(group);
        return group;
    }

    public Group createGroup(String mentorId) {
        return createGroup(mentorId, "Skill Swap Group");
    }

    public Group getGroupById(String groupId) {
        for (Group group : groups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    public List<Group> getGroupsByMentorId(String mentorId) {
        List<Group> mentorGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group.getMentorId().equals(mentorId)) {
                mentorGroups.add(group);
            }
        }
        return mentorGroups;
    }

    public Group getGroupByMenteeId(String menteeId) {
        for (Group group : groups) {
            if (group.getMembersIds().contains(menteeId)) {
                return group;
            }
        }
        return null;
    }

    public List<Group> getAllGroups() {
        return new ArrayList<>(groups);
    }

    public boolean removeGroup(String groupId) {
        return groups.removeIf(group -> group.getId().equals(groupId));
    }
}