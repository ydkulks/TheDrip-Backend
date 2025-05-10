package dev.ydkulks.TheDrip.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

  @Autowired ProductImageService productImageService;
  @Autowired UserService userService;

  @DeleteMapping("/all/product/image")
  public ResponseEntity<?> deleteImages() {
    try {
      productImageService.deleteAllImages("thedrip");
      return new ResponseEntity<>("Deleted: All images of all products", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/user")
  public ResponseEntity<?> createUserByAdmin(@RequestBody UserModel userData) {
    try {
      UserModel createdUser = userService.createAdminUser(userData);
      return new ResponseEntity<>(createdUser, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }
}
