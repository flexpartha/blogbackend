package com.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptHashTool {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java com.blog.util.BCryptHashTool <password1> [password2] ...");
            System.exit(1);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for (String rawPassword : args) {
            String hash = encoder.encode(rawPassword);
            System.out.println(rawPassword + " -> " + hash);
        }
    }
}
