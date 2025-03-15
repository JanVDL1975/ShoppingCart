## :warning: Please read these instructions carefully and entirely first
* Clone this repository to your local machine.
* Use your IDE of choice to complete the assignment.
* When you have completed the assignment, you need to  push your code to this repository and [mark the assignment as completed by clicking here](https://app.snapcode.review/submission_links/f2508b33-e77c-4bc8-9d0f-9812c78437e4).
* Once you mark it as completed, your access to this repository will be revoked. Please make sure that you have completed the assignment and pushed all code from your local machine to this repository before you click the link.
* There is no time limit for this task - however, for guidance, it is expected to typically take around 1-2 hours.

# Begin the task

Write some code that provides the following basic shopping cart capabilities:

1. Add a product to the cart
   1. Specifying the product name and quantity
   2. Retrieve the product price by issuing a request to the [Price API](#price-api) specified below
   3. Cart state (totals, etc.) must be available

2. Calculate the state:
   1. Cart subtotal (sum of price for all items)
   2. Tax payable (charged at 12.5% on the subtotal)
   3. Total payable (subtotal + tax)
   4. Totals should be rounded up where required (to two decimal places)

## Price API

The price API is an existing API that returns the price details for a product, identified by it's name. The shopping cart should integrate with the price API to retrieve product prices. 

### Price API Service Details

Base URL: `https://equalexperts.github.io/`

View Product: `GET /backend-take-home-test-data/{product}.json`

List of available products
* `cheerios`
* `cornflakes`
* `frosties`
* `shreddies`
* `weetabix`

## Example
The below is a sample with the correct values you can use to confirm your calculations

### Inputs
* Add 1 × cornflakes @ 2.52 each
* Add another 1 x cornflakes @2.52 each
* Add 1 × weetabix @ 9.98 each
  
### Results  
* Cart contains 2 x cornflakes
* Cart contains 1 x weetabix
* Subtotal = 15.02
* Tax = 1.88
* Total = 16.90

## Tips on what we’re looking for

We value simplicity as an architectural virtue and as a development practice. Solutions should reflect the difficulty of the assigned task, and shouldn’t be overly complex. We prefer simple, well tested solutions over clever solutions. 

### DO

* ✅ Include unit tests.
* ✅ Test both any client and logic.
* ✅ Update the README.md with any relevant information, assumptions, and/or tradeoffs you would like to highlight.

### DO NOT

* ❌ Submit any form of app, such as web APIs, browser, desktop, or command-line applications.
* ❌ Add unnecessary layers of abstraction.
* ❌ Add unnecessary patterns/ architectural features that aren’t called for e.g. persistent storage.

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------
# Approach

## Given:
* Prices need to be fetched from an external API.
* Request is a GET.
* Need to make an http call, using GET.

# Issues retrieving price, related to the HTTP calls:

-------------------------------------------------------------------------------------------------------------------------------------------
The request for the price is a Web Request. It means that several things can go wrong!

### 404 Not Found:
The web server cannot locate the specific page or resource you're trying to access, likely because the page no longer exists or the URL is incorrect.

### 500 Internal Server Error
The product does not exist in the API.
Example: Requesting /backend-take-home-test-data/invalid_product.json.

### 503 Service Unavailable
The API server has an internal issue.
Example: The backend has a temporary failure.

### 400 Bad Request
The API is down or temporarily unavailable.

### Malformed request

### Timeout Errors:
The API takes too long to respond.

### Connection Errors:
The network is unavailable, or the API cannot be reached.
-------------------------------------------------------------------------------------------------------------------------------------------

## Given:
* There is a list of GIVEN products
* *IMPORTANT*: This implies that you may also receive products NOT given, say because of typo's?
* For each product there is a quantity involved.
  I need to keep track of: 
* 1) products in the cart and 
  2) number of the products.
* You need to keep track of: Cart State

### What is the Cart's State seen as?
* Items, number of items, subtotal.

### Calculations to be made:

* Total number of a specific item. (To be multiplied by the price for that item.)
* Find the subtotal
* Calculate the Tax on the subtotal
* Find Subtotal + Tax
* Round up to 2 decimal places.

