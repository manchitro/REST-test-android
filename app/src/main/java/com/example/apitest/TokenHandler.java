package com.example.apitest;

public final class TokenHandler {
    private TokenHandler() {}

    private static String token = "";

    public static void setToken(String newToken) {
        if (newToken != null)
            token = newToken;
    }

    public static String getToken() {
        return token;
    }
}