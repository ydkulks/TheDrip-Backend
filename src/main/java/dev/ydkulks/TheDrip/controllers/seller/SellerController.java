package dev.ydkulks.TheDrip.controllers.seller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.ydkulks.TheDrip.repos.ProductImageRepository;
import dev.ydkulks.TheDrip.repos.ProductProductImageRepository;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.services.ProductService;
import jakarta.transaction.Transactional;
import dev.ydkulks.TheDrip.models.ProductCreationDTO;
import dev.ydkulks.TheDrip.models.ProductCreationModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductImageModel;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/seller")
public class SellerController {

  @Autowired private ProductImageRepository productImageRepository;
  @Autowired private ProductImageService productImageService;
  @Autowired private ProductProductImageRepository productProductImageRepository;
  @Autowired private ProductService productService;

  // NOTE: Create/Update product details
  @PostMapping("/product")
  public ResponseEntity<ProductCreationModel> createProduct(@RequestBody ProductCreationDTO data) {
    try {
      // System.out.println("ProductName: "+data.getProductName());
      // System.out.println("CategoryId: "+data.getCategoryId());
      // System.out.println("UserId: "+data.getUserId());
      // System.out.println("UserId: "+data.getSeriesId());
      // System.out.println("Price: "+data.getProductPrice());
      // System.out.println("Description: "+data.getProductDescription());
      // System.out.println("SizeId: "+ data.getProductSizes());
      // System.out.println("ColorId: "+ data.getProductColors());

      ProductCreationModel product = productService.createProduct(
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
      return new ResponseEntity<>(product, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/{username}/{productId}/image")
  @Transactional
  public ResponseEntity<String> uploadImage(@PathVariable String username, @PathVariable String productId, @RequestParam("file") List<MultipartFile> files) throws IOException {
    // Upload image to S3
    List<CompletableFuture<PutObjectResponse>> responses = productImageService.uploadMultipleFilesAsync("thedrip", username, productId, files);
    CompletableFuture.allOf(responses.toArray(new CompletableFuture[0])).join();

    // Store in product_images and product_product_images
    files.forEach(file -> {
      ProductImageModel image = new ProductImageModel();
      image.setImg_name(file.getOriginalFilename());
      image.setImg_type(file.getContentType());
      image.setImg_path(String.format("%s/%s/%s", username, productId, file.getOriginalFilename()));
      ProductImageModel savedImage = productImageRepository.save(image);

      int imgId = savedImage.getImg_id();

      ProductProductImageModel productImageLink = new ProductProductImageModel();
      productImageLink.setProduct_id(Integer.parseInt(productId));
      productImageLink.setImg_id(imgId);
      productProductImageRepository.save(productImageLink);
    });

    return ResponseEntity.ok("All files uploaded");
  }

  // NOTE: Get presigned URL for 1 user ,1 of their product and 1 image
  @GetMapping("/{username}/{productId}/{img_name}")
  public String getImageLink(@PathVariable String username, @PathVariable String productId, @PathVariable String img_name) {
    String filePath = String.format("%s/%s/%s",username,productId,img_name);
    return productImageService.getPresignedImageURL("thedrip",filePath);
  }

  // NOTE: Get presigned URL for 1 user, 1 of their product's images
  @GetMapping("/{username}/{productId}/image")
  public List<String> getImageLink(@PathVariable String username, @PathVariable String productId) {
    return productImageService.getImagesForProduct("thedrip", username, productId).join();
  }

  // NOTE: Get presigned URL for 1 user and all of their product
  @GetMapping("/{username}/product/image")
  public List<String> getImageLink(@PathVariable String username) {
    return productImageService.getImagesForSeller("thedrip", username).join();
  }

  // NOTE: Get presigned URLs of all the products
  @GetMapping("/all/product/image")
  public List<String> getImageLink() {
    return productImageService.getAllImages("thedrip").join();
  }

  @DeleteMapping("/{username}/{productId}/image")
  public void deleteImages(@PathVariable String username, @PathVariable String productId) {
    productImageService.deleteImagesForProduct("thedrip", username, productId);
  }

  @DeleteMapping("/{username}/product/image")
  public void deleteImages(@PathVariable String username) {
    productImageService.deleteImagesForSeller("thedrip", username);
  }

  @DeleteMapping("/all/product/image")
  public void deleteImages() {
    productImageService.deleteAllImages("thedrip");
  }
}
