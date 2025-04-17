package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.modelo.Idol;
import com.enriclop.kpopbot.security.Settings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class KpopApi {

    @Autowired
    private KpopService kpopService;

    @Autowired
    Settings settings;

    private static final String WEB_URL = "https://kpop.fandom.com/wiki/";
    private static final String GIRL_GROUPS = "Category:Female_groups";
    private static final String SPOTIFY_AUTH = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_API = "https://api.spotify.com/v1/search?q=";

    private static final List<String> EXCLUDED_GROUPS = List.of(
            "YOUNGEST",
            "Kirots",
            "SONOKI",
            "KIDOLS",
            "VIVIDIVA"
    );

    private static List<String> getAllFemaleGroups() throws IOException {

        List<String> femaleGroups = new java.util.ArrayList<>(List.of());

        while (femaleGroups.isEmpty() || !femaleGroups.get(femaleGroups.size() - 1).startsWith("Z")) {

            String url = WEB_URL + GIRL_GROUPS;
            if (!femaleGroups.isEmpty()) url = url + "?from=" + femaleGroups.get(femaleGroups.size() - 1);
            Document doc = Jsoup.connect(url).get();
            doc.select(".category-page__member-link").forEach(element -> {
                String groupName = element.text();
                femaleGroups.add(groupName);
            });
        }

        log.info("Female groups: " + femaleGroups.size());

        // Filter out excluded groups


        return femaleGroups.stream()
                .filter(group -> !EXCLUDED_GROUPS.contains(group))
                .toList();
    }

    private List<Idol> getAllFemaleIdols() throws IOException {

        List<String> femaleGroups = getAllFemaleGroups();
        List<Idol> femaleIdols = new java.util.ArrayList<>(List.of());

        for (String group : femaleGroups) {
            String url = WEB_URL + group;
            log.info("Getting idols from: " + url);
            Document doc = Jsoup.connect(url).get();
            final boolean[] active = {true, false};

            doc.select("table.wikitable").forEach(
                    element -> {
                        element.select("tr").forEach(member -> {

                            if (active[1]) return;

                            String memberName = member.select("a").text();
                            String memberUrl = member.select("a").attr("href");
                            memberUrl = memberUrl.replace("/wiki/", "");

                            if (memberName.isEmpty()) {
                                String info = member.select("th").text();
                                if (info.contains("Pre-debut")) {
                                    active[1] = true;
                                    return;
                                }
                                if (info.contains("Disbanded")) {
                                    active[0] = false;
                                    return;
                                }
                                if (info.contains("Inactive")) {
                                    active[0] = false;
                                    return;
                                }
                                if (info.contains("Former")) {
                                    active[0] = false;
                                    return;
                                }
                                return;
                            }

                            if (memberUrl.isEmpty()) {
                                return;
                            }

                            String[] info = member.select("td").text().split(", ");

                            List<Types> types = new java.util.ArrayList<>(List.of());
                            for (String s : info) {
                                Types type = Types.fromString(s);
                                if (type != Types.NONE) {
                                    types.add(type);
                                }
                            }

                            Idol idol = new Idol();
                            idol.setName(memberName);
                            idol.setApiName(memberUrl);
                            idol.setBand(group);
                            idol.setActive(active[0]);

                            if (!types.isEmpty()) {
                                if (types.size() > 1) {
                                    idol.setType(types.get(0));
                                    idol.setType2(types.get(1));
                                } else {
                                    idol.setType(types.get(0));
                                }
                            }

                            try {
                                getIdolInfo(idol);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            femaleIdols.add(idol);
                        });
                    }
            );
        }

        return femaleIdols;
    }

    public Idol getIdolInfo(Idol idol) throws IOException, InterruptedException {
        String url = WEB_URL + idol.getApiName();
        log.info("Getting idol info from: " + url);

        Document doc = Jsoup.connect(url).get();

        String birthName = doc.select("div[data-source=birth_name] > div").text();
        idol.setFullName(birthName);

        int groupPopularity = getArtistPopularity(List.of(idol.getBand()));

        groupPopularity = (int) (groupPopularity * 1.35);
        if (groupPopularity > 100) {
            groupPopularity = 100;
        }
        idol.setPopularity(groupPopularity);


        return idol;
    }

    @Scheduled(cron = "0 0 0 */2 * *")
    public void updateIdols() throws IOException {
        List<Idol> idols = getAllFemaleIdols();

        for (Idol idol : idols) {

            List<Idol> existingIdol = kpopService.getIdolByApiName(idol.getApiName());

            if (existingIdol.isEmpty()) {
                idol.setType(Types.randomType());
            } else {
                Idol sameGroupIdol = existingIdol.stream().filter(i -> i.getBand().equals(idol.getBand())).findFirst().orElse(null);
                if (sameGroupIdol != null) {
                    idol.setId(sameGroupIdol.getId());
                    idol.setType(sameGroupIdol.getType());
                    idol.setType2(sameGroupIdol.getType2());
                } else {
                    idol.setType(Types.randomType());
                }
            }

            if (idol.getType() == null) {
                idol.setType(Types.NONE);
            }

            if (idol.getType() == Types.NONE) {
                idol.setType(Types.randomType());
            }

            kpopService.saveIdol(idol);
        }
    }

    public static String getAccessToken(String clientId, String clientSecret) throws IOException, InterruptedException {
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPOTIFY_AUTH))
                .header("Authorization", "Basic " + auth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());
        return json.get("access_token").asText();
    }

    public int getArtistPopularity(List<String> artists) throws IOException, InterruptedException {
        String accessToken = getAccessToken(settings.getSpotifyClientId(), settings.getSpotifyClientSecret());

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        for (String name : artists) {
            String query = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String url = SPOTIFY_API + query + "&type=artist&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = mapper.readTree(response.body());
            JsonNode artist = json.path("artists").path("items").get(0);

            if (artist != null) {
                return artist.get("popularity").asInt();
            }
        }

        return 0;
    }
}
