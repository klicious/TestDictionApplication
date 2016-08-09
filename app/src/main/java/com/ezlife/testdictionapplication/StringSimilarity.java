package com.ezlife.testdictionapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StringSimilarity {

    private final char[] CHO =
		/*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
            {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
    private final char[] JUN =
		/*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
            {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
                    0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160,	0x3161,	0x3162,
                    0x3163};
    /*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
    private final char[] JON =
            {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
                    0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
                    0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    private double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "");
        s2 = s2.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "");

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    // For Korean!
    private double similarityKor(String s1, String s2) {

        s1 = splitKor(s1.toLowerCase().replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣0-9 ]", ""));
        s2 = splitKor(s2.toLowerCase().replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣0-9 ]", ""));

        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
        return (longerLength - editDistanceKor(longer, shorter)) / (double) longerLength;
    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    private int editDistanceKor(String s1, String s2) {

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public String getSimilarity(String s, String t, boolean isEng) {
        String result;
        if(!isEng) {
            result = String.format("%.3f is the similarity between \"%s\" and \"%s\"", similarityKor(s, t), s, t);
        } else {
            result = String.format("%.3f is the similarity between \"%s\" and \"%s\"", similarity(s, t), s, t);
        }
        return result;
    }

    public double getSimilarityDouble(String s, String t, boolean isEng) {
        double result;
        if(!isEng) {
            result = similarityKor(s, t);
        } else {
            result = similarity(s, t);
        }
        return result;
    }


    private String splitKor(String inputKor) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0 ; i < inputKor.length();i++)
        {
            char test = inputKor.charAt(i);

            if(test >= 0xAC00)
            {
                char uniVal = (char) (test - 0xAC00);

                char cho = (char) (((uniVal - (uniVal % 28))/28)/21);
                char jun = (char) (((uniVal - (uniVal % 28))/28)%21);
                char jon = (char) (uniVal %28);

                sb.append(CHO[cho]);
                sb.append(JUN[jun]);
                if(jon != 0x0000)
                    sb.append(JON[jon]);

            } else {
                sb.append(test);
            }
        }

        return sb.toString();
    }

}
