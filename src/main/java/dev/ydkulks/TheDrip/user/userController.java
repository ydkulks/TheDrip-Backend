package dev.ydkulks.TheDrip.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {

  // NOTE: Signup page
  @GetMapping("/signup")
  public String signup() {
    return "Signup Page";
  }

  // NOTE: Login page
  @GetMapping("/login")
  public String login() {
    return "Login page";
  }

}
