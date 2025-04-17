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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
            "KpopMart-Logo"
    };



    public static String getRandomPhoto(String apiName, String name, String group) {
        String url = "https://kpop.fandom.com/wiki/" + apiName + "/Gallery";

        System.out.println(url);
        try {
            Document doc = Jsoup.connect(url).get();
            List<Element> images = doc.select("img");
            if (images.isEmpty()) {
                return null;
            }

            images.removeIf(image -> {
                for (String banned : bannedPhotos) {
                    if (image.attr("src").contains(banned)) {
                        return true;
                    }
                }

                if (!image.attr("src").contains(name) && !image.attr("src").contains(group)) {
                    return true;
                }

                return false;
            });

            Random random = new Random();
            Element randomImage = images.get(random.nextInt(images.size()));
            String imageUrl = randomImage.attr("src");

            imageUrl = imageUrl.substring(0, imageUrl.indexOf(".png") + 4);

            if (imageUrl.equals("htt")) {
                return getRandomPhoto(apiName, name, group);
            }

            return imageUrl;
        } catch (IOException e) {
            String url2 = "https://kpop.fandom.com/wiki/" + apiName;

            try {
                Document doc = Jsoup.connect(url2).get();
                List<Element> images = doc.select("img");
                if (images.isEmpty()) {
                    return null;
                }


                images.removeIf(image -> {
                    for (String banned : bannedPhotos) {
                        if (image.attr("src").contains(banned)) {
                            return true;
                        }
                    }

                    return !image.attr("src").contains(name) && !image.attr("src").contains(group) && !image.attr("src").contains(apiName);
                });

                Random random = new Random();
                Element randomImage = images.get(random.nextInt(images.size()));
                String imageUrl = randomImage.attr("src");

                imageUrl = imageUrl.substring(0, imageUrl.indexOf(".png") + 4);

                if (imageUrl.equals("http")) {
                    return getRandomPhoto(apiName, name, group);
                }

                return imageUrl;
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    public PhotoCard generateRandomPhotocard() {
        try {

            Idol idol = kpopService.getRandomIdol();

            String photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getBand());
            if (photo == null) {
                return generateRandomPhotocard();
            }

            return new PhotoCard(idol, photo);
        } catch (Exception e) {
            e.printStackTrace();
            return generateRandomPhotocard();
        }
    }

    public PhotoCard generateRandomPhotocardByRange(int min, int max) {
        Idol idol = kpopService.getRandomIdolByRange(min, max);

        String photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getBand());
        if (photo == null) {
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
        System.out.println(getRandomPhoto("Jang_Wonyoung", "Wonyoung", "IVE"));
    }
}