package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.dto.*;
import com.enriclop.kpopbot.modelo.Badge;
import com.enriclop.kpopbot.modelo.Idol;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.repositorio.IBadgesRepository;
import com.enriclop.kpopbot.repositorio.IIdolRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class KpopService {

    @Autowired
    private IIdolRepository idolRepository;

    @Autowired
    private IBadgesRepository badgesRepository;

    @PostConstruct
    public void init() {
        try {
            if (System.getProperty("spring.profiles.active").equals("prod")) {
                loadIdols();
                loadBadges();
            }
        } catch (NullPointerException e) {
            log.info("Not in prod mode");
        }
    }

    public List<Idol> getIdols() {
        return idolRepository.findAll();
    }

    public Idol getIdolById(String id) {
        return idolRepository.findById(id);
    }

    public List<Idol> getActiveIdols() {
        return idolRepository.findByIsActiveTrue();
    }

    public void saveIdol(Idol idol) {
        idolRepository.save(idol);
    }

    public void deleteIdolById(Integer id) {
        idolRepository.deleteById(id);
    }

    public List<Badge> getBadges() {
        return badgesRepository.findAll();
    }

    public Badge getBadgeById(Integer id) {
        return badgesRepository.findById(id).orElse(null);
    }

    public void saveBadge(Badge badge) {
        badgesRepository.save(badge);
    }

    public void deleteBadgeById(Integer id) {
        badgesRepository.deleteById(id);
    }

    public void loadIdols() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = KpopPhotos.class.getResourceAsStream("/kpopData/idols.yaml");) {
            if (inputStream == null) {
                throw new IOException("File not found: kpopData");
            }
            IdolListDTO idolList = mapper.readValue(inputStream, IdolListDTO.class);

            getIdols().forEach(idol -> {
                log.info("Checking idol: " + idol.getName());
                IdolDTO newIdol = idolList.getIdols().stream().filter(idolDTO -> idolDTO.getId().equals(idol.getId())).findFirst().orElse(null);
                if (newIdol == null) {
                    idol.setActive(false);
                    saveIdol(idol);
                } else if (!idol.equals(newIdol)) {
                    idol.setApiName(newIdol.getApiName());
                    idol.setBand(newIdol.getGroup());
                    idol.setPopularity(newIdol.getPopularity());
                    idol.setName(newIdol.getName());
                    saveIdol(idol);
                }
            });

            idolList.getIdols().forEach(idolDTO -> {
                if (getIdolById(idolDTO.getId()) == null) {
                    saveIdol(new Idol(idolDTO));
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Idol getRandomIdol() {
        List<Idol> idols = getActiveIdols();
        return idols.get((int) (Math.random() * idols.size()));
    }

    public String getRandomPhoto(String id) {
        Idol idol = getIdolById(id);
        if (idol == null) {
            return null;
        }
        return KpopPhotos.getRandomPhoto(idol.getApiName(), idol.getName(), idol.getBand());
    }

    public void loadBadges() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = KpopPhotos.class.getResourceAsStream("/kpopData/badges.yaml");) {
            if (inputStream == null) {
                throw new IOException("File not found: kpopData");
            }
            BadgeListDTO badgeList = mapper.readValue(inputStream, BadgeListDTO.class);

            badgeList.getBadges().forEach(badgeDTO -> {
                log.info("Checking badge: " + badgeDTO.getName());
               saveBadge(new Badge(badgeDTO));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BoosterPackListDTO getBoosterPacks() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = KpopPhotos.class.getResourceAsStream("/kpopData/boosterPacks.yaml");) {
            if (inputStream == null) {
                throw new IOException("File not found: kpopData");
            }

            return mapper.readValue(inputStream, BoosterPackListDTO.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BoosterPackDTO getBoosterPackById(int id) {
        return getBoosterPacks().getBoosterPacks().stream().filter(boosterPackDTO -> boosterPackDTO.getId() == id).findFirst().orElse(null);
    }

    public int getPrice(PhotoCard photoCard) {
        int price = 0;

        int popularity = photoCard.getPopularity() * 10;
        int attack = photoCard.getAttack();
        int defense = photoCard.getDefense();
        int hp = photoCard.getHp() / 10;

        price = popularity + attack + defense + hp;

        price /= 10;

        return price;
    }
}