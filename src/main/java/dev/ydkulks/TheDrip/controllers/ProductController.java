package dev.ydkulks.TheDrip.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.CategorySeriesSizesColorsDTO;
import dev.ydkulks.TheDrip.models.ListProductResponseDTO;
import dev.ydkulks.TheDrip.models.ProductCategoriesModel;
import dev.ydkulks.TheDrip.models.ProductColorsModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductResponseDTO;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import dev.ydkulks.TheDrip.models.ProductSizesModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.models.UserReviewsDTO;
import dev.ydkulks.TheDrip.models.UserReviewsModel;
import dev.ydkulks.TheDrip.repos.ProductCategoriesRepository;
import dev.ydkulks.TheDrip.repos.ProductColorsRepository;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductSeriesRepository;
import dev.ydkulks.TheDrip.repos.ProductSizesRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.services.ProductService;
import dev.ydkulks.TheDrip.services.UserReviewsService;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired private ProductService productService;
  @Autowired private ProductCategoriesRepository productCategoriesRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductSeriesRepository productSeriesRepository;
  @Autowired private UserReviewsService userReviewsService;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductColorsRepository productColorsRepository;
  @Autowired private ProductSizesRepository productSizesRepository;

  // NOTE: Get product by ID
  @GetMapping("/product")
  public ResponseEntity<ProductResponseDTO> product(@RequestParam(defaultValue = "1") int id) {
    // System.out.println("ProdId: " + id);
    ProductResponseDTO response = productService.getProductDetails(id);
    if (response != null) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/productsbyid")
  public ResponseEntity<?> productsByIds(@RequestParam List<Integer> id, @PageableDefault(size=10,page=0) Pageable pageable) {
    // System.out.println("ProdId: " + id);
    Page<ProductResponseDTO> response = productService.getProductByIds(id, pageable);
    if (response != null) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/productsallbyid")
  public ResponseEntity<?> productsAllByIds(@RequestParam List<Integer> id) {
    // System.out.println("ProdId: " + id);
    List<ListProductResponseDTO> response = productService.getAllProductByIdsNoImg(id);
    if (response != null) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/products")
  public ResponseEntity<?> allProducts(
      @RequestParam(required = false) List<Integer> colorIds,
      @RequestParam(required = false) List<Integer> sizeIds,
      @RequestParam(required = false) Boolean inStock,
      @RequestParam(required = false) List<Integer> categoryIds,
      @RequestParam(required = false) Integer userId,
      @RequestParam(required = false) List<Integer> seriesIds,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection,
      @RequestParam(required = false) String searchTerm,
      @RequestParam Integer imgCount,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);

      // Fetch the entities based on the IDs
      List<ProductColorsModel> colors =
        (colorIds != null && !colorIds.isEmpty())
            ? productColorsRepository.findAllById(colorIds)
            : null;
      List<ProductSizesModel> sizes =
        (sizeIds != null && !sizeIds.isEmpty())
            ? productSizesRepository.findAllById(sizeIds)
            : null;
      List<ProductCategoriesModel> categories =
        (categoryIds != null && !categoryIds.isEmpty())
            ? productCategoriesRepository.findAllById(categoryIds)
            : null;
      UserModel user = (userId != null) ? userRepo.findById(userId).orElse(null) : null;
      List<ProductSeriesModel> series =
        (seriesIds != null && !seriesIds.isEmpty())
            ? productSeriesRepository.findAllById(seriesIds)
            : null;

      // Call the getProductByFilters service
      Page<ProductResponseDTO> products =
          productService.getProductsByFilters(
              colors, sizes, inStock, categories, user, series, minPrice, maxPrice, sortBy, sortDirection, searchTerm, imgCount, pageable);

      return new ResponseEntity<Page<ProductResponseDTO>>(products, HttpStatus.OK);

    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Mapping method: UserReviewsModel -> UserReviewsDTO
  private UserReviewsDTO convertToDto(UserReviewsModel review) {
    UserReviewsDTO dto = new UserReviewsDTO();
    dto.setUserId(review.getUser().getId());
    dto.setUserName(review.getUser().getUsername());
    dto.setProduct(review.getProduct().getProductId());
    dto.setReview_title(review.getReviewTitle());
    dto.setReview_text(review.getReviewText());
    dto.setRating(review.getRating());
    dto.setCreated(review.getCreated());
    dto.setUpdated(review.getUpdated());
    return dto;
  }

  @GetMapping("/reviews/{productId}")
  public ResponseEntity<?> getReviews(
      @PathVariable Integer productId,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortDirection,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      ProductModel product = productRepository.findById(productId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID: " + productId));

      Page<UserReviewsModel> response = userReviewsService.getProductReviews(product, sortBy, sortDirection, pageable);

      List<UserReviewsDTO> reviewDTOs = response.getContent().stream()
        .map(this::convertToDto) // Use a method to map each UserReviewsModel to UserReviewsDTO
        .collect(Collectors.toList());

      Page<UserReviewsDTO> pagesOfReviewDTO = new PageImpl<>(reviewDTOs, pageable, response.getTotalElements());

      return new ResponseEntity<Page<UserReviewsDTO>>(pagesOfReviewDTO, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(
          "Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(
          "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/productspecifications")
  public ResponseEntity<?> getProdSpecs() {
    try {
      CategorySeriesSizesColorsDTO response = productService.getCategorySeriesSizesColors();
      return new ResponseEntity<CategorySeriesSizesColorsDTO>(response, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
