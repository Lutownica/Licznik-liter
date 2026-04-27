package com.example.lettercounter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lettercounter.model.AnalysisTask;
import com.example.lettercounter.model.TaskStatus;

@Service
public class AnalysisService {

    public void processTask(AnalysisTask task) {
        task.setStatus(TaskStatus.RUNNING);
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(task.getWorkers());

        try {
            String content = task.getContent();
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Tekst jest pusty");
            }

            String[] words = content.split("\\s+");
            List<Future<Map<Character, Long>>> futures = new ArrayList<>();
            for (String word : words) {
                if (!word.trim().isEmpty()) {
                    futures.add(executor.submit(() -> countLetters(word)));
                }
            }

            Map<Character, Long> finalMap = new HashMap<>();
            for (Future<Map<Character, Long>> future : futures) {
                Map<Character, Long> wordMap = future.get();
                wordMap.forEach((key, value) -> finalMap.merge(key, value, Long::sum));
            }

            task.setResult(finalMap);
            task.setDurationMs(System.currentTimeMillis() - startTime);
            task.setStatus(TaskStatus.DONE);

        } catch (Exception e) {
            System.err.println("BLAD PODCZAS OBLICZEN: " + e.getMessage());
            e.printStackTrace();
            task.setStatus(TaskStatus.FAILED);
        } finally {
            executor.shutdown();
        }
    }

    private Map<Character, Long> countLetters(String word) {
        Map<Character, Long> result = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            result = word.toLowerCase().chars()
                    .filter(Character::isLetter)
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        }
        return result;
    }
}