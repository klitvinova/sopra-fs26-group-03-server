package ch.uzh.ifi.hase.soprafs26.rest.dto;

import ch.uzh.ifi.hase.soprafs26.constant.GroupRole;
import java.time.Instant;

public class GroupMemberGetDTO {
	private String userID;
	private String username;
	private GroupRole role;
	private Instant joinedAt;

	public String getUserID() { return userID; }
	public void setUserID(String userID) { this.userID = userID; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public GroupRole getRole() { return role; }
	public void setRole(GroupRole role) { this.role = role; }
	public Instant getJoinedAt() { return joinedAt; }
	public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}
