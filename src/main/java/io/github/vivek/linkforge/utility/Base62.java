package io.github.vivek.linkforge.utility;

public final class Base62 {

    private Base62() {
    }

    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int BASE = ALPHABET.length;

    public static String encode(long value) {
        if (value == 0) return String.valueOf(ALPHABET[0]);

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET[(int) (value % BASE)]);
            value /= BASE;
        }
        return sb.reverse().toString();
    }
}

