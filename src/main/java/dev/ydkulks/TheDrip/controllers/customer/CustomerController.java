package dev.ydkulks.TheDrip.controllers.customer;

import java.lang.System.Logger;

// import org.checkerframework.checker.index.qual.Positive;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserReviewsDTO;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.repos.UserReviewsRepository;
import dev.ydkulks.TheDrip.services.CartService;
import dev.ydkulks.TheDrip.services.UserReviewsService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
  @Autowired
  private UserReviewsService userReviewsService;
  @Autowired
  private UserReviewsRepository userReviewsRepository;
  @Autowired
  private CartService cartService;

  @PostMapping("/review")
  public ResponseEntity<?> createOrUpdate(@RequestBody UserReviewsDTO data) {
    try {
      UserReviewsModel response = userReviewsService.createOrUpdateReview(
          data.getUser(),
          data.getProduct(),
          data.getReview_title(),
          data.getReview_text(),
          data.getRating());
      UserReviewsDTO responseDTO = new UserReviewsDTO();
      if (response != null) {
        responseDTO.setUser(data.getUser());
        responseDTO.setProduct(data.getProduct());
        responseDTO.setReview_title(data.getReview_title());
        responseDTO.setReview_text(data.getReview_text());
        responseDTO.setRating(data.getRating());
      }
      return new ResponseEntity<UserReviewsDTO>(responseDTO, HttpStatus.OK);

    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/review")
  public ResponseEntity<String> delete(@RequestParam Integer reviewId) {
    try {
      UserReviewsModel response = userReviewsRepository.findById(reviewId).orElse(null);
      if (response != null) {
        userReviewsRepository.deleteById(reviewId);
        return new ResponseEntity<String>(response.getReviewTitle() + " got deleted!", HttpStatus.OK);
      }
      return new ResponseEntity<String>("Review not found!", HttpStatus.NOT_FOUND);

    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/items") // More specific endpoint for adding items
  public ResponseEntity<?> addToCart(
      @RequestParam Integer userId,
      @RequestParam Integer productId,
      @RequestParam Integer quantity) {
    try {
      cartService.addToOrUpdateCart(userId, productId, quantity);
      // Return a more informative response
      return new ResponseEntity<>("Item added to cart successfully!", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
