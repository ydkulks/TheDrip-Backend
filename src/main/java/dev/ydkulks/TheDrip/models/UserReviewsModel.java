package dev.ydkulks.TheDrip.models;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "user_reviews")
public class UserReviewsModel {
  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "review_id", updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer reviewId;

  @Column(name = "review_title")
  private String reviewTitle;

  @Column(name = "review_text")
  private String reviewText;

  @Column(name = "rating")
  private Integer rating;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserModel user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductModel product;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @CreationTimestamp
  private Timestamp created;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @UpdateTimestamp
  private Timestamp updated;
}
