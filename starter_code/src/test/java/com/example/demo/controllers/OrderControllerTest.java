package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository", userRepository);
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);
    }

    @Test
    public void submit_order() {
        // 1. Créer un item
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setPrice(BigDecimal.valueOf(2.99));

        // 2. Créer un cart avec cet item
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item)));
        cart.setTotal(BigDecimal.valueOf(2.99));

        // 3. Créer le user avec son cart
        User user = new User();
        user.setId(1L);
        user.setUsername("akoele");
        user.setCart(cart);
        cart.setUser(user);

        // 4. Simuler le repository
        when(userRepository.findByUsername("akoele")).thenReturn(user);

        // 5. Appeler et vérifier
        ResponseEntity<UserOrder> response = orderController.submit("akoele");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(2.99), order.getTotal());
        assertEquals("akoele", order.getUser().getUsername());
    }

    @Test
    public void submit_order_userNotFound() {
        when(userRepository.findByUsername("inconnu")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("inconnu");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user() {
        // 1. Créer un item
        Item item = new Item();
        item.setId(1L);
        item.setName("Widget");
        item.setPrice(BigDecimal.valueOf(2.99));

        // 2. Créer un cart avec cet item
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item)));
        cart.setTotal(BigDecimal.valueOf(2.99));

        // 3. Créer le user avec son cart
        User user = new User();
        user.setId(1L);
        user.setUsername("akoele");
        user.setCart(cart);
        cart.setUser(user);

        // 4. Créer la commande
        UserOrder order = new UserOrder();
        order.setId(1L);
        order.setItems(new ArrayList<>(List.of(item)));
        order.setTotal(BigDecimal.valueOf(2.99));
        order.setUser(user);

        // 5. Simuler le repository
        when(userRepository.findByUsername("akoele")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        // 6. Appeler et vérifier
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("akoele");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(1, orders.get(0).getId());
    }

    @Test
    public void get_orders_for_user_notFound() {
        when(userRepository.findByUsername("inconnu")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("inconnu");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
