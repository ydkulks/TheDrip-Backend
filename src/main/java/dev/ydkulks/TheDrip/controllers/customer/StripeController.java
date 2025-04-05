package dev.ydkulks.TheDrip.controllers.customer;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.ShippingAddressCollection.AllowedCountry;

import dev.ydkulks.TheDrip.models.CheckoutProduct;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.CheckoutProduct.ProductItem;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

  @Autowired ProductRepository productRepository;

  @Value("${stripe.secret-key}")
  private String stripeSecretKey;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeSecretKey;
  }

  @PostMapping("/create-checkout-session")
  public ResponseEntity<String> createCheckoutSession(
      @RequestBody CheckoutProduct data) throws StripeException {

    List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

    String successUrl = data.getSuccessUrl();
    String cancelUrl = data.getCancelUrl();
    List<ProductItem> products = data.getProducts();

    List<SessionCreateParams.ShippingOption> shippingOptionsList = new ArrayList<>();
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mJ5QhqoBLs2E5BT9VFrHs").build()); // Standard
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mkpQhqoBLs2E58nHTw3y2").build()); // Express
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mm9QhqoBLs2E5kh9hWEzj").build()); // Overnight

    String indiaTaxRateId = "txr_1R9merQhqoBLs2E5gqhOoNjT";

    try {
      for (ProductItem productData : products) {
        Integer productId = productData.getProductId();
        Long qty = productData.getQty();

        ProductModel product = productRepository.findById(productId).orElse(null);
        if (product != null) {
          Double productPrice = product.getProductPrice() * 100;

          SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
            .addAllTaxRate(List.of(indiaTaxRateId))
            .setQuantity(qty)
            .setPriceData(
                SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount(productPrice.longValue())
                .setProductData(
                  SessionCreateParams.LineItem.PriceData.ProductData.builder()
                  .setName(product.getProductName())
                  .build())
                .build())
            .build();

          lineItems.add(lineItem);
        }
      }

      List<AllowedCountry> countries = new ArrayList<>();
        countries.add(SessionCreateParams.ShippingAddressCollection.AllowedCountry.IN);
        countries.add(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US);
        countries.add(SessionCreateParams.ShippingAddressCollection.AllowedCountry.JP);
      SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(successUrl)
        .setCancelUrl(cancelUrl)
        .addAllLineItem(lineItems) // Add all created line items
        .addAllShippingOption(shippingOptionsList)
        .setAllowPromotionCodes(true)
        .setShippingAddressCollection(
            SessionCreateParams.ShippingAddressCollection.builder()
            .addAllAllowedCountry(countries).build())
        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
        .build();

      Session session = Session.create(params);

      // Return the session URL as JSON
      return ResponseEntity.ok("{\"url\": \"" + session.getUrl() + "\"}");

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error creating checkout session\"}");
    }
  }
}
