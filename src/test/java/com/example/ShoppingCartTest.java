package com.example;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void testAddNoNameProductToCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        // Expect an exception when adding an invalid product
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addProduct("", 1);
        });

        // Optionally, check the cart's state (ensure product doesn't get added)
        boolean val = cart.checkIfProductExists("");
        assertFalse(val); // Assuming checkIfProductExists returns false for non-existing products
    }

    @Test
    public void testAddInvalidQuantityProductToCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        // Expect an exception when adding an invalid product
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addProduct("cheerios", -1);
        });
    }

    @Test
    public void testAddProductNotInAvialableListToCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        // Expect an exception when adding an invalid product
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addProduct("cheeri", 1);
        });
    }

    @Test
    public void testFetchProductPriceNotAvailableCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        // Expect an exception when adding an invalid product
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addProduct("cheeri", 1);
        });
    }

}

