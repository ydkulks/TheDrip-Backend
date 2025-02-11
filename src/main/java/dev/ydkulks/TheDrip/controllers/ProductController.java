package dev.ydkulks.TheDrip.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ydkulks.TheDrip.models.ProductModel;
// import dev.ydkulks.TheDrip.repos.ProductProjectionDTO;
import dev.ydkulks.TheDrip.repos.ProductResponseDTO;
import dev.ydkulks.TheDrip.services.ProductService;

@RestController
@RequestMapping("/api")
public class ProductController {

  @Autowired
  private ProductService productService;

  @GetMapping("/product")
  public ResponseEntity<ProductModel> product() {
    return productService.getProductDetails()
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @GetMapping("/products")
  public CompletableFuture<List<ProductResponseDTO>> allProducts() {
    // System.out.println("Got request to api/products");
    CompletableFuture<List<ProductResponseDTO>> products = productService.getAllProductDetails();
    // System.out.println("Products :" + products);
    return products;
  }

}
