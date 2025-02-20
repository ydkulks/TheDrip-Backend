package dev.ydkulks.TheDrip.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.repos.UserReviewsRepository;
import jakarta.transaction.Transactional;

@Service
public class UserReviewsService {
  @Autowired private UserReviewsRepository userReviewsRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductRepository productRepository;

  @Transactional
  public UserReviewsModel createOrUpdateReview(
      Integer user,
      Integer product,
      String reviewTitle,
      String reviewText,
      Integer rating
      ) {
    UserModel userObj = userRepo
      .findById(user)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + user));
    ProductModel productObj = productRepository
      .findById(product)
      .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + product));

    Optional<UserReviewsModel> reviewStatus = userReviewsRepository.findByUser_IdAndProduct_ProductId(user, product);

    UserReviewsModel review;
    if (reviewStatus.isEmpty()) {
      // If review does not exists, create a new one
      review = new UserReviewsModel();
    } else {
      // Update if review exists
      review = reviewStatus.get();
    }

    review.setUser(userObj);
    review.setProduct(productObj);
    review.setReviewTitle(reviewTitle);
    review.setReviewText(reviewText);
    review.setRating(rating);
    return userReviewsRepository.save(review);
  }
}
