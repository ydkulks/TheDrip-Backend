package dev.ydkulks.TheDrip.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.ProductCreationDTO;
import dev.ydkulks.TheDrip.models.ProductCreationModel;
import dev.ydkulks.TheDrip.repos.ProductResponseDTO;
import dev.ydkulks.TheDrip.services.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired
  private ProductService productService;

  // NOTE: test controller
  @PostMapping("/test")
  public ResponseEntity<String> test(@RequestBody ProductCreationDTO data) {
    try {
      ProductCreationModel product = productService.createOrUpdateProduct(
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
  public CompletableFuture<List<ProductResponseDTO>> allProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    // System.out.println("Got request to api/products");
    // System.out.println("Page & Size [controller]: " + page + " " + size);
    CompletableFuture<List<ProductResponseDTO>> products = productService.getAllProductDetails(page, size);
    // System.out.println("Products :" + products);
    return products;
  }

}
