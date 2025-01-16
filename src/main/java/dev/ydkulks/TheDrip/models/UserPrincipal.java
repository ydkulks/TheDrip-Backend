package dev.ydkulks.TheDrip.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
  private UserModel user;

  public UserPrincipal(UserModel user) {
    this.user = user;
  }

  // NOTE: Got these modules from UserDetails interface and modified them with @Override
  @Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
    return Collections.singleton(new SimpleGrantedAuthority("USER"));
  };

  @Override
	public String getPassword(){
    return user.getPassword();
  };

  @Override
	public String getUsername(){
    return user.getUsername();
  };

  @Override
	public  boolean isAccountNonExpired() {
		return true;
	}

  @Override
	public boolean isAccountNonLocked() {
		return true;
	}

  @Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

  @Override
	public boolean isEnabled() {
		return true;
	}
}
