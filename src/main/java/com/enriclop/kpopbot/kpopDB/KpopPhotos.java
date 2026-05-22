package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.dto.IdolDTO;
import com.enriclop.kpopbot.dto.IdolListDTO;
import com.enriclop.kpopbot.modelo.Idol;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class KpopPhotos {

    @Autowired
    private KpopService kpopService;

    final static String[] bannedPhotos = {
            "google_play",
            "app_store",
            "kpopping_logo",
            "kpopping-logo",
            "momoshogun",
            "KpopMart-Logo",
            "Orange_Kpop_wiki_wordmark"
    };


    public static String getRandomPhoto(String url, String apiName, String name, String group) {
        log.info("Getting photo from: " + url);
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .referrer("https://www.google.com")
                    .timeout(10_000)
                    .get();

            log.info("location: " + doc.location());

            if (!doc.location().equals(url)) {
                log.info("Redirected to: " + doc.location());

                doc = Jsoup.connect(doc.location() + "?s=random")
                        .userAgent("Mozilla/5.0")
                        .referrer("https://www.google.com")
                        .timeout(10_000)
                        .get();

            }

            //Select <ul id="thumbs2">
            Elements imgs = doc.select("ul#thumbs2 img");

            List<String> imageUrls = new ArrayList<>();

            for (Element img : imgs) {
                String urlImage = img.attr("src");

                String alt = img.attr("alt").toLowerCase();
                if (alt.contains("download") || alt.contains("thumbnail") || alt.contains("icon") || alt.contains(group.toLowerCase())) {
                    continue;
                }

                if (!urlImage.isEmpty()) {
                    urlImage = urlImage.replace("/240/", "/full/");
                    imageUrls.add(urlImage);
                }
            }

            if (imageUrls.isEmpty()) {
                return null;
            }


            Random random = new Random();
            String randomImage = imageUrls.get(random.nextInt(imageUrls.size()));

            log.info(randomImage);

            return randomImage;
        } catch (Exception e) {
            log.error("Error fetching photo: " + e.getMessage());
            return null;
        }
    }

    public static String getRandomPhoto(String apiName, String name, String group) {
        //String url = "http://api.scrape.do/?url=https://kpop.fandom.com/wiki/" + apiName + "/Gallery&token=cb69170fb37f4b28bd477664af37d39f1b8a42a5a85&render=true";
        //String url = "https://kpop.fandom.com/wiki/" + apiName + "/Gallery";


        // Example apiName: "Kim Bo-ra (김보라)" and you want to search for "Kim Bora"
        name = name.replaceAll("\\s*\\([^)]*\\)\\s*", "");
        name = name.replaceAll("\\s+", " ").trim();
        name = name.replaceAll("-", "");
        name = name.replaceAll(" ", "+");

        String url = "https://kpop.asiachan.com/search?q=" + name;

        String image = getRandomPhoto(url, apiName, name, group);


        if (image == null) {
            //String url2 = "http://api.scrape.do/?url=https://kpop.fandom.com/wiki/" + apiName + "&token=cb69170fb37f4b28bd477664af37d39f1b8a42a5a85&render=true";
            //String url2 = "https://kpop.fandom.com/wiki/" + apiName;

            apiName = apiName.replaceAll("\\s*\\([^)]*\\)\\s*", "");
            apiName = apiName.replaceAll("\\s+", " ").trim();
            apiName = apiName.replaceAll("-", "");
            apiName = apiName.replaceAll(" ", "+");

            String url2 = "https://kpop.asiachan.com/search?q=" + apiName + "&s=random";

            image = getRandomPhoto(url2, apiName, name, group);
        }

        return image;
    }

    public PhotoCard generateRandomPhotocard() {
        Idol idol = kpopService.getRandomIdol();

        String photo;
        try {
            photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getBand());
            if (photo == null) {
                return generateRandomPhotocard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return generateRandomPhotocard();
        }

        return new PhotoCard(idol, photo);
    }

    public PhotoCard generateRandomPhotocardByRange(int min, int max) {
        Idol idol = kpopService.getRandomIdolByRange(min, max);

        String photo;
        try {
            photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getBand());
            if (photo == null) {
                return generateRandomPhotocardByRange(min, max);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return generateRandomPhotocardByRange(min, max);
        }

        return new PhotoCard(idol, photo);
    }

    public static PhotoCard regeneratePhotocard(PhotoCard card) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = KpopPhotos.class.getResourceAsStream("/kpopData/idols.json");) {

            if (inputStream == null) {
                throw new IOException("File not found: kpopData");
            }
            IdolListDTO idolList = mapper.readValue(inputStream, IdolListDTO.class);

            IdolDTO idol = idolList.getIdols().stream().filter(idolDTO -> idolDTO.getId().equals(card.getIdolID())).findFirst().orElse(null);

            String photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getGroup());
            if (photo == null) {
                return null;
            }

            card.setPhoto(photo);
            card.setApiName(idol.getApiName());
            return new PhotoCard(card);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        System.out.println(getRandomPhoto("SuA_(Dreamcatcher)", "Kim Bo-ra (김보라)", "Dreamcatcher"));
    }
}