package dev.ydkulks.TheDrip.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.services.SignupService;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private SignupService service;

  @PostMapping("/signup")
  public UserModel createUser(@RequestBody UserModel user) {
    return service.create(user);
  }

  // @GetMapping("/signup/{id}")
  // public Optional<UserModel> createUser(@PathVariable Integer id) {
  //   return userService.getUserById(id);
  // }

  // NOTE: Login page
  @GetMapping("/login")
  public String login() {
    return "Login page";
  }

}
