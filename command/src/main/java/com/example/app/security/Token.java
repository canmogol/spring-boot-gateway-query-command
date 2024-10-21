
package com.example.app.security;

import java.util.List;

public record Token(String subject, List<String> roles) {
    public static final String TOKEN_SUBJECT = "TOKEN_SUBJECT";
    public static final String TOKEN_ROLES = "TOKEN_ROLES";
}
