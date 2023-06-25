package org.example.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;


@Data
@Entity
@Table(name = "EMOJI")
public class Emoji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "emoji_group")
    private String group;

    @Column(name = "html_code")
    private String htmlCode;
}
