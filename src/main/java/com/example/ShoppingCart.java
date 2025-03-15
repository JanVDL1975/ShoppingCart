package com.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;


/**
 * Represents a simple shopping cart that allows adding products,
 * fetching their prices from an external API, and calculating totals.
 */
public class ShoppingCart {
    private static final String BASE_URL = "https://equalexperts.github.io/backend-take-home-test-data/";
    private static final BigDecimal TAX_RATE = new BigDecimal("0.125");
    private final String[] validProducts = {"cheerios",
            "cornflakes",
            "frosties",
            "shreddies",
            "weetabix"};
    /*There is a reasanable number of possible items that one could buy - for testability I am making it this value*/
    private final int MAX_POSSIBLE_NUM_ITEMS = 1000000;

    // Store item and number of the specific item.
    private Map<String, Integer> cart = new HashMap<>();

    // Store the price for the item - don't want to make unnecessary calls to the API!
    private Map<String, BigDecimal> priceCache = new HashMap<>();

    public ShoppingCart() {
        // Constructor
    }

    /**
     * Fetches the price of a product from the external API.
     *
     * @param productName The name of the product.
     * @return The price of the product as a BigDecimal.
     * @throws IOException If there's an issue with the API request.
     */
    public BigDecimal fetchPrice(String productName) throws IOException {
        boolean validName = false;

        for(String name:validProducts) {


            if(productName.toLowerCase().compareTo(name) == 0) {
                validName = true;
                break;
            }
        }

        if(!validName) {
            throw  new IllegalArgumentException();
        }

        String urlString = BASE_URL + productName + ".json";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch price for " + productName);
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject jsonObject = new JSONObject(inline.toString());
        return jsonObject.getBigDecimal("price");
    }

    /**
     * Adds a product to the cart.
     * If the product is not in the cart, it fetches the price from the API.
     *
     * @param productName The name of the product.
     * @param quantity    The quantity of the product to add.
     * @throws IOException If there's an issue fetching the price from the API.
     */
    public void addProduct(String productName, int quantity) throws IOException {
        boolean validName = false;

        // Empty product name should not be allowed.
        if(productName.isEmpty()){
           throw  new IllegalArgumentException();
        }

        // This is debateable - should 0 be allowed or not? It would work probably - simply zero times price.
        if(quantity <= 0) {
           throw  new IllegalArgumentException();
        }

        for(String name:validProducts) {


            if(productName.toLowerCase().compareTo(name) == 0) {
                validName = true;
                break;
            }
        }

        if(!validName) {
            throw  new IllegalArgumentException();
        }

        if(quantity > MAX_POSSIBLE_NUM_ITEMS) {
            throw  new IllegalArgumentException();
        }

        // Add the product to the cart
        cart.put(productName, cart.getOrDefault(productName, 0) + quantity);

        // Fetch the price and store it in the priceCache
        if (!priceCache.containsKey(productName)) {
            BigDecimal price = fetchPrice(productName);
            priceCache.put(productName, price);
        }
    }

    public Map<String, Integer> getCart() {
        return cart;
    }

    public Map<String, BigDecimal> getPriceCache() {
        return priceCache;
    }

    public static void main(String[] args) throws IOException {
        String[] products = {"cheerios", "cornflakes", "frosties", "shreddies", "weetabix"};
        ShoppingCart cart = new ShoppingCart();

        // Add products to the cart
        for (String product : products) {
            cart.addProduct(product, 1);
        }

        // Display the cart contents
        System.out.println("Cart: " + cart.getCart());
        System.out.println("Price Cache: " + cart.getPriceCache());
    }

    /**
     * Gets the quantity of a specific product from the cart.
     *
     * @param productName The name of the product.
     * @return The quantity of the product, or 0 if the product is not in the cart.
     */
    public int getProductQuantity(String productName) {
        return cart.getOrDefault(productName, 0); // Returns 0 if productName is not found
    }

    public boolean checkIfProductExists(String s) {
        return cart.containsKey(s);
    }
}
