package org.example.service;

import org.example.model.Emoji;
import org.example.repository.EmojiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmojiService {
    private final EmojiRepository emojiRepository;

    @Autowired
    public EmojiService(EmojiRepository emojiRepository) {
        this.emojiRepository = emojiRepository;
    }

    public Emoji create(Emoji emoji) {
        return emojiRepository.save(emoji);
    }

    public List<Emoji> getAll() {
        return emojiRepository.findAll();
    }

    public Optional<Emoji> getById(Integer id) {
        return emojiRepository.findById(id);
    }

    public Emoji update(Integer id, Emoji updatedEmoji) {
        Emoji emoji = emojiRepository.findById(id).orElse(null);
        if (emoji != null) {
            emoji.setName(updatedEmoji.getName());
            // Update other properties as needed
            return emojiRepository.save(emoji);
        }
        return null;
    }

    public void delete(Integer id) {
        emojiRepository.deleteById(id);
    }
}
