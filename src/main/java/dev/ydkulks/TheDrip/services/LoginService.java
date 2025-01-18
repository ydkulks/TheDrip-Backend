package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.UserModel;

@Service
public class LoginService {
  @Autowired
  AuthenticationManager authManager;

  @Autowired
  JWTService jwtService;

  public String verifyUser(UserModel user) {
    Authentication auth = 
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword())
        );
    if (auth.isAuthenticated()) return jwtService.generateToken(user.getUsername());
    return "Fail";
  }
}
