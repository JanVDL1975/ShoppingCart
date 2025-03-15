package com.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    /* Adding ann array to store the names of the valid products. This could also be read from config file. */
    private final String[] validProducts = {"cheerios",
            "cornflakes",
            "frosties",
            "shreddies",
            "weetabix"};

    /*There is a reasanable number of possible items that one could buy - for testability I am making it this value*/
    private final int MAX_POSSIBLE_NUM_ITEMS = 1000000;
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
    private static final int READ_TIMEOUT = 5000;       // 5 seconds

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
        String urlString = BASE_URL + productName + ".json";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set timeouts
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder inline = new StringBuilder();
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonObject = new JSONObject(inline.toString());
                return jsonObject.getBigDecimal("price");

            case 404:
                throw new IOException("Error 404: Product not found - " + productName);

            case 500:
                throw new IOException("Error 500: Internal Server Error");

            case 503:
                throw new IOException("Error 503: Service Unavailable");

            default:
                throw new IOException("Unexpected API error: " + responseCode);
        }
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

        /* Check if the product name is in the possible valid products */
        for(String name:validProducts) {
            if(productName.toLowerCase().compareTo(name) == 0) {
                validName = true;
                break;
            }
        }

        if(!validName) {
            throw  new IllegalArgumentException();
        }

        /* This is a physical limit, as one could expect in a real world scenario. */
        /* Note: In this case a value was randomly selected. */
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

    /**
     * Retrieves the current contents of the shopping cart.
     *
     * @return A map where the keys are product names and the values are their respective quantities.
     */
    public Map<String, Integer> getCart() {
        return cart;
    }

    /**
     * Retrieves the cached prices of products.
     *
     * @return A map where the keys are product names and the values are their respective prices.
     */
    public Map<String, BigDecimal> getPriceCache() {
        return priceCache;
    }

    /**
     * Checks if a product exists in the shopping cart.
     *
     * @param s The name of the product to check.
     * @return {@code true} if the product exists in the cart, {@code false} otherwise.
     */
    public boolean checkIfProductExists(String s) {
        return cart.containsKey(s);
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

    /**
     * Calculates the subtotal of the cart (sum of price * quantity for all items).
     *
     * @return The subtotal amount as a BigDecimal.
     */
    public BigDecimal getSubtotal() {
        return cart.entrySet().stream()
                .map(entry -> priceCache.get(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the tax payable (12.5% of the subtotal).
     * The tax is rounded up to 2 decimal places.
     *
     * @return The tax amount as a BigDecimal.
     */
    public BigDecimal getTax() {
        return getSubtotal().multiply(TAX_RATE).setScale(2, RoundingMode.UP);
    }

    /**
     * Calculates the total payable amount (subtotal + tax).
     *
     * @return The total amount as a BigDecimal.
     */
    public BigDecimal getTotal() {
        return getSubtotal().add(getTax());
    }

    /**
     * Prints the cart contents and the calculated totals (subtotal, tax, total).
     */
    public void printCart() {
        cart.forEach((product, quantity) -> System.out.println("Cart contains " + quantity + " x " + product));
        System.out.println("Subtotal = " + getSubtotal());
        System.out.println("Tax = " + getTax());
        System.out.println("Total = " + getTotal());
    }


    public static void main(String[] args) throws IOException {
        String[] products = {"cheerios", "cornflakes", "frosties", "shreddies", "weetabix"};
        ShoppingCart cart = new ShoppingCart();

        cart.addProduct("cheerios", 2);
        BigDecimal subTotal = cart.getSubtotal();
        System.out.println("The subtotal is: " + subTotal);

        BigDecimal tax = cart.getTax();
        System.out.println("The tax is: " + tax);

        BigDecimal total = cart.getTotal();
        System.out.println("The tax is: " + tax);

        // Add products to the cart
        for (String product : products) {
            cart.addProduct(product, 1);
        }

        // Display the cart contents
        System.out.println("Cart: " + cart.getCart());
        System.out.println("Price Cache: " + cart.getPriceCache());
    }
}
