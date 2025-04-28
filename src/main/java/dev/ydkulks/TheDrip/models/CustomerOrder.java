package dev.ydkulks.TheDrip.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CustomerOrderId.class)
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
  @Id
  @Column(name = "user_id")
  private Integer userId;

  @Id
  @Column(name = "product_id")
  private Integer productId;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "order_amount")
  private BigDecimal orderAmount;

  @Column(name = "order_status")
  private String orderStatus;
}
