package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.dto.IdolDTO;
import com.enriclop.kpopbot.dto.IdolListDTO;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class KpopPhotos {

    final static String[] bannedPhotos = {
            "google_play",
            "app_store",
            "kpopping_logo",
            "kpopping-logo",
            "momoshogun",
            "KpopMart-Logo"
    };



    public static String getRandomPhoto(String apiName, String name, String group) {
        //https://kpop.fandom.com/wiki/Gahyun_(Dreamcatcher)/Gallery

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

            //images.forEach(image -> System.out.println("https://kpopping.com" + image.attr("src")));

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

                    if (!image.attr("src").contains(name) && !image.attr("src").contains(group)) {
                        return true;
                    }

                    return false;
                });

                //images.forEach(image -> System.out.println("https://kpopping.com" + image.attr("src")));

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

    public static PhotoCard generateRandomPhotocard() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = KpopPhotos.class.getResourceAsStream("/kpopData/idols.json");) {
            if (inputStream == null) {
                throw new IOException("File not found: kpopData");
            }
            IdolListDTO idolList = mapper.readValue(inputStream, IdolListDTO.class);

            boolean found = false;
            IdolDTO idol = null;
            while (!found) {
                int randomIndex = new Random().nextInt(idolList.getIdols().size());
                idol = idolList.getIdols().get(randomIndex);
                if (new Random().nextInt(100) > idol.getPopularity() - 30){
                    found = true;
                }
            }

            String photo = getRandomPhoto(idol.getApiName(), idol.getName(), idol.getGroup());
            if (photo == null) {
                return null;
            }

            return new PhotoCard(idol, photo);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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