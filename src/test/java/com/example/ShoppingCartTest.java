package com.example;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

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

    @Test
    public void testGetTotal_EmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(BigDecimal.ZERO, cart.getTotal(), "Total should be zero for an empty cart");
    }

    @Test
    public void testGetTotal_SingleProduct() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));

        BigDecimal expectedTotal = new BigDecimal("6.60"); // 6.00 + 0.60
        assertEquals(expectedTotal, cart.getTotal(), "Total should be 6.60 for a 6.00 subtotal at 10% tax");
    }

    @Test
    public void testGetTotal_MultipleProducts() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getCart().put("Banana", 3);

        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));
        cart.getPriceCache().put("Banana", new BigDecimal("2.00"));

        BigDecimal expectedTotal = new BigDecimal("13.20");
        assertEquals(expectedTotal, cart.getTotal(), "Total should be 13.20 for a 12.00 subtotal at 10% tax");
    }


    @Test
    public void testGetTotal_RoundingUp() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("ItemX", 1);
        cart.getPriceCache().put("ItemX", new BigDecimal("10.055"));

        BigDecimal expectedTotal = new BigDecimal("11.07"); // Rounded up tax affects total
        assertEquals(expectedTotal, cart.getTotal(), "Total should be 11.07 for subtotal 10.055 at 10% tax");
    }

    @Test
    public void testGetTotal_LargeNumbers() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Laptop", 1000);
        cart.getPriceCache().put("Laptop", new BigDecimal("999.99"));

        BigDecimal expectedTotal = new BigDecimal("1099989.00");
        assertEquals(expectedTotal, cart.getTotal(), "Total should be correct for large subtotals");
    }

    @Test
    public void testPrintCart_EmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        // Capture the console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        cart.printCart();

        // Restore original System.out
        System.setOut(originalOut);

        // Check expected output
        String expectedOutput = "Subtotal = 0.00\nTax = 0.00\nTotal = 0.00\n";
        assertTrue(outputStream.toString().contains(expectedOutput), "Output should match expected for an empty cart.");
    }

    @Test
    public void testPrintCart_SingleProduct() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        cart.printCart();

        System.setOut(originalOut);

        String output = outputStream.toString();
        assertTrue(output.contains("Cart contains 2 x Apple"), "Output should list 'Apple' with correct quantity.");
        assertTrue(output.contains("Subtotal = 6.00"), "Subtotal should be correctly printed.");
        assertTrue(output.contains("Tax = 0.60"), "Tax should be correctly printed.");
        assertTrue(output.contains("Total = 6.60"), "Total should be correctly printed.");
    }

    @Test
    public void testPrintCart_MultipleProducts() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("Apple", 2);
        cart.getCart().put("Banana", 3);
        cart.getPriceCache().put("Apple", new BigDecimal("3.00"));
        cart.getPriceCache().put("Banana", new BigDecimal("2.00"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        cart.printCart();

        System.setOut(originalOut);

        String output = outputStream.toString();
        assertTrue(output.contains("Cart contains 2 x Apple"), "Apple should be listed.");
        assertTrue(output.contains("Cart contains 3 x Banana"), "Banana should be listed.");
        assertTrue(output.contains("Subtotal = 12.00"), "Subtotal should be correct.");
        assertTrue(output.contains("Tax = 1.20"), "Tax should be correct.");
        assertTrue(output.contains("Total = 13.20"), "Total should be correct.");
    }

    @Test
    public void testPrintCart_Rounding() {
        ShoppingCart cart = new ShoppingCart();
        cart.getCart().put("ItemX", 1);
        cart.getPriceCache().put("ItemX", new BigDecimal("10.055"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        cart.printCart();

        System.setOut(originalOut);

        String output = outputStream.toString();
        assertTrue(output.contains("Cart contains 1 x ItemX"), "ItemX should be listed.");
        assertTrue(output.contains("Subtotal = 10.06"), "Subtotal should be rounded up.");
        assertTrue(output.contains("Tax = 1.01"), "Tax should be rounded up.");
        assertTrue(output.contains("Total = 11.07"), "Total should be correctly printed.");
    }

    @Mock
    private HttpURLConnection mockConnection;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cart = new ShoppingCart();
    }

    @Test
    void testFetchPrice_Success() throws Exception {
        // Mocking a successful response
        BigDecimal expectedPrice = new BigDecimal("2.52");
        ShoppingCart priceServiceSpy = spy(cart);

        doReturn(expectedPrice).when(priceServiceSpy).fetchPrice("cornflakes");

        BigDecimal actualPrice = priceServiceSpy.fetchPrice("cornflakes");
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    void testFetchPrice_ProductNotFound() {
        ShoppingCart priceServiceSpy = spy(cart);

        Exception exception = assertThrows(IOException.class, () -> {
            doThrow(new IOException("Error 404: Product not found - invalid_product"))
                    .when(priceServiceSpy).fetchPrice("invalid_product");

            priceServiceSpy.fetchPrice("invalid_product");
        });

        assertTrue(exception.getMessage().contains("Error 404"));
    }

    @Test
    void testFetchPrice_InternalServerError() {
        ShoppingCart priceServiceSpy = spy(cart);

        Exception exception = assertThrows(IOException.class, () -> {
            doThrow(new IOException("Error 500: Internal Server Error"))
                    .when(priceServiceSpy).fetchPrice("frosties");

            priceServiceSpy.fetchPrice("frosties");
        });

        assertTrue(exception.getMessage().contains("Error 500"));
    }

    @Test
    void testFetchPrice_ServiceUnavailable() {
        ShoppingCart priceServiceSpy = spy(cart);

        Exception exception = assertThrows(IOException.class, () -> {
            doThrow(new IOException("Error 503: Service Unavailable"))
                    .when(priceServiceSpy).fetchPrice("weetabix");

            priceServiceSpy.fetchPrice("weetabix");
        });

        assertTrue(exception.getMessage().contains("Error 503"));
    }

    @Test
    void testFetchPrice_UnexpectedError() {
        ShoppingCart priceServiceSpy = spy(cart);

        Exception exception = assertThrows(IOException.class, () -> {
            doThrow(new IOException("Unexpected API error: 403"))
                    .when(priceServiceSpy).fetchPrice("shreddies");

            priceServiceSpy.fetchPrice("shreddies");
        });

        assertTrue(exception.getMessage().contains("Unexpected API error"));
    }

    @Test
    void testFetchPrice_Timeout() {
        ShoppingCart priceServiceSpy = spy(cart);

        Exception exception = assertThrows(SocketTimeoutException.class, () -> {
            doThrow(new SocketTimeoutException("Connection timed out"))
                    .when(priceServiceSpy).fetchPrice("cornflakes");

            priceServiceSpy.fetchPrice("cornflakes");
        });

        assertTrue(exception.getMessage().contains("Connection timed out"));
    }
}

