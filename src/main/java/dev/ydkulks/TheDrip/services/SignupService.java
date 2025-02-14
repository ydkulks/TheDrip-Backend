package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class SignupService {
  @Autowired
  private UserRepo repo;

  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  @Transactional
  public UserModel create(UserModel user) {
    user.setPassword(encoder.encode(user.getPassword()));
    return repo.save(user);
  }
}
