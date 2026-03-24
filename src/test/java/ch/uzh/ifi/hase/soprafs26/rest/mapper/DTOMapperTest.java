package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
	@Test
	public void testCreateUser_fromUserPostDTO_toUser_success() {
		UserPostDTO userPostDTO = new UserPostDTO();
		userPostDTO.setEmail("name@example.com");
		userPostDTO.setUsername("username");
		userPostDTO.setPassword("secret");

		User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

		assertEquals(userPostDTO.getEmail(), user.getEmail());
		assertEquals(userPostDTO.getUsername(), user.getUsername());
		assertEquals(userPostDTO.getPassword(), user.getPassword());
	}

	@Test
	public void testGetUser_fromUser_toUserGetDTO_success() {
		User user = new User();
		user.setEmail("firstname@lastname.com");
		user.setUsername("firstname@lastname");
		user.setStatus(UserStatus.OFFLINE);
		user.setToken("1");
		user.setPassword("secret");

		UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

		assertEquals(user.getId(), userGetDTO.getId());
		assertEquals(user.getEmail(), userGetDTO.getEmail());
		assertEquals(user.getUsername(), userGetDTO.getUsername());
		assertEquals(user.getToken(), userGetDTO.getToken());
		assertEquals(user.getStatus(), userGetDTO.getStatus());
	}
}
