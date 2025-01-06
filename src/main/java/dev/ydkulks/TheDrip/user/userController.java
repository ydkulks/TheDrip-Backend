package dev.ydkulks.TheDrip.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {

  private final userService userService;

  public userController(userService userService){
    this.userService = userService;
  }

  // NOTE: Signup page
  @GetMapping("/signup")
  public Iterable<User> signup() {
    return userService.get();
  }

  // NOTE: Login page
  @GetMapping("/login")
  public String login() {
    return "Login page";
  }

}
