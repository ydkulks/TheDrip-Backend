package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.UserRepo;

@Service
public class LoginService {
  @Autowired
  AuthenticationManager authManager;

  @Autowired
  JWTService jwtService;

  @Autowired
  UserRepo repo;

  public String verifyUser(UserModel user) {
    Authentication auth = 
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword())
        );

    UserModel userData = repo.findByUsername(user.getUsername());
    if (auth.isAuthenticated()) return jwtService.generateToken(
        userData.getEmail(),
        user.getUsername(),
        userData.getRole(),
        userData.getId()
      );
    return "Fail";
  }
}
