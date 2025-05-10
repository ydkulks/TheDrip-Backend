package dev.ydkulks.TheDrip.controllers;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.LoginService;
import dev.ydkulks.TheDrip.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired private UserService service;
  @Autowired private UserService userService;

  // NOTE: Unauthenticated endpoints
  @PostMapping("/signup")
  public UserModel createUser(@RequestBody UserModel user) {
    return service.create(user);
  }

  // @GetMapping("/signup/{id}")
  // public Optional<UserModel> createUser(@PathVariable Integer id) {
  //   return userService.getUserById(id);
  // }

  @GetMapping("/login")
  public String login() {
    return "Login page";
  }
  @Autowired
  private LoginService loginService;

  @PostMapping("/login")
  public HashMap<String, String> login(@RequestBody UserModel user) {
    HashMap<String, String> jwtToken = new HashMap<>();
    jwtToken.put("token", loginService.verifyUser(user));
    return jwtToken;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable Integer id) {
    Optional<UserModel> user = userService.getUserById(id);
    return user.map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  @Data
  public static class ResetPasswordRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
  }
  @PutMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest data) {
    try {
      userService.resetPassword(data);
      // return ResponseEntity.ok("Password reset successfully");
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (UserService.UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DeleteUserRequest {
    private String password;
  }
  @DeleteMapping("/{id}/delete")
  public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestBody DeleteUserRequest password) {
    try {
      userService.deleteUser(id, password); // The service method doesn't return the deleted user
      return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Successful deletion, no content to return
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Specific error message
    } catch (Exception e) {
      // Log the error for debugging purposes
      e.printStackTrace();
      return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR); // Generic error
    }
  }

}
