package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class UserService {
  @Autowired
  private UserRepo repo;

  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  @Transactional
  public UserModel create(UserModel user) {
    if (repo.findByUsername(user.getUsername()) != null) {
      throw new UserAlreadyExistsException(
        "User with username '" + user.getUsername() + "' already exists"
      );
    }

    user.setPassword(encoder.encode(user.getPassword()));
    return repo.save(user);
  }

  public static class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
      super(message);
    }
  }
}
