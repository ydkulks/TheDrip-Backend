package dev.ydkulks.TheDrip.controllers.seller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import dev.ydkulks.TheDrip.repos.ProductRepository;
import dev.ydkulks.TheDrip.services.ProductImageService;
import dev.ydkulks.TheDrip.services.ProductService;
import jakarta.transaction.Transactional;
import dev.ydkulks.TheDrip.models.ProductCreationDTO;
import dev.ydkulks.TheDrip.models.ProductModel;
import dev.ydkulks.TheDrip.models.ProductImageModel;
import dev.ydkulks.TheDrip.models.ProductProductImageModel;
import dev.ydkulks.TheDrip.models.ProductSeriesModel;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/seller")
public class SellerController {

  @Autowired private ProductImageRepository productImageRepository;
  @Autowired private ProductImageService productImageService;
  @Autowired private ProductProductImageRepository productProductImageRepository;
  @Autowired private ProductService productService;
  @Autowired private ProductRepository productRepository;

  // NOTE: Create/Update product details
  @PostMapping("/product")
  // public ResponseEntity<ProductCreationModel> createProduct(@RequestBody ProductCreationDTO data) {
  public ResponseEntity<String> createProduct(@RequestBody ProductCreationDTO data) {
    try {
      // System.out.println("ProductName: "+data.getProductName());
      // System.out.println("CategoryId: "+data.getCategoryId());
      // System.out.println("UserId: "+data.getUserId());
      // System.out.println("UserId: "+data.getSeriesId());
      // System.out.println("Price: "+data.getProductPrice());
      // System.out.println("Description: "+data.getProductDescription());
      // System.out.println("SizeId: "+ data.getProductSizes());
      // System.out.println("ColorId: "+ data.getProductColors());

      ProductModel product = productService.createOrUpdateProduct(
        data.getProductName(),
        data.getCategoryId(),
        data.getUserId(),
        data.getSeriesId(),
        data.getProductPrice(),
        data.getProductDescription(),
        data.getProductStock(),
        data.getProductSold(),
        data.getProductSizes(),
        data.getProductColors()
      );
      return new ResponseEntity<>(product.getProductName(), HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/{username}/{productId}/image")
  @Transactional
  public ResponseEntity<?> uploadImage(
      @PathVariable String username,
      @PathVariable String productId,
      @RequestParam("file") List<MultipartFile> files
      ) throws IOException {
    try{
      // Does the product exists?
      boolean product = productRepository.existsById(Integer.parseInt(productId));
      long imgCount = productProductImageRepository.countByProductId(Integer.parseInt(productId));
      if (imgCount > 5) {
        return new ResponseEntity<>(
            "Maximum number of images (" + 5 + ") reached for product " + productId,
            HttpStatus.NOT_ACCEPTABLE);
      }
      if (product) {
        // Upload image to S3
        List<CompletableFuture<PutObjectResponse>> responses = productImageService
          .uploadMultipleFilesAsync("thedrip", username, productId, files);
        CompletableFuture.allOf(responses.toArray(new CompletableFuture[0])).join();
      }

      // Store in product_images and product_product_images
      files.forEach(file -> {
        String imgPath = String.format("%s/%s/%s", username, productId, file.getOriginalFilename());
        // Check if image exists for that product
        Optional<ProductImageModel> img = productImageRepository.findByImgPath(imgPath);
        ProductImageModel image;
        ProductProductImageModel productImageLink;
        if (img.isEmpty()) {
          image = new ProductImageModel();
          image.setImg_name(file.getOriginalFilename());
          image.setImg_type(file.getContentType());
          image.setImgPath(String.format("%s/%s/%s", username, productId, file.getOriginalFilename()));
          ProductImageModel savedImage = productImageRepository.save(image);

          int imgId = savedImage.getImg_id();

          productImageLink = new ProductProductImageModel();
          productImageLink.setProductId(Integer.parseInt(productId));
          productImageLink.setImg_id(imgId);
          productProductImageRepository.save(productImageLink);
        } else {
          image = img.get();
          // Update fields if needed, for example:
          image.setImg_name(file.getOriginalFilename());
          image.setImg_type(file.getContentType());

          productImageRepository.save(image); // Save the updated image
        }
      });
      return new ResponseEntity<>("All files uploaded", HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

  }

  @PostMapping("/series")
  @Transactional
  public ResponseEntity<?> createOrUpdateSeries(@RequestParam String seriesName) {
    try{
      ProductSeriesModel series = productService.createOrUpdateSeries(seriesName);
      return new ResponseEntity<>(series, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
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

  @DeleteMapping("/{username}/{productId}/{image}")
  public ResponseEntity<?> deleteImages(@PathVariable String username, @PathVariable Integer productId, @PathVariable String image) {
    String imgPath = username + "/" + productId + "/" + image;
    ProductImageModel imageRecord = productImageRepository.findByImgPath(imgPath).orElse(null);
    if (imageRecord == null) {
      return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
    }
    productImageService.deleteImageForProduct("thedrip", username, productId, image);
    return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
  }

  @DeleteMapping("/{username}/{productId}/image")
  public ResponseEntity<?> deleteImages(@PathVariable String username, @PathVariable Integer productId) {
    String imgPath = "/" + username + "/" + productId + "/";
    ProductImageModel imageRecord = productImageRepository.findByImgPath(imgPath).orElse(null);
    if (imageRecord == null) {
      return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
    }
    productImageService.deleteImagesForProduct("thedrip", username, productId);
    return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
  }

  @DeleteMapping("/{username}/product/image")
  public ResponseEntity<?> deleteImages(@PathVariable String username) {
    String imgPath = "/" + username + "/";
    ProductImageModel imageRecord = productImageRepository.findByImgPath(imgPath).orElse(null);
    if (imageRecord == null) {
      return new ResponseEntity<>(imgPath, HttpStatus.NOT_FOUND);
    }
    productImageService.deleteImagesForSeller("thedrip", username);
    return new ResponseEntity<>("Deleted: " + imgPath, HttpStatus.OK);
  }

  // TODO: Move this to ADMIN Controller
  @DeleteMapping("/all/product/image")
  public ResponseEntity<?> deleteImages() {
    productImageService.deleteAllImages("thedrip");
    return new ResponseEntity<>("Deleted: All images of all products", HttpStatus.OK);
  }

  @DeleteMapping("/product")
  public ResponseEntity<?> deleteProductById(@RequestParam(required = true) Integer productId) {
    try {
      productService.deleteProduct(productId);
      return new ResponseEntity<>(productId, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("Invalid arguments provided.", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
