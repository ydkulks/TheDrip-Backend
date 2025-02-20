package dev.ydkulks.TheDrip.controllers.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserReviewsDTO;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.services.UserReviewsService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
  @Autowired private UserReviewsService userReviewsService;
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
}
