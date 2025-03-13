package com.example.demo.akka.controller;

import com.example.demo.akka.service.AkkaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/akka")
public class AkkaController {

    private final AkkaService akkaService;

    public AkkaController(AkkaService akkaService) {
        this.akkaService = akkaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Lire les lignes du fichier
            List<String> lines = Files.readAllLines(file.getResource().getFile().toPath());
            
            // Distribuer les lignes aux mappers
            for(int i = 0; i < lines.size(); i++) {
                akkaService.processLine(lines.get(i), i);
            }
            
            return ResponseEntity.ok("Fichier traité avec succès");
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body("Erreur de traitement du fichier: " + e.getMessage());
        }
    }

    @GetMapping("/count/{word}")
    public ResponseEntity<Integer> getWordCount(@PathVariable String word) {
        try {
            int count = akkaService.getWordCount(word);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(-1);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service actif");
    }
}