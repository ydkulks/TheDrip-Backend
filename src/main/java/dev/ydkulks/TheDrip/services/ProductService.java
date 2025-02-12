package dev.ydkulks.TheDrip.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.repos.ProductProjectionDTO;
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.repos.ProductResponseDTO;


@Service
public class ProductService {
  @Autowired
  ProductRepository repo;

  @Autowired
  ProductImageService productImageService;

  public Optional<ProductModel> getProductDetails() {
    Optional<ProductModel> product = repo.findById(1);
    // List<ProductModel> product = repo.findAll();
    return product;
  }

  // TODO: Paginate and sort all products
  public CompletableFuture<List<ProductResponseDTO>> getAllProductDetails(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ProductProjectionDTO> products = repo.getAllProducts(pageable);

    List<CompletableFuture<ProductResponseDTO>> futures = products.getContent().stream()
      .map(product -> {
        List<String> s3Paths = product.getImages(); // Get all stored paths
        // System.out.println("S3paths: " + s3Paths);

        // Fetch pre-signed URLs for each path
        List<CompletableFuture<List<String>>> urlFutures = s3Paths.stream()
          .map(path -> productImageService.getPresignedImageURLs("thedrip", path))
          .collect(Collectors.toList());

        return CompletableFuture.allOf(urlFutures.toArray(new CompletableFuture[0]))
          .thenApply(v -> {
            List<List<String>> urlLists = urlFutures.stream()
              .map(CompletableFuture::join)
              .collect(Collectors.toList());
            // System.out.println("Completed URLs: " + urlLists);
            return urlLists.stream().flatMap(List::stream).collect(Collectors.toList());
          })
        .thenApply(imageUrls -> new ProductResponseDTO(product, imageUrls));

      })
    .collect(Collectors.toList());

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
    .thenApply(v -> {
        List<ProductResponseDTO> productResponseList = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        // System.out.println("Final Product Response: " + productResponseList);
        return productResponseList;
    });
  }
}
