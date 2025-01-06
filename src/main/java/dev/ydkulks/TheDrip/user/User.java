package dev.ydkulks.TheDrip.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * public class User {
 *   // Fields, Constructors, Getters and Setters
 * }
 */
@Entity
@Table(name = "USERS")
public class User {
  // Fields
  @Id
  private Long id;

  private String firstName;

  private String lastName;

  // Empty Constructor: JPA requries a default constructor for all entities
  public User() {

  }

  // Constructors
  public User(Long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  // Setters
  public void setId(Long id) {
    this.id = id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.firstName = lastName;
  }
}
