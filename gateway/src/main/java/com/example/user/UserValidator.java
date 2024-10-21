package com.example.user;

import com.example.user.exception.InvalidUserException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserValidator {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_LENGTH = 20;
    private static final int MIN_NAME_LENGTH = 8;

    public void validateUser(UserDTO userDTO) throws InvalidUserException {
        if (Objects.isNull(userDTO)) {
            throw new InvalidUserException("User cannot be null");
        }
        // username cannot be null
        if (Objects.isNull(userDTO.username())) {
            throw new InvalidUserException("Username cannot be null");
        }
        // id cannot be null
        if (Objects.isNull(userDTO.name())) {
            throw new InvalidUserException("Name cannot be null");
        }
        // username should be at least 4 characters and maximum 20 characters
        if (userDTO.username().length() < MIN_USERNAME_LENGTH || userDTO.username().length() > MAX_LENGTH) {
            throw new InvalidUserException("Username must be between 4 and 20 characters long");
        }
        // id should be at least 8 characters and maximum 20 characters
        if (userDTO.name().length() < MIN_NAME_LENGTH || userDTO.name().length() > MAX_LENGTH) {
            throw new InvalidUserException("Name must be between 8 and 20 characters long");
        }
    }

}
