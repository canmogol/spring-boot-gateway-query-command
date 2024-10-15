package com.example.api.user;

import com.example.api.exception.InvalidUserException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserValidator {

    public void validateUser(User user) throws InvalidUserException {
        if (Objects.isNull(user)) {
            throw new InvalidUserException("User cannot be null");
        }
        // username cannot be null
        if (Objects.isNull(user.username())) {
            throw new InvalidUserException("Username cannot be null");
        }
        // name cannot be null
        if (Objects.isNull(user.name())) {
            throw new InvalidUserException("Name cannot be null");
        }
        // username should be at least 4 characters and maximum 20 characters
        if (user.username().length() < 4 || user.username().length() > 20) {
            throw new InvalidUserException("Username must be between 4 and 20 characters long");
        }
        // name should be at least 8 characters and maximum 20 characters
        if (user.name().length() < 8 || user.name().length() > 20) {
            throw new InvalidUserException("Name must be between 8 and 20 characters long");
        }
    }

}
