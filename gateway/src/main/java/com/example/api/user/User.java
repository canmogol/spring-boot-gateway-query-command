package com.example.api.user;

import java.util.List;

public record User(String name, String username, List<String> roles) {
}
