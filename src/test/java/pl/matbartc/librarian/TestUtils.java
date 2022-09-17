package pl.matbartc.librarian;

import java.util.UUID;

public abstract class TestUtils {

    public static String generateRandomUrl() {
        return "http://xyz.pl/" + UUID.randomUUID();
    }
}
