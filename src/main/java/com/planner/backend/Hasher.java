package com.planner.backend;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Hasher {
    public String hash(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public boolean hashMatches(String input, String hash)
    {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString().equals(hash);
    }
}
