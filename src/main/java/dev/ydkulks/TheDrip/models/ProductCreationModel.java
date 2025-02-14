package dev.ydkulks.TheDrip.models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "product")
public class ProductCreationModel {
  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer product_id;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private ProductCategoriesModel category;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserModel user;

  @ManyToOne
  @JoinColumn(name = "series_id", nullable = false)
  private ProductSeriesModel series;

  @Column(name = "product_price", nullable = false)
  private Double productPrice;

  @Column(name = "product_description", columnDefinition = "TEXT")
  private String productDescription;

  @Column(name = "product_stock", nullable = false)
  private Integer productStock;

  @ManyToMany
  @JoinTable(
    name = "product_product_sizes",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "size_id")
  )
  private Set<ProductSizesModel> sizes = new HashSet<>();

  @ManyToMany
  @JoinTable(
    name = "product_product_colors",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "color_id")
  )
  private Set<ProductColorsModel> colors = new HashSet<>();


  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @CreationTimestamp
  private Timestamp created;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(updatable = false, insertable = false)
  @UpdateTimestamp
  private Timestamp updated;
}
