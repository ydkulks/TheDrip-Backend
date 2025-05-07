package dev.ydkulks.TheDrip.services;

import java.security.InvalidParameterException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.controllers.UserController.DeleteUserRequest;
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

  @Transactional
  public Optional<UserModel> getUserById(Integer id) {
    return repo.findById(id);
  }

  @Transactional
  public void resetPassword(String username, String newPassword) {
    UserModel user = repo.findByUsername(username);
    if (user == null) {
      throw new UserNotFoundException(
        "User with username '" + username + "' not found"
      );
    }

    user.setPassword(encoder.encode(newPassword));
    repo.save(user);
  }

  @Transactional
  public void deleteUser(Integer userId, DeleteUserRequest password) {
    UserModel user = repo.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

    // Compare encrypted password before deleting user
    if (password != null && encoder.matches(password.getPassword(), user.getPassword())) {
      repo.delete(user); // deletes the user
    } else {
      throw new IllegalArgumentException("Invalid password for user ID: " + userId);
    }
  }

  public static class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
      super(message);
    }
  }

  public static class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
      super(message);
    }
  }
}
