package dev.ydkulks.TheDrip.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.LoginService;
import dev.ydkulks.TheDrip.services.SignupService;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private SignupService service;

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

}
