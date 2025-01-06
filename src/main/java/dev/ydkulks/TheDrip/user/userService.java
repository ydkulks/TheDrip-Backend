package dev.ydkulks.TheDrip.user;

import org.springframework.stereotype.Service;

@Service
public class userService {
  private final userRepository userRepository;

  public userService(userRepository userRepository){
    this.userRepository = userRepository;
  }

  public Iterable<User> get() {
    return userRepository.findAll();
  }
}
