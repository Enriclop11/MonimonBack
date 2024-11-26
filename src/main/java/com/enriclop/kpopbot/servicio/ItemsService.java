package com.enriclop.kpopbot.servicio;

import com.enriclop.kpopbot.modelo.Items;
import com.enriclop.kpopbot.repositorio.IItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsService {

    @Autowired
    private IItemsRepository itemsRepository;

    public ItemsService(IItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public List<Items> getItems() {
        return itemsRepository.findAll();
    }

    public Items getItemById(Integer id) {
        return itemsRepository.findById(id).get();
    }

    public Items saveItem(Items item) {
        return itemsRepository.save(item);
    }

    public void deleteItemById(Integer id) {
        itemsRepository.deleteById(id);
    }
}
