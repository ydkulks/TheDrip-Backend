package dev.ydkulks.TheDrip.controllers.customer;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.CartItemsDTO;
import dev.ydkulks.TheDrip.models.CartItemsModel;
import dev.ydkulks.TheDrip.models.CartMapper;
import dev.ydkulks.TheDrip.models.CartPageDTO;
import dev.ydkulks.TheDrip.models.CartProductDTO;
import dev.ydkulks.TheDrip.models.CartResponseDTO;
import dev.ydkulks.TheDrip.models.CartResponseWithTotalDTO;
import dev.ydkulks.TheDrip.models.CustomerOrder;
import dev.ydkulks.TheDrip.models.CustomerOrderDTO;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserReviewsDTO;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.repos.ProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductSizesRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.repos.UserReviewsRepository;
import dev.ydkulks.TheDrip.services.CartService;
import dev.ydkulks.TheDrip.services.CustomerOrderService;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.services.UserReviewsService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
  @Autowired private UserReviewsService userReviewsService;
  @Autowired private UserReviewsRepository userReviewsRepository;
  @Autowired private CartService cartService;
  @Autowired private CartMapper cartMapper;
  @Autowired private ProductImageService productImageService;
  @Autowired private ProductColorsRepository productColorsRepository;
  @Autowired private ProductSizesRepository productSizesRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private UserRepo userRepo;
  @Autowired CustomerOrderService customerOrderService;

  @GetMapping("/review")
