package com.leetcode.aihelper.model;

public class HintRequest {
    private String problemTitle;

    public HintRequest() {}

    public HintRequest(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }
}
