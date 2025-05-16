package dev.ydkulks.TheDrip.services;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.controllers.UserController.DeleteUserRequest;
import dev.ydkulks.TheDrip.controllers.UserController.ResetPasswordRequest;
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
  public Page<UserModel> getAllUsers(Pageable pageable) {
    Page<UserModel> users = repo.findAll(pageable);
    if (users == null) {
      throw new UserNotFoundException("Users not found");
    }
    return users;
  }

  // Compare oldPassword with existing password before password reset
  @Transactional
  public void resetPassword(ResetPasswordRequest data) {
    UserModel user = repo.findByUsername(data.getUsername());
    if (user == null) {
      throw new UserNotFoundException(
        "User with username '" + data.getUsername() + "' not found"
      );
    }
    // Compare encrypted password before password reset
    if (data.getOldPassword() != null && encoder.matches(data.getOldPassword(), user.getPassword())) {
      user.setPassword(encoder.encode(data.getNewPassword()));
      // Flag to reset temp password set by admin during admin user creation
      if (user.getPasswordResetRequired().equals(true)) {
        user.setPasswordResetRequired(false);
      }
      repo.save(user);
    } else {
      throw new IllegalArgumentException("Invalid password for user ID: " + user.getId());
    }
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

  @Transactional
  public UserModel createAdminUser(UserModel user) {
    if (repo.findByUsername(user.getUsername()) != null) {
      throw new UserAlreadyExistsException(
          "User with username '" + user.getUsername() + "' already exists"
          );
    }

    String tempPassword = generateTemporaryPassword();
    user.setPassword(encoder.encode(tempPassword));
    System.out.println(tempPassword);

    user.setPasswordResetRequired(true);
    // TODO: Send email
    //  Here you would typically send an email to the user
    //  with the temporary password.  For example:
    //  emailService.sendTemporaryPassword(user.getUsername(), tempPassword);

    return repo.save(user);
  }

  // Helper method to generate a random password
  private String generateTemporaryPassword() {
    SecureRandom random = new SecureRandom();
    byte[] passwordBytes = new byte[16]; // 16 bytes = 128 bits
    random.nextBytes(passwordBytes);
    return Base64.getEncoder().encodeToString(passwordBytes);
  }
}
