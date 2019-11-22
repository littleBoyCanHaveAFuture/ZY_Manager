package com.ssm.promotion.core.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {
    public RandomUtil() {
    }

    public static int rndInt() {
        return rndInt(2147483647);
    }

    public static int rndInt(int max) {
        Random RND = ThreadLocalRandom.current();
        return RND.nextInt(max);
    }

    public static int rndInt(int min, int max) {
        Random RND = ThreadLocalRandom.current();
        return min + RND.nextInt(max - min + 1);
    }

    public static int rnd100() {
        return rndInt(100);
    }

    public static int rnd10000() {
        return rndInt(10000);
    }

    public static long rndLong() {
        Random RND = ThreadLocalRandom.current();
        return RND.nextLong();
    }

    public static long rndLong(long max) {
        Random RND = ThreadLocalRandom.current();
        return max < 2147483647L ? (long) rndInt((int) max) : Math.abs(RND.nextLong());
    }

    public static long rndLong(long min, long max) {
        return min + rndLong(max - min + 1L);
    }

    public static boolean rndBool() {
        Random RND = ThreadLocalRandom.current();
        return RND.nextBoolean();
    }

    public static boolean rndBool100(int less100) {
        if (less100 >= 100) {
            return true;
        } else {
            return rnd100() < less100;
        }
    }

    public static boolean rndBool10000(int less10000) {
        if (less10000 >= 10000) {
            return true;
        } else {
            return rnd10000() < less10000;
        }
    }

    public static char rndWord() {
        int val = 97 + rndInt(26);
        char word = (char) val;
        return word;
    }

    public static String rndStr(int len, boolean hasNumber) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; ++i) {
            if (sb.length() > 0 && hasNumber && rndBool100(38)) {
                sb.append(rndInt(10));
            } else if (rndBool()) {
                sb.append(Character.toUpperCase(rndWord()));
            } else {
                sb.append(Character.toLowerCase(rndWord()));
            }
        }

        return sb.toString();
    }

    public static <T> T rndElement(List<T> list) {
        return list.get(rndInt(list.size()));
    }
}
