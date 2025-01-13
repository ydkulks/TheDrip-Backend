package dev.ydkulks.TheDrip.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.models.UserModel;

@Service
public class UserService {
  private final UserRepo userRepo;

  public UserService(UserRepo userRepository){
    this.userRepo = userRepository;
  }

  public Iterable<UserModel> get() {
    return userRepo.findAll();
  }

  public UserModel save(UserModel user) {
    return userRepo.save(user);
  }

  public Optional<UserModel> getUserById(Long id) {
    return userRepo.findById(id);
  }
}
