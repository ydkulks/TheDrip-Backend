package dev.ydkulks.TheDrip.controllers.customer;

import com.stripe.exception.StripeException;

import dev.ydkulks.TheDrip.models.CheckoutProduct;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.CheckoutProduct.ProductItem;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.services.CartService;
import dev.ydkulks.TheDrip.services.StripeService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

  @Autowired ProductRepository productRepository;
  @Autowired StripeService stripeService;
  @Autowired CartService cartService;

  @PostMapping("/create-checkout-session")
  public ResponseEntity<?> createCheckoutSession(
      @RequestBody CheckoutProduct data) throws StripeException {

    try {
      Map<String, String> checkoutResponse = stripeService.createCheckoutSession(data);
      return ResponseEntity.ok(checkoutResponse);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("{\"error\": \"Error creating checkout session\"}");
    }
  }

  @PutMapping("/success")
  public ResponseEntity<?> checkoutSuccess(@RequestBody CheckoutProduct data) {
    try{
      List<ProductItem> products = data.getProducts();
      for (ProductItem productItem : products) {
        Integer productId = productItem.getProductId();
        Long quantity = productItem.getQty();

        // Fetch the product from the database
        ProductModel product = productRepository.findById(productId).orElse(null);

        if (product != null) {
          // Update stock and sold counts
          Integer currentStock = product.getProductStock();
          Integer currentSold = product.getProductSold();

          if (currentSold == null) {
            product.setProductSold(quantity.intValue());
          } else {
            product.setProductSold(currentSold + quantity.intValue());
          }

          if (currentStock == null) {
            // // Not stock == No update
            // product.setProductStock(quantity.intValue());
          } else {
            product.setProductStock(currentStock - quantity.intValue());
          }
          System.out.println("CurrentStock: " + currentStock + " CurrentSold :" + currentSold);

          // Save the updated product back to the database
          productRepository.save(product);

          // Delete from cart
          // cartService.removeFromCart(data.getCartItemsId());
          List<Integer> cartItemIds = data.getCartItemsId();
          if (cartItemIds != null && !cartItemIds.isEmpty()) {
            for (Integer cartItemId : cartItemIds) {
              // cartService.removeFromCart(cartItemId);
              System.out.println(cartItemId);
              try {
                cartService.removeFromCart(cartItemId);
              } catch (Exception e) {
                // Handle exceptions during cart item deletion.
                System.err.println("Error deleting cart item " + cartItemId + ": " + e.getMessage());
                // You might want to log the error and potentially return an error response.
                // Depending on your requirements, you could continue deleting other items,
                // or return an error and stop the process. Returning an error might be best
                // to ensure data consistency.

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Error deleting cart item: " + e.getMessage());
              }
            }
          }
        }
      }
      return new ResponseEntity<String>(HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("{\"error\": \"Error creating checkout session\"}");
    }
  }
}
