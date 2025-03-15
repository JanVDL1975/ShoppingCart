package com.example;

import com.example.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.io.IOException;

import static org.mockito.Mockito.*;

class ShoppingCartTest {
    private ShoppingCart cart;
    private ShoppingCart spyCart;

    @BeforeEach
    void setUp() throws IOException {
        cart = new ShoppingCart();
        spyCart = Mockito.spy(cart);

        // Mock API responses for known products
        doReturn(new BigDecimal("2.52")).when(spyCart);
        ShoppingCart.fetchPrice("cornflakes");
        doReturn(new BigDecimal("9.98")).when(spyCart);
        ShoppingCart.fetchPrice("weetabix");
        doReturn(new BigDecimal("3.75")).when(spyCart);
        ShoppingCart.fetchPrice("shreddies");
    }
}