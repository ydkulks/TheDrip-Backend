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
@Table(name = "categories")
public class ProductCategoriesModel {
  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "category_id", updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer categoryId;

  @Column(name = "category_name", nullable = false)
  private String categoryName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @CreationTimestamp
  private Timestamp created;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @UpdateTimestamp
  private Timestamp updated;
}
