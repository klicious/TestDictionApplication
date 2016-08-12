package com.ezlife.testdictionapplication;

import com.ezlife.testdictionapplication.Database.KEY;

/**
 * Created by Kwon on 2016-08-09.
 */
public class Script {
    private String English;
    private String Korean;

    public String username;
    public String category;
    public String program;
    public int season;
    public int episode;
    public int score;
    public int lineNumber;

    public Script(String english, String korean) {
        English = english;
        Korean = korean;
    }

    public Script() {
        init();
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