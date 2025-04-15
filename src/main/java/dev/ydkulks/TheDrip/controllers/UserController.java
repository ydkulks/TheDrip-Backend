package dev.ydkulks.TheDrip.controllers;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.LoginService;
import dev.ydkulks.TheDrip.services.UserService;

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

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(
    @RequestParam String username,
    @RequestParam String newPassword
  ) {
    try {
      userService.resetPassword(username, newPassword);
      return ResponseEntity.ok("Password reset successfully");
    } catch (UserService.UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

}