### What is the tax rate: 
* 12.5%, charged on the Subtotal.

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------

# Play with the API to see what is returned:

## Valid call:

* Call made: https://equalexperts.github.io/backend-take-home-test-data/cheerios.json , 
  
leads to:
  
{
  "title": "Cheerios",
  "price": 8.43
}

## Spelling error:

* Change input call, make spelling error: https://equalexperts.github.io/backend-take-home-test-data/cheeris.json
* Leads to 404, file not found.

## No extension supplied:
Forgetting the .json leads to:
* 404 not found.

## Check for case sensitivity
Tried:

https://equalexperts.github.io/backend-take-home-test-data/WEETABIX.json

This seems to work.

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------

# NOTE:

How about collecting all the prices in advance? In this way you need to traverse this list once on setup?
Possible drawback: If item is not requested, you don't HAVE to make the call for the price!

Is this important for the client or not? 

Pros: 
* After the prices are fetched, no more API calls have to be made.
  In the scenario where you "lose communications" or the site become unavialble AFTER this,
  you could calculate "offline", since you have everything you need then.

Cons:
* If item is not requested, you don't HAVE to make the call for the price!
  If you make the single call and it fails, what happens later if you get the same request?
* Should the price change often and unexpectedly, say like with stock prices, you
  will not be current with the latest price. Is this acceptable?
* What happens if you collect all the prices on startup and on price is unavailable?
  How do you decide on validity? Should ALL be available and if not, how many may be unavailable?

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------

# Design consideration:
How do I handle it when no price is returned? Then it is "unspecified", but how does it affect the 
rest of the program? There is no price to add to Subtotal. This will "affect" the numbers and calculations, since 
everything is not available?

I could simply exclude the item from the Cart?
I guess this could be considered as the "No Stock Available" scenario?
The most reasonable approach is leave it out of the cart - if you shop and the shop is "Out of stock", 
you cannot load something in the Cart that is not there!

Quastion:
Should the user be notified?

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------
# Design consideration:
I made an initial version, where all the prices are read in one go.

Question:
Should the 'available items' not rather be read from a file or somewhere?
In that case the code needs no recompilation if it changes. The values can be supplied externally?

Some options are:
* file: JSON or simple text file
* environment variables.

Potentially all the inputs can be gathered from a file. If the names are given, the number of can also be added?

-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------

* config.properties format:

product1=cheerios
product2=cornflakes
product3=frosties
product4=shreddies
product5=weetabix

Usage:
// Load the properties file
Properties properties = new Properties();
properties.load(new FileInputStream("config.properties"));

Read product names from the properties file
String product1 = properties.getProperty("product1");
String product2 = properties.getProperty("product2");
String product3 = properties.getProperty("product3");
String product4 = properties.getProperty("product4");
String product5 = properties.getProperty("product5");

-------------------------------------------------------------------------------------------------------------------------------------------
* config.json file format:

{
    "products": [
    "cheerios",
    "cornflakes",
    "frosties",
    "shreddies",
    "weetabix"
    ]
}

Usage: 

// Load the JSON file
JSONObject config = new JSONObject(new FileReader("config.json"));

// Get the list of products from the JSON file
JSONArray products = config.getJSONArray("products");

for (int i = 0; i < products.length(); i++) {
    System.out.println(products.getString(i));
}

-------------------------------------------------------------------------------------------------------------------------------------------
* Environment variables:

export PRODUCT_1="cheerios"
export PRODUCT_2="cornflakes"
export PRODUCT_3="frosties"
export PRODUCT_4="shreddies"
export PRODUCT_5="weetabix"

Usage:

// Read the product names from environment variables
String product1 = System.getenv("PRODUCT_1");
String product2 = System.getenv("PRODUCT_2");
String product3 = System.getenv("PRODUCT_3");
String product4 = System.getenv("PRODUCT_4");
String product5 = System.getenv("PRODUCT_5");
-------------------------------------------------------------------------------------------------------------------------------------------
