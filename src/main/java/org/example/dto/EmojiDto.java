package org.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmojiDto {
    private String name;
    private String category;
    private String group;
    private List<String> htmlCode;
}


