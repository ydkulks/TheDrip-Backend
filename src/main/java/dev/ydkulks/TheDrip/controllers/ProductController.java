package dev.ydkulks.TheDrip.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.ProductCategoriesModel;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductResponseDTO;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import dev.ydkulks.TheDrip.models.UserModel;
import dev.ydkulks.TheDrip.repos.ProductCategoriesRepository;
import dev.ydkulks.TheDrip.repos.ProductSeriesRepository;
import dev.ydkulks.TheDrip.repos.UserRepo;
import dev.ydkulks.TheDrip.services.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired private ProductService productService;
  @Autowired private ProductCategoriesRepository productCategoriesRepository;
  @Autowired private UserRepo userRepo;
  @Autowired private ProductSeriesRepository productSeriesRepository;

  // NOTE: test controller
  @PostMapping("/test")
  public ResponseEntity<String> test(@RequestBody ProductCreationDTO data) {
    try {
      ProductModel product = productService.createOrUpdateProduct(
        data.getProductName(),
        data.getCategoryId(),
        data.getUserId(),
        data.getSeriesId(),
        data.getProductPrice(),
        data.getProductDescription(),
        data.getProductStock(),
        data.getProductSizes(),
        data.getProductColors()
      );
      return new ResponseEntity<>(product.getProductName(), HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Or handle the error more specifically
    }
  }

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

  @GetMapping("/products")
  public ResponseEntity<?> allProducts(
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) Integer userId,
      @RequestParam(required = false) Integer seriesId,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);

      // Fetch the entities based on the IDs
      ProductCategoriesModel category =
          (categoryId != null)
              ? productCategoriesRepository.findById(categoryId).orElse(null)
              : null;
      UserModel user = (userId != null) ? userRepo.findById(userId).orElse(null) : null;
      ProductSeriesModel series =
          (seriesId != null) ? productSeriesRepository.findById(seriesId).orElse(null) : null;

      // Call the getProductByFilters service
      Page<ProductResponseDTO> products =
          productService.getProductsByFilters(
              category, user, series, minPrice, maxPrice, pageable);

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

}
