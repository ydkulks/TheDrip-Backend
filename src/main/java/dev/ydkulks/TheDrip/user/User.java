package dev.ydkulks.TheDrip.user;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;

/*
 * public class User {
 *   // Fields, Getters and Setters
 * }
 */
// @Entity
// @Table(name = "USERS")
public class User {
  // Fields
  // @Id
  // @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  // @Column(name = "FIRST_NAME", length = 20, nullable = false, unique = false, updatable = true)
  private String firstName;

  // @Column(name = "LAST_NAME", length = 20, nullable = false, unique = false, updatable = true)
  private String lastName;

  // Empty Constructor
  public User(){

  }

  // Constructors
  public User(Long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.firstName = lastName;
  }
}
