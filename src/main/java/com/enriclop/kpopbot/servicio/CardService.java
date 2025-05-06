package com.enriclop.kpopbot.servicio;

import com.enriclop.kpopbot.enums.Types;
import com.enriclop.kpopbot.modelo.PhotoCard;
import com.enriclop.kpopbot.modelo.User;
import com.enriclop.kpopbot.repositorio.IPhotoCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private IPhotoCardRepository cardRepository;
    @Autowired
    private UserService userService;

    public CardService(IPhotoCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<PhotoCard> getCards() {
        return cardRepository.findAll();
    }

    public void saveCard(PhotoCard card) {
        cardRepository.save(card);
    }

    public PhotoCard getCardById(Integer id) {
        return cardRepository.findById(id).get();
    }

    public void deleteCardById(Integer id) {
        cardRepository.deleteById(id);
    }

    public void changeUser(PhotoCard card, User user) {
        //if the old owner has the card selected, remove it
        User oldOwner = card.getUser();
        List<Integer> selectedCards = oldOwner.getSelectedCards();
        if (selectedCards.contains(card.getId())) {
            selectedCards.remove(card.getId());
            oldOwner.setSelectedCards(selectedCards);
            userService.saveUser(oldOwner);
        }

        card.setUser(user);
        cardRepository.save(card);
    }

    public void createCustomCard(String name, String band, String fullName, String photo, Types type, Types type2, int attack, int defense, int hp, int popularity, User user) {
        PhotoCard card = new PhotoCard();
        card.setName(name);
        card.setPhoto(photo);
        card.setType(type);
        card.setType2(type2);
        card.setAttack(attack);
        card.setDefense(defense);
        card.setPopularity(popularity);
        card.setBand(band);
        card.setUser(user);
        card.setHp(hp);
        card.setFullName(fullName);

        card.setIdolID(0);

        cardRepository.save(card);
    }
}
