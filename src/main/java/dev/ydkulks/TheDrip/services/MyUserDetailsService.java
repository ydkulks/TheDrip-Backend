package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserPrincipal;
import dev.ydkulks.TheDrip.repos.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService{
  @Autowired
  private UserRepo repo;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserModel user = repo.findByUsername(username);

    if(user == null) {
      System.out.println("user Not Found");
      throw new UsernameNotFoundException("User Not Found");
    }
    // System.out.println(user);

    return new UserPrincipal(user);
  }
}
