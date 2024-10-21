package com.example.user;

import com.example.user.exception.UserNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserValidator userValidator;

    private final List<UserDTO> userDTOS = new ArrayList<>();

    public UserController(final UserValidator userValidator) {
        this.userValidator = userValidator;
        userDTOS.add(new UserDTO("John Doe", "user1", List.of("user")));
        userDTOS.add(new UserDTO("Jane Doe", "user2", List.of("user")));
        userDTOS.add(new UserDTO("Admin", "admin", List.of("admin")));
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        return userDTOS;
    }

    @GetMapping("/{username}")
    public UserDTO getUser(@PathVariable String username) {
        return userDTOS.stream()
                .filter(userDTO -> userDTO.username().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public void addUser(@RequestBody UserDTO userDTO) {
        userValidator.validateUser(userDTO);
        userDTOS.add(userDTO);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) {
        final UserDTO userDTOToDelete = userDTOS.stream()
                .filter(userDTO -> userDTO.username().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
        userDTOS.remove(userDTOToDelete);
    }

}
