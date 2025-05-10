package dev.ydkulks.TheDrip.models;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "USERS")
public class UserModel {
  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  private String email;

  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  private String username;

  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  private String password;

  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  private String role;

  @JsonProperty(access = JsonProperty.Access.READ_WRITE)
  @Column(name = "is_pwd_reset_required")
  private Boolean passwordResetRequired;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @CreationTimestamp
  private Timestamp created;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @UpdateTimestamp
  private Timestamp updated;
}
