package dev.ydkulks.TheDrip.controllers;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // NOTE: Signup page
  @GetMapping("/signup")
  public Iterable<UserModel> signup() {
    // NOTE: Signup logic
    // Check if user exists and create user if user does not exists
    // If already exists, send error stating account exists
    return userService.get();
  }

  @GetMapping("/signup/{id}")
  public Optional<UserModel> createUser(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  // NOTE: Login page
  @GetMapping("/login")
  public String login() {
    return "Login page";
  }

}
