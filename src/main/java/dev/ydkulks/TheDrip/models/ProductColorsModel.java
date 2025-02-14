package dev.ydkulks.TheDrip.models;

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
@Table(name = "product_colors")
public class ProductColorsModel {
  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer color_id;

  private String color_name;
}
