package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void register_withoutAuthorization_isAllowed() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("public-register-user@example.com");
        userPostDTO.setUsername("public-register-user");
        userPostDTO.setPassword("secret");

        MockHttpServletRequestBuilder request = post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userPostDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("AUTH_TOKEN=")))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void users_withoutAuthorization_isUnauthorized() throws Exception {
        MockHttpServletRequestBuilder request = get("/users").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void users_withBearerHeader_isUnauthorized() throws Exception {
        User persistedUser = new User();
        persistedUser.setEmail("authorized-user@example.com");
        persistedUser.setUsername("authorized-user");
        persistedUser.setPassword("hashed-password");
        persistedUser.setToken("valid-token");
        persistedUser.setStatus(UserStatus.ONLINE);
        userRepository.saveAndFlush(persistedUser);

        MockHttpServletRequestBuilder request = get("/users")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void users_withValidAuthCookie_isAllowed() throws Exception {
        User persistedUser = new User();
        persistedUser.setEmail("authorized-user@example.com");
        persistedUser.setUsername("authorized-user");
        persistedUser.setPassword("hashed-password");
        persistedUser.setToken("valid-token");
        persistedUser.setStatus(UserStatus.ONLINE);
        userRepository.saveAndFlush(persistedUser);

        MockHttpServletRequestBuilder request = get("/users")
                .cookie(new MockCookie("AUTH_TOKEN", "valid-token"))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void logout_withValidAuthCookie_clearsCookieAndInvalidatesToken() throws Exception {
        User persistedUser = new User();
        persistedUser.setEmail("logout-user@example.com");
        persistedUser.setUsername("logout-user");
        persistedUser.setPassword("hashed-password");
        persistedUser.setToken("logout-token");
        persistedUser.setStatus(UserStatus.ONLINE);
        userRepository.saveAndFlush(persistedUser);

        MockHttpServletRequestBuilder logoutRequest = post("/logout")
                .cookie(new MockCookie("AUTH_TOKEN", "logout-token"))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(logoutRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("AUTH_TOKEN=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));

        MockHttpServletRequestBuilder usersRequest = get("/users")
                .cookie(new MockCookie("AUTH_TOKEN", "logout-token"))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(usersRequest).andExpect(status().isUnauthorized());
    }
}
