package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart() {
        User user = createUserWithEmptyCart();
        Item item = createItem();

        when(userRepository.findByUsername("akoele")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("akoele");
        request.setItemId(1L);
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(2, cart.getItems().size());
        assertEquals(BigDecimal.valueOf(5.98), cart.getTotal());
    }

    @Test
    public void add_to_cart_user_not_found() {
        when(userRepository.findByUsername("inconnu")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("inconnu");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = createUserWithEmptyCart();
        when(userRepository.findByUsername("akoele")).thenReturn(user);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("akoele");
        request.setItemId(99L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart() {
        Item item = createItem();

        // Cart contient déjà 3 exemplaires de l'item
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);
        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);

        User user = new User();
        user.setId(1L);
        user.setUsername("akoele");
        user.setCart(cart);
        cart.setUser(user);

        when(userRepository.findByUsername("akoele")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("akoele");
        request.setItemId(1L);
        request.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart resultCart = response.getBody();
        assertNotNull(resultCart);
        assertEquals(1, resultCart.getItems().size());
        assertEquals(BigDecimal.valueOf(2.99), resultCart.getTotal());
    }

    @Test
    public void remove_from_cart_user_not_found() {
        when(userRepository.findByUsername("inconnu")).thenReturn(null);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("inconnu");
        request.setItemId(1L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_from_cart_item_not_found() {
        User user = createUserWithEmptyCart();
        when(userRepository.findByUsername("akoele")).thenReturn(user);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("akoele");
        request.setItemId(99L);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User createUserWithEmptyCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        User user = new User();
        user.setId(1L);
        user.setUsername("akoele");
        user.setCart(cart);
        cart.setUser(user);
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        return item;
    }
}
