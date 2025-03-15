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

    @Test
    public void testMaxNumberOfAvailableProductsInCart() throws IOException {
        ShoppingCart cart = new ShoppingCart();

        // Expect an exception when adding an invalid product
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addProduct("cheerios", 1000001);
        });
    }

    @Test
    public void testGetSubtotal_EmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(BigDecimal.ZERO, cart.getSubtotal(), "Subtotal should be zero for an empty cart");
    }

    @Test
    public void testGetSubtotal_SingleProduct() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));

        assertEquals(new BigDecimal("6.00"), cart.getSubtotal(), "Subtotal should be 6.00 for 2 Apples at 3.00 each");
    }

    @Test
    public void testGetSubtotal_MultipleProducts() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getCart().put("Banana", 3);

        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));
        cart.getPriceCache().put("Banana", new BigDecimal("2.00"));

        assertEquals(new BigDecimal("12.00"), cart.getSubtotal(), "Subtotal should be 12.00 for multiple products");
    }

    @Test
    public void testGetSubtotal_ProductWithoutPrice() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2); // Apple added to cart
        // Price cache is empty or missing Apple

        assertThrows(NullPointerException.class, cart::getSubtotal, "Should throw an exception when a product has no price");
    }

    @Test
    public void testGetSubtotal_LargeNumbers() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Laptop", 1000);
        cart.getPriceCache().put("Laptop", new BigDecimal("999.99"));

        assertEquals(new BigDecimal("999990.00"), cart.getSubtotal(), "Subtotal should be correct for large numbers");
    }


    @Test
    public void testGetTax_EmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(BigDecimal.ZERO, cart.getTax(), "Tax should be zero for an empty cart");
    }


    @Test
    public void testGetTax_SingleProduct() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));

        BigDecimal expectedTax = new BigDecimal("0.60"); // Assuming TAX_RATE is 10%
        assertEquals(expectedTax, cart.getTax(), "Tax should be 0.60 for 6.00 subtotal at 10%");
    }

    @Test
    public void testGetTax_MultipleProducts() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getCart().put("Banana", 3);

        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));
        cart.getPriceCache().put("Banana", new BigDecimal("2.00"));

        BigDecimal expectedTax = new BigDecimal("1.20");
        assertEquals(expectedTax, cart.getTax(), "Tax should be 1.20 for a 12.00 subtotal at 10%");
    }

    @Test
    public void testGetTax_RoundingUp() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("ItemX", 1);
        cart.getPriceCache().put("ItemX", new BigDecimal("10.055"));

        BigDecimal expectedTax = new BigDecimal("1.01"); // Rounded up
        assertEquals(expectedTax, cart.getTax(), "Tax should round up to 1.01 for subtotal 10.055 at 10%");
    }

    @Test
    public void testGetTax_LargeNumbers() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Laptop", 1000);
        cart.getPriceCache().put("Laptop", new BigDecimal("999.99"));

        BigDecimal expectedTax = new BigDecimal("99999.00");
        assertEquals(expectedTax, cart.getTax(), "Tax should be correct for large subtotals");
    }
}

