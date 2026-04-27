package com.example.lettercounter.model;

import java.util.Map;
import java.util.UUID;

public class AnalysisTask {
    private final UUID id;
    private final String content;
    private final int workers;
    private TaskStatus status;
    private Map<Character, Long> result;
    private String errorMessage;
    private long durationMs;

    public AnalysisTask(String content, int workers) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.workers = workers;
        this.status = TaskStatus.QUEUED;
    }

    public UUID getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Map<Character, Long> getResult() {
        return result;
    }

    public void setResult(Map<Character, Long> result) {
        this.result = result;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getWorkers() {
        return workers;
    }

    public String getContent() {
        return content;
    }
}