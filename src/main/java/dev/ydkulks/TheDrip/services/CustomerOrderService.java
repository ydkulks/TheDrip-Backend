package dev.ydkulks.TheDrip.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartModel;
import dev.ydkulks.TheDrip.models.CustomerOrder;
import dev.ydkulks.TheDrip.models.CustomerOrderDTO;
import dev.ydkulks.TheDrip.models.CustomerOrderId;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.repos.CartItemsRepository;
import dev.ydkulks.TheDrip.repos.CartRepository;
import dev.ydkulks.TheDrip.repos.CustomerOrderRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerOrderService {
  // @Autowired CustomerOrderService customerOrderService;
  @Autowired CustomerOrderRepository customerOrderRepository;
  @Autowired CartRepository cartRepository;
  @Autowired CartItemsRepository cartItemsRepository;
  // @Autowired CustomerOrderService customerOrderService;
  @Autowired ProductRepository productRepository;

  @Transactional
  private CustomerOrder saveCustomerOrder(CustomerOrder customerOrder) {
    Optional<CustomerOrder> existingOrder = customerOrderRepository
      .findById(
          new CustomerOrderId(customerOrder.getUserId(), customerOrder.getProductId())
          );
    if (existingOrder.isPresent()) {
      //Update existing order
      CustomerOrder existing = existingOrder.get();
      existing.setQuantity(customerOrder.getQuantity());
      existing.setOrderAmount(customerOrder.getOrderAmount());
      existing.setOrderStatus(customerOrder.getOrderStatus());
      return customerOrderRepository.save(existing);
    }
    return customerOrderRepository.save(customerOrder);
  }

  // @Transactional
  // public Optional<CustomerOrder> getCustomerOrder(Integer userId, Integer productId) {
  //   return customerOrderRepository.findById(new CustomerOrderId(userId, productId));
  // }

  @Transactional
  private Page<CustomerOrder> getCustomerOrder(
      Integer userId,
      String sortBy,
      String sortDirection,
      Pageable pageable
      ) { // Changed return type and parameters
    Sort sort = null;
    if (sortBy != null && !sortBy.isEmpty()) {
      Sort.Direction direction =
        sortDirection != null && sortDirection.equalsIgnoreCase("desc")
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
      sort = Sort.by(direction, sortBy);
    }

    // Create a new Pageable object with the Sort information
    Pageable sortedPageable = pageable;
    if (sort != null) {
      sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
    return customerOrderRepository.findByUserId(userId, sortedPageable);
  }

  // NOTE: DELETE
  @Transactional
  public void deleteCustomerOrder(Integer userId, Integer productId) {
    customerOrderRepository.deleteById(new CustomerOrderId(userId, productId));
  }
  
  // NOTE: CREATE
  @Transactional
  public ResponseEntity<?> createOrder(Integer cartItemId, Integer productId, Long quantity, Double productPrice) {
    // Integer firstCartItemId = cartItemIds.get(0); // Ensure list isn't empty first.
    CartItemsModel firstCartItem = cartItemsRepository.findById(cartItemId)
      .orElseThrow(() -> new EntityNotFoundException("Cart item not found with ID: " + cartItemId));
    CartModel firstCart = cartRepository.findById(firstCartItem.getCart().getCart_id())
      .orElseThrow(() -> new EntityNotFoundException("Cart not found with ID: " + firstCartItem.getCart().getCart_id()));

    if (firstCartItem == null || firstCart.getUser() == null) {
      return ResponseEntity.badRequest()
        .body("Invalid cart item or user associated with cart not found.");
    }

    Integer userId = firstCart.getUser().getId();
    // Integer userId = cartItemIds;
    CustomerOrder customerOrder = new CustomerOrder();
    customerOrder.setUserId(userId);
    customerOrder.setProductId(productId);
    customerOrder.setQuantity(quantity.intValue()); // Ensure correct type
    customerOrder.setOrderAmount(
        BigDecimal.valueOf(productPrice)); // Example: Use the product's price
    customerOrder.setOrderStatus("order_placed"); // Set initial status

    saveCustomerOrder(customerOrder); // Save using the service

    System.out.println("Customer order created for productId: " + productId);
    return new ResponseEntity<String>(HttpStatus.OK);
  }

  private ProductModel getProductDetails(Integer productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
  }

  // NOTE: GET
  @Transactional
  public Page<CustomerOrderDTO> getCustomerOrderDto(
      Integer userId, String sortBy, String sortDirection, Pageable pageable) {
    Page<CustomerOrder> customerOrderPage =
        getCustomerOrder(userId, sortBy, sortDirection, pageable);

    List<CustomerOrderDTO> customerOrderDtoList =
        customerOrderPage.getContent().stream()
            .map(
                customerOrder -> {
                  ProductModel product = getProductDetails(customerOrder.getProductId());

                  CustomerOrderDTO dto = new CustomerOrderDTO();
                  dto.setUserId(customerOrder.getUserId());
                  dto.setProductId(customerOrder.getProductId());
                  dto.setQuantity(customerOrder.getQuantity());
                  dto.setOrderAmount(customerOrder.getOrderAmount());
                  dto.setOrderStatus(customerOrder.getOrderStatus());
                  dto.setProductName(product.getProductName());
                  dto.setCategory(product.getCategory().getCategoryName());
                  dto.setSeries(product.getSeries().getSeriesName());
                  return dto;
                })
            .collect(Collectors.toList());

    return new PageImpl<>(
        customerOrderDtoList, customerOrderPage.getPageable(), customerOrderPage.getTotalElements());
  }

  // NOTE: Update
  @Transactional
  public void updateCustomerOrderStatus(Integer userId, Integer productId, String status) {
    CustomerOrder order = customerOrderRepository.findById(new CustomerOrderId(userId, productId)).orElse(null);

    if (order != null) {
      order.setOrderStatus(status);
      customerOrderRepository.save(order);
    } else {
      // Handle the case where the order was not found
      throw new EntityNotFoundException(
          "Order not found for userId: " + userId + " and productId: " + productId); // Or handle however you want
    }
  }
}
