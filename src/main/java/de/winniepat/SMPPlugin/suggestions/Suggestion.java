package de.winniepat.SMPPlugin.suggestions;

import java.time.Instant;
import java.util.UUID;

public class Suggestion {
    private final int id;
    private final UUID playerUuid;
    private final String playerName;
    private final String title;
    private final String content;
    private SuggestionStatus status;
    private final Instant timestamp;

    public Suggestion(int id, UUID playerUuid, String playerName, String title, String content) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.title = title;
        this.content = content;
        this.status = SuggestionStatus.OPEN;
        this.timestamp = Instant.now();
    }

    public int getId() { return id; }
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public SuggestionStatus getStatus() { return status; }
    public void setStatus(SuggestionStatus status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
}
