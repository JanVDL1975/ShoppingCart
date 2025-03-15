package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.io.IOException;

class ShoppingCartTest {
    private ShoppingCart cart;
    private ShoppingCart spyCart;

    @Test
    public void testAddProductToCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        cart.addProduct("cheerios", 1);
        cart.addProduct("cornflakes", 2);

        // Check the quantities of products in the cart
        assertEquals(1, cart.getProductQuantity("cheerios"));
        assertEquals(2, cart.getProductQuantity("cornflakes"));
        assertEquals(0, cart.getProductQuantity("frosties")); // This product hasn't been added yet
    }
}

