package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final String AUTH_COOKIE_NAME = "AUTH_TOKEN";
    private static final long AUTH_COOKIE_MAX_AGE_SECONDS = 7L * 24L * 60L * 60L;

    private final UserService userService;

    AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO registerUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User createdUser = userService.createUser(userInput);
        addAuthCookie(response, createdUser.getToken());
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
        User loggedInUser = userService.loginUser(userPostDTO.getUsername(), userPostDTO.getPassword());
        addAuthCookie(response, loggedInUser.getToken());
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(loggedInUser);
    }

    private void addAuthCookie(HttpServletResponse response, String token) {
        ResponseCookie authCookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(AUTH_COOKIE_MAX_AGE_SECONDS)
                .build();
        response.addHeader("Set-Cookie", authCookie.toString());
    }
}
