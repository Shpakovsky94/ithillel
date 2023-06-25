package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.EmojiDto;
import org.example.mapper.EmojiMapper;
import org.example.model.Emoji;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
public class ScheduledService {

    private final RestTemplate restTemplate;
    private final EmojiService emojiService;

    @Value("${app.url}")
    private String url;

    public ScheduledService(RestTemplate restTemplate, EmojiService emojiService) {
        this.restTemplate = restTemplate;
        this.emojiService = emojiService;
    }

    @Scheduled(fixedRateString = "${app.scheduledFixedRate}")
    private void saveNewEmojiToDb() {
        try {
            log.info("invoke saveNewEmojiToDb()");

            // Set the request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the RequestEntity with GET method and headers
            RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(url));

            // Send the request and get the response
            ResponseEntity<EmojiDto> responseEntity = restTemplate.exchange(requestEntity, EmojiDto.class);
            EmojiDto emojiDto = responseEntity.getBody();
            log.info("Gotten emoji: {}", emojiDto);

            if (emojiDto != null) {
                Emoji emoji = EmojiMapper.dtoToEntity(emojiDto);
                emojiService.create(emoji);
            }

        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
    }
}
