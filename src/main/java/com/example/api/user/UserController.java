package com.example.api.user;

import com.example.api.exception.InvalidUserException;
import com.example.api.exception.UserNotFoundException;
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

    private final List<User> users = new ArrayList<>();

    public UserController(final UserValidator userValidator) {
        this.userValidator = userValidator;
        users.add(new User("John Doe", "user1", List.of("user")));
        users.add(new User("Jane Doe", "user2", List.of("user")));
        users.add(new User("Admin", "admin", List.of("admin")));
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) throws UserNotFoundException {
        return users.stream()
                .filter(user -> user.username().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public void addUser(@RequestBody User user) throws InvalidUserException {
        userValidator.validateUser(user);
        users.add(user);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) throws UserNotFoundException {
        final User userToDelete = users.stream()
                .filter(user -> user.username().equals(username))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
        users.remove(userToDelete);
    }

}
