package de.winniepat.SMPPlugin.polls;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PollManager {
    private Poll currentPoll = null;
    private final File dataFile;
    private final YamlConfiguration data;
    private final List<Poll> expiredPolls = new ArrayList<>();

    public PollManager(File dataFile) {
        this.dataFile = dataFile;
        this.data = YamlConfiguration.loadConfiguration(dataFile);
        load();
    }

    public void startPoll(String question, List<String> options, String creator, long durationSeconds) {
        if (currentPoll != null && currentPoll.isExpired()) {
            expiredPolls.add(currentPoll);
        }
        this.currentPoll = new Poll(question, options, creator, durationSeconds);
        save();
    }

    public Poll getCurrentPoll() {
        return currentPoll;
    }

    public void clearPoll() {
        if (currentPoll != null) {
            expiredPolls.add(currentPoll);
        }
        this.currentPoll = null;
        data.set("poll", null);
        save();
    }

    public boolean hasActivePoll() {
        return currentPoll != null && !currentPoll.isExpired();
    }

    public List<Poll> getExpiredPolls() {
        return expiredPolls;
    }

    public void save() {
        if (currentPoll == null) return;
        data.set("poll.question", currentPoll.getQuestion());
        data.set("poll.options", currentPoll.getOptions());
        data.set("poll.creator", currentPoll.getCreator());
        data.set("poll.endTime", currentPoll.getEndTimeMillis());
        data.set("poll.results", currentPoll.getResults());
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!data.contains("poll")) return;
        String question = data.getString("poll.question");
        List<String> options = data.getStringList("poll.options");
        String creator = data.getString("poll.creator");
        long endTime = data.getLong("poll.endTime");
        Map<String, Integer> results = new HashMap<>();
        if (data.contains("poll.results")) {
            for (String key : data.getConfigurationSection("poll.results").getKeys(false)) {
                results.put(key, data.getInt("poll.results." + key));
            }
        }
        long duration = (endTime - System.currentTimeMillis()) / 1000;
        if (duration > 0) {
            this.currentPoll = new Poll(question, options, creator, duration);
        } else {
            this.currentPoll = new Poll(question, options, creator, endTime, results);
            this.expiredPolls.add(currentPoll);
            this.currentPoll = null;
        }
    }
}