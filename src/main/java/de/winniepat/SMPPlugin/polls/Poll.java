package de.winniepat.SMPPlugin.polls;

import java.util.*;

public class Poll {
    private final String question;
    private final List<String> options;
    private final Map<UUID, String> votes = new HashMap<>();
    private final String creator;
    private final long endTimeMillis;

    public Poll(String question, List<String> options, String creator, long durationSeconds) {
        this.question = question;
        this.options = options;
        this.creator = creator;
        this.endTimeMillis = System.currentTimeMillis() + (durationSeconds * 1000);
    }

    public Poll(String question, List<String> options, String creator, long endTimeMillis, Map<String, Integer> savedVotes) {
        this.question = question;
        this.options = options;
        this.creator = creator;
        this.endTimeMillis = endTimeMillis;
        for (Map.Entry<String, Integer> entry : savedVotes.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                votes.put(UUID.randomUUID(), entry.getKey());
            }
        }
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCreator() {
        return creator;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public boolean vote(UUID player, String option) {
        if (!options.contains(option) || isExpired()) return false;
        votes.put(player, option);
        return true;
    }

    public Map<String, Integer> getResults() {
        Map<String, Integer> results = new HashMap<>();
        for (String option : options) {
            results.put(option, 0);
        }
        for (String vote : votes.values()) {
            results.put(vote, results.get(vote) + 1);
        }
        return results;
    }

    public boolean hasVoted(UUID player) {
        return votes.containsKey(player);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTimeMillis;
    }

    public long getRemainingTimeSeconds() {
        long remaining = endTimeMillis - System.currentTimeMillis();
        return Math.max(remaining / 1000, 0);
    }
} 