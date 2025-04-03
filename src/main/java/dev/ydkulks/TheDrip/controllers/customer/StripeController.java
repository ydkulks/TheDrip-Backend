package dev.ydkulks.TheDrip.controllers.customer;
import com.stripe.Stripe;
import com.stripe.model.PaymentLink.TaxIdCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import dev.ydkulks.TheDrip.models.ProductModel;
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
      @RequestParam Integer productId,
      // TODO: Checkout for multiple products
      // @RequestParam List<Integer> productId,
      // @RequestParam List<Long> qty,
      @RequestParam Long qty,
      @RequestParam(value = "successUrl") String successUrl,
      @RequestParam(value = "cancelUrl") String cancelUrl
      ) {

    List<SessionCreateParams.ShippingOption> shippingOptionsList = new ArrayList<>();
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mJ5QhqoBLs2E5BT9VFrHs").build()); // Standard
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mkpQhqoBLs2E58nHTw3y2").build()); // Express
    shippingOptionsList.add(SessionCreateParams.ShippingOption.builder()
        .setShippingRate("shr_1R9mm9QhqoBLs2E5kh9hWEzj").build()); // Overnight

    String indiaTaxRateId = "txr_1R9merQhqoBLs2E5gqhOoNjT";

    try {
      ProductModel product = (productId != null) ? productRepository.findById(productId).orElse(null) : null;
      Double productPrice = product.getProductPrice() * 100;

      SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(successUrl)
        .setCancelUrl(cancelUrl)
        // .setAutomaticTax(
        //     SessionCreateParams.AutomaticTax.builder()
        //     .setEnabled(true
        //       ).build())
        .addAllShippingOption(shippingOptionsList)
        .setAllowPromotionCodes(true)
        .addLineItem(
            SessionCreateParams.LineItem.builder()
            // .addTaxRate(indiaTaxRateId)
            .addAllTaxRate(List.of(indiaTaxRateId))
            .setQuantity(qty)
            .setPriceData(
              SessionCreateParams.LineItem.PriceData.builder()
              .setCurrency("usd")
              .setUnitAmount(productPrice.longValue()) // $20.00 (in cents)
              .setProductData(
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(product.getProductName())
                .build())
              .build())
            .build())
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
