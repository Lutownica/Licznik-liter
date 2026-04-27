package com.example.lettercounter.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lettercounter.model.AnalysisTask;
import com.example.lettercounter.service.AnalysisService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final Map<UUID, AnalysisTask> tasks = new ConcurrentHashMap<>();
    private final AnalysisService analysisService;

    public TaskController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> payload) {
        String content = (String) payload.get("content");
        int workers = (int) payload.getOrDefault("workers", 1);

        AnalysisTask task = new AnalysisTask(content, workers);
        tasks.put(task.getId(), task);
        CompletableFuture.runAsync(() -> analysisService.processTask(task));
        return ResponseEntity.accepted().body(Map.of(
                "taskId", task.getId(),
                "status", task.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisTask> getTask(@PathVariable UUID id) {
        AnalysisTask task = tasks.get(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }
}