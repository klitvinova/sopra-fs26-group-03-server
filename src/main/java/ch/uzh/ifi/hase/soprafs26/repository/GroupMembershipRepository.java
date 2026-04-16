package ch.uzh.ifi.hase.soprafs26.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ch.uzh.ifi.hase.soprafs26.entity.GroupMembership;
import java.util.List;
import java.util.Optional;

@Repository("groupMembershipRepository")
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
	Optional<GroupMembership> findByUserUserID(String userID);
	Optional<GroupMembership> findByUserUserIDAndGroupId(String userID, Long groupId);
	List<GroupMembership> findByGroupId(Long groupId);
	long countByGroupId(Long groupId);
}
