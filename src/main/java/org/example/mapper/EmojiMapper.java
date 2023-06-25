package org.example.mapper;

import org.example.dto.EmojiDto;
import org.example.model.Emoji;

import java.util.ArrayList;
import java.util.List;

public class EmojiMapper {

    public static Emoji dtoToEntity(EmojiDto dto) {
        Emoji entity = new Emoji();
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setGroup(dto.getGroup());
        entity.setHtmlCode(dto.getHtmlCode().get(0));
        return entity;
    }

    public static EmojiDto entityToDto(Emoji entity) {
        EmojiDto dto = new EmojiDto();
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setGroup(entity.getGroup());

        List<String> htmlCodeList = new ArrayList<>();
        htmlCodeList.add(entity.getHtmlCode());

        dto.setHtmlCode(htmlCodeList);

        return dto;
    }
}
