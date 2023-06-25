package org.example.repository;

import org.example.model.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Integer> {
}
