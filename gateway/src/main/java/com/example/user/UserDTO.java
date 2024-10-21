package com.example.user;

import java.util.List;

public record UserDTO(String name, String username, List<String> roles) {
}
