package ch.uzh.ifi.hase.soprafs26.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs26.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, String> {
	User findByEmail(String email);

	User findByUserID(String userID);

	User findByUsername(String username);

	User findByToken(String token);
}
