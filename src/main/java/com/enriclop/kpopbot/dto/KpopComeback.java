package com.enriclop.kpopbot.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class KpopComeback {
    public String day;
    public String time;
    public String artist;
    public String albumTitle;
    public String albumType;
    public String titleTrack;
    public String streaming;

    public KpopComeback(String day, String time, String artist, String albumTitle, String albumType, String titleTrack, String streaming) {
        this.day = day;
        this.time = time;
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.albumType = albumType;
        this.titleTrack = titleTrack;
        this.streaming = streaming;
    }

    public static List<KpopComeback> parseScheduledReleases(String json) throws JsonProcessingException {
        // Parse JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String html = root.at("/data/content_md").asText();

        log.info("HTML" + html);

        List<KpopComeback> comebacks = new ArrayList<>();

        // Regex to capture table rows starting with "|"
        Pattern rowPattern = Pattern.compile("^\\|.*\\|$", Pattern.MULTILINE);
        Matcher matcher = rowPattern.matcher(html);

        while (matcher.find()) {
            String row = matcher.group();
            if (row.contains("--")) continue; // skip header separators

            String[] parts = row.split("\\|", -1);
            if (parts.length >= 8) {
                KpopComeback comeback = new KpopComeback(
                        parts[1].trim(), // day
                        parts[2].trim(), // time
                        parts[3].trim(), // artist
                        parts[4].trim(), // album title
                        parts[5].trim(), // album type
                        parts[6].trim(), // title track
                        parts[7].trim()  // streaming
                );
                comebacks.add(comeback);
            }
        }

        return comebacks;
    }
}