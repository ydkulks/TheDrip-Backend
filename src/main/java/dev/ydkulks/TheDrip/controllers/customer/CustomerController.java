package dev.ydkulks.TheDrip.controllers.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserReviewsDTO;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.services.UserReviewsService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
  @Autowired private UserReviewsService userReviewsService;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductRepository productRepository;

  @PostMapping("/review")
  public ResponseEntity<?> test(@RequestBody UserReviewsDTO data) {
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

  @GetMapping("/review")
  public ResponseEntity<?> getReviews(
      @RequestParam(required = false) Integer userId,
      @RequestParam(required = false) Integer productId,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);

      UserModel user = (userId != null) ? userRepo.findById(userId).orElse(null) : null;
      ProductModel product = (productId != null) ? productRepository.findById(productId).orElse(null) : null;

      Page<UserReviewsModel> response = userReviewsService.getReview(user, product, sortBy, sortDirection, pageable);
      return new ResponseEntity<Page<UserReviewsModel>>(response, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