public ResponseEntity<?> getReview(
    @RequestParam Integer userId,
    @RequestParam(required = false) Integer productId,
    @RequestParam(required = false) String sortBy,
    @RequestParam(required = false) String sortDirection,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
  try {
    Pageable pageable = PageRequest.of(page, size);

    UserModel user = (userId != null) ? userRepo.findById(userId).orElse(null) : null;
    ProductModel product =
        (productId != null) ? productRepository.findById(productId).orElse(null) : null;

    Page<UserReviewsModel> response =
        userReviewsService.getReview(user, product, sortBy, sortDirection, pageable);
    List<UserReviewsDTO.MyReviewsDTO> reviewDTOs =
        response.getContent().stream()
            .map(
                review ->
                    convertToMyReviewsDTO(
                        review, productRepository)) // Use a method to map each UserReviewsModel
            // to UserReviewsDTO.MyReviewsDTO
            .collect(Collectors.toList());

    Page<UserReviewsDTO.MyReviewsDTO> pagesOfMyReviewDTO =
        new PageImpl<>(reviewDTOs, pageable, response.getTotalElements());
    return new ResponseEntity<>(pagesOfMyReviewDTO, HttpStatus.OK);
  } catch (IllegalArgumentException e) {
    return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
  } catch (Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

private UserReviewsDTO.MyReviewsDTO convertToMyReviewsDTO(
    UserReviewsModel review, ProductRepository productRepository) {
  UserReviewsDTO.MyReviewsDTO dto = new UserReviewsDTO.MyReviewsDTO();
  dto.setUserId(review.getUser().getId());
  dto.setUserName(review.getUser().getUsername());

  ProductModel product = productRepository.findById(review.getProduct().getProductId()).orElse(null);
  if (product != null) {
    dto.setProductId(product.getProductId());
    dto.setProductName(product.getProductName());
  } else {
    dto.setProductId(null);
    dto.setProductName("Product Not Found");
  }

  dto.setReview_title(review.getReviewTitle());
  dto.setReview_text(review.getReviewText());
  dto.setRating(review.getRating());
  dto.setCreated(review.getCreated());
  dto.setUpdated(review.getUpdated());
  return dto;
}


  @PostMapping("/review")
  public ResponseEntity<?> createOrUpdate(@RequestBody UserReviewsDTO data) {
    try {
      UserReviewsModel response = userReviewsService.createOrUpdateReview(
          data.getUserId(),
          data.getProduct(),
          data.getReview_title(),
          data.getReview_text(),
          data.getRating());
      UserReviewsDTO responseDTO = new UserReviewsDTO();
      if (response != null) {
        responseDTO.setUserId(data.getUserId());
        responseDTO.setUserName(data.getUserName());
        responseDTO.setProduct(data.getProduct());
        responseDTO.setReview_title(data.getReview_title());
        responseDTO.setReview_text(data.getReview_text());
        responseDTO.setRating(data.getRating());
      }
      return new ResponseEntity<UserReviewsDTO>(responseDTO, HttpStatus.OK);

    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/review")
  public ResponseEntity<String> delete(@RequestParam Integer reviewId) {
    try {
      UserReviewsModel response = userReviewsRepository.findById(reviewId).orElse(null);
      if (response != null) {
        userReviewsRepository.deleteById(reviewId);
        return new ResponseEntity<String>(response.getReviewTitle() + " got deleted!", HttpStatus.OK);
      }
      return new ResponseEntity<String>("Review not found!", HttpStatus.NOT_FOUND);

    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/items") // More specific endpoint for adding items
  public ResponseEntity<?> addToCart(
      @RequestParam Integer userId,
      @RequestParam Integer productId,
      @RequestParam Integer quantity,
      @RequestParam Integer color,
      @RequestParam Integer size) {
    try {

      ProductColorsModel colorRecord = productColorsRepository.findById(color)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + color));
      String colorName = colorRecord.getColor_name();

      ProductSizesModel sizeRecord = productSizesRepository.findById(size)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + size));
      String sizeName = sizeRecord.getSize_name();

      cartService.createNewCartItem(userId, productId, quantity, colorName, sizeName);

      return new ResponseEntity<>("Item added to cart successfully!", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/items") // More specific endpoint for adding items
  public ResponseEntity<?> updateCartItem(
      @RequestParam Integer userId,
      @RequestParam Integer productId,
      @RequestParam Integer quantity,
      @RequestParam Integer color,
      @RequestParam Integer size) {
    try {

      ProductColorsModel colorRecord = productColorsRepository.findById(color)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + color));
      String colorName = colorRecord.getColor_name();

      ProductSizesModel sizeRecord = productSizesRepository.findById(size)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Color ID: " + size));
      String sizeName = sizeRecord.getSize_name();

      cartService.updateCartItemQuantity(userId, productId, quantity, colorName, sizeName);

      return new ResponseEntity<>("Item updated successfully!", HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/items")
  public ResponseEntity<?> getCartItems(
      @RequestParam Integer userId,
      @RequestParam(required = false) String productName,
      @RequestParam(required = false) Integer colorId,
      @RequestParam(required = false) Integer sizeId,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);

      CartResponseWithTotalDTO itemsObj = cartService.getItems(
          userId,
          productName,
          sizeId,
          colorId,
          sortBy,
          sortDirection,
          pageable
        );
      Page<CartItemsModel> cartItemsPage = itemsObj.getCartItemsPage();
      double total = itemsObj.getTotal();

      List<CartItemsDTO> cartItemDTOs = cartItemsPage.getContent().stream()
        .map(cartItemModel -> {
          CartItemsDTO cartItemDTO = cartMapper.cartItemsModelToCartItemDTO(cartItemModel);
          CartProductDTO cartProductDTO = cartItemDTO.getProduct();

          // Get the first image path from the Set<ProductImageModel>
          Set<ProductImageModel> images = cartItemModel.getProduct().getImages();
          String firstImagePath = null;
          if (images != null && !images.isEmpty()) {
            Iterator<ProductImageModel> iterator = images.iterator();
            if (iterator.hasNext()) {
              ProductImageModel firstImage = iterator.next();
              firstImagePath = firstImage.getImgPath();
            }
          }

          // Generate the presigned URL for the first image
          String imageUrl = null;
          if (firstImagePath != null) {
            imageUrl = productImageService.getPresignedImageURL("thedrip", firstImagePath);
          }

          // Set the image URL in the CartProductDTO
          cartProductDTO.setImage(imageUrl);
          return cartItemDTO;
        })
      .collect(Collectors.toList());

      // Create the PageDTO
      CartPageDTO pageDTO = new CartPageDTO();
      pageDTO.setSize(cartItemsPage.getSize());
      pageDTO.setNumber(cartItemsPage.getNumber());
      pageDTO.setTotalElements(cartItemsPage.getTotalElements());
      pageDTO.setTotalPages(cartItemsPage.getTotalPages());

      // Create the final response object
      CartResponseDTO response = new CartResponseDTO();
      response.setContent(cartItemDTOs);
      response.setPage(pageDTO);
      response.setTotal(total);

      return new ResponseEntity<CartResponseDTO>(response, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/item")
  public ResponseEntity<?> removeFromCart(@RequestParam Integer cartItemId) {
    try{
      cartService.removeFromCart(cartItemId);
      return new ResponseEntity<>("Removed cart item of ID: " + cartItemId, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/myorders/{userId}")
  public ResponseEntity<?> getMyOrders(@PathVariable Integer userId,
    @RequestParam(required = false) String sortBy,
    @RequestParam(required = false) String sortDirection,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    try{
      Pageable pageable = PageRequest.of(page, size);
      Page<CustomerOrderDTO> response = customerOrderService.getCustomerOrderDto(userId, sortBy, sortDirection, pageable);
      return new ResponseEntity<Page<CustomerOrderDTO>>(response, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
