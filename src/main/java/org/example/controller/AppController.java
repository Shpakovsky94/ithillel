package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Emoji;
import org.example.service.EmojiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class AppController {
    private final EmojiService emojiService;

    public AppController(EmojiService emojiService) {
        this.emojiService = emojiService;
    }

    @PostMapping
    public Emoji createEmoji(@RequestBody Emoji emoji) {
        return emojiService.create(emoji);
    }

    @GetMapping("/")
    public String hello() {
        return "Working";
    }

    @GetMapping("/all")
    public List<Emoji> getAllEmojis() {
        return emojiService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emoji> getEmojiById(@PathVariable Integer id) {
        Optional<Emoji> emoji = emojiService.getById(id);
        return emoji.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emoji> updateEmoji(@PathVariable Integer id, @RequestBody Emoji updatedEmoji) {
        Emoji emoji = emojiService.update(id, updatedEmoji);
        if (emoji != null) {
            return ResponseEntity.ok(emoji);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmoji(@PathVariable Integer id) {
        emojiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
