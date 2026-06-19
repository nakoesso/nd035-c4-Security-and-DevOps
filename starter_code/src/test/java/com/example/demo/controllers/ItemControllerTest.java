package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);


    @BeforeEach
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Widget");
        item1.setDescription("Un widget standard");
        item1.setPrice(BigDecimal.valueOf(9.99));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Gadget");
        item2.setDescription("Un gadget premium");
        item2.setPrice(BigDecimal.valueOf(19.99));

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals("Widget", body.get(0).getName());
        assertEquals("Gadget", body.get(1).getName());
    }

    @Test
    public void get_item_by_id() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setPrice(BigDecimal.valueOf(9.99));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Widget", response.getBody().getName());
    }

    @Test
    public void get_item_by_id_notFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(99L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_items_by_name() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setPrice(BigDecimal.valueOf(9.99));

        when(itemRepository.findByName("Widget")).thenReturn(List.of(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Widget");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("Widget", body.get(0).getName());
    }

    @Test
    public void get_items_by_name_notFound() {
        when(itemRepository.findByName("Inexistant")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Inexistant");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
