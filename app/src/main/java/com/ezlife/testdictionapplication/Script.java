package com.ezlife.testdictionapplication;

import com.ezlife.testdictionapplication.Database.KEY;

/**
 * Created by Kwon on 2016-08-09.
 */
public class Script {
    private String English;
    private String Korean;

    private String username;
    private String category;
    private String program;
    private String expression;
    private int season;
    private int episode;
    private int score;
    private int lineNumber;
    private int occurence;


    public Script(String english, String korean) {
        this.English = english;
        this.Korean = korean;
    }

    public Script(String english, String korean, String express) {
        this.expression = express;
        this.English = english;
        this.Korean = korean;
    }

    public Script(String english, String korean, String express, int occur) {
        this.expression = express;
        this.English = english;
        this.Korean = korean;
        this.occurence = occur;
    }



    public Script() {
        init();
    }

    public int getOccurence() {
        return occurence;
    }

    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }

    private void init() {
        username = KEY.KEY_DEFAULT_USERNAME;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }

    public String getKorean() {
        return Korean;
    }

    public void setKorean(String korean) {
        Korean = korean;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getSeasonString() {
        int a = this.season;
        String value;
        if (a < 10) {
            value = "0" + a;
        } else {
            value = String.valueOf(a);
        }
        return value;
    }

    public void setSeasonString(String a) {
        this.season = Integer.valueOf(a);
    }

    public String getEpisodeString() {
        int a = this.episode;
        String value;
        if (a < 10) {
            value = "0" + a;
        } else {
            value = String.valueOf(a);
        }
        return value;
    }

    public void setEpisodeString(String a) {
        this.season = Integer.valueOf(a);
    }
}