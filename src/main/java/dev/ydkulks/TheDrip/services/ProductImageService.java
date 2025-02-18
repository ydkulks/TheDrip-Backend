package dev.ydkulks.TheDrip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.ydkulks.TheDrip.repos.ProductImageRepository;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ProductImageService {
  @Autowired
  ProductImageRepository productImageRepository;

  private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class);
  private static S3AsyncClient s3AsyncClient;

  private static S3AsyncClient getAsyncClient() {
    if (s3AsyncClient == null) {
      SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
        .maxConcurrency(50)
        .connectionTimeout(Duration.ofSeconds(60))
        .readTimeout(Duration.ofSeconds(60))
        .writeTimeout(Duration.ofSeconds(60))
        .build();

      ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
        .apiCallTimeout(Duration.ofMinutes(2))
        .apiCallAttemptTimeout(Duration.ofSeconds(90))
        .retryPolicy(RetryMode.STANDARD)
        .build();

      s3AsyncClient = S3AsyncClient.builder()
        .region(Region.AP_SOUTH_1)
        .httpClient(httpClient)
        .overrideConfiguration(overrideConfig)
        .build();
    }
    return s3AsyncClient;
  }

  private void close() {
    s3AsyncClient.close();
  }

  // NOTE: Upload the images to S3 bucket
  public List<CompletableFuture<PutObjectResponse>> uploadMultipleFilesAsync(String bucketName, String username, String productId, List<MultipartFile> files) {
    return files.stream().map(file -> {
      try {
        String key = String.format("%s/%s/%s", username, productId, file.getOriginalFilename());

        PutObjectRequest objectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
          .build();

        AsyncRequestBody requestBody = AsyncRequestBody.fromBytes(file.getBytes());

        CompletableFuture<PutObjectResponse> response = getAsyncClient().putObject(objectRequest, requestBody);

        return response.whenComplete((resp, ex) -> {
          if (ex != null) {
            throw new RuntimeException("Failed to upload file: " + key, ex);
          } else {
            System.out.println(key);
          }
        });
      } catch (Exception e) {
        throw new RuntimeException("Error processing file", e);
      }
    }).collect(Collectors.toList());
  }

  // NOTE: Get the presigned url of the object for limited duration from S3 bucket
  private final Map<String, CachedPresignedUrl> presignedUrlCache =
      new ConcurrentHashMap<>();

  private static final Duration PRESIGNED_URL_DURATION = Duration.ofMinutes(10);
  private static final Duration REFRESH_THRESHOLD = Duration.ofMinutes(2);

  private static class CachedPresignedUrl {
    private final String url;
    private final Instant expiration;

    public CachedPresignedUrl(String url, Instant expiration) {
      this.url = url;
      this.expiration = expiration;
    }

    public String getUrl() {
      return url;
    }

    public Instant getExpiration() {
      return expiration;
    }

    public boolean isExpired(Duration refreshThreshold) {
      return Instant.now().plus(refreshThreshold).isAfter(expiration);
    }
  }

  private String generatePresignedUrl(String bucketName, String keyName) {
    try (S3Presigner presigner =
        S3Presigner.builder()
            .region(Region.AP_SOUTH_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()) {

      GetObjectRequest objectRequest =
          GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

      GetObjectPresignRequest presignRequest =
          GetObjectPresignRequest.builder()
              .signatureDuration(PRESIGNED_URL_DURATION)
              .getObjectRequest(objectRequest)
              .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toExternalForm();
    }
  }

  public String getPresignedImageURL(String bucketName, String keyName) {
    String cacheKey = bucketName + "/" + keyName;
    CachedPresignedUrl cachedUrl = presignedUrlCache.get(cacheKey);

    if (cachedUrl != null && !cachedUrl.isExpired(REFRESH_THRESHOLD)) {
      return cachedUrl.getUrl(); // Return cached URL if still valid
    }

    // Generate a new URL and update the cache
    String newUrl = generatePresignedUrl(bucketName, keyName);
    Instant expiration = Instant.now().plus(PRESIGNED_URL_DURATION);
    presignedUrlCache.put(cacheKey, new CachedPresignedUrl(newUrl, expiration));
    return newUrl;
  }

  public CompletableFuture<List<String>> getPresignedImageURLs(
      String bucket, String prefix) {
    ListObjectsV2Request listRequest =
        ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();

    return getAsyncClient()
        .listObjectsV2(listRequest)
        .thenApply(
            listResponse ->
                listResponse.contents().stream()
                    .map(S3Object::key)
                    .map(key -> getPresignedImageURL(bucket, key))
                    .collect(Collectors.toList()))
        .exceptionally(
            e -> {
              logger.error("Error fetching objects from S3", e);
              return List.of(); // Return empty list on failure
            });
  }
  /*
  public String getPresignedImageURL(String bucketName, String keyName) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.AP_SOUTH_1)
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build()) {

      GetObjectRequest objectRequest = GetObjectRequest.builder()
          .bucket(bucketName)
          .key(keyName)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(objectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      // logger.info("Presigned URL: [{}]", presignedRequest.url().toString());
      // logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

      return presignedRequest.url().toExternalForm();
    }
  }
  public CompletableFuture<List<String>> getPresignedImageURLs(String bucket, String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(prefix)
            .build();

        return getAsyncClient().listObjectsV2(listRequest)
            .thenApply(listResponse ->
                listResponse.contents().stream()
                    .map(S3Object::key)
                    // .map(this::getPresignedImageURL)  // Generate presigned URL for each key
                    .map(key -> getPresignedImageURL(bucket,key))
                    .collect(Collectors.toList())
            )
            .exceptionally(e -> {
                logger.error("Error fetching objects from S3", e);
                return List.of(); // Return empty list on failure
            });
    }
  */

    // Get all images for a specific product
    public CompletableFuture<List<String>> getImagesForProduct(String bucket, String sellerName, String productId) {
        return getPresignedImageURLs(bucket, sellerName + "/" + productId + "/");
    }

    // Get all images for a specific seller
    public CompletableFuture<List<String>> getImagesForSeller(String bucket, String sellerName) {
        return getPresignedImageURLs(bucket, sellerName + "/");
    }

    // Get all images for all sellers
    public CompletableFuture<List<String>> getAllImages(String bucket) {
        return getPresignedImageURLs(bucket, "");
    }

    // NOTE: Delete images from S3 bucket
    public CompletableFuture<Void> deleteMultipleImages(String bucket, String prefix) {
      ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
        .bucket(bucket)
        .prefix(prefix)
        .build();

      return getAsyncClient().listObjectsV2(listRequest)
        .thenCompose(listResponse -> {
          List<ObjectIdentifier> objectIdentifiers = listResponse.contents().stream()
            .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
            .collect(Collectors.toList());

          if (objectIdentifiers.isEmpty()) {
            logger.info("No objects found for prefix: {}", prefix);
            return CompletableFuture.completedFuture(null);
          }

          Delete deleteObjects = Delete.builder()
            .objects(objectIdentifiers)
            .build();

          DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(deleteObjects)
            .build();

          return getAsyncClient().deleteObjects(deleteRequest)
            .thenAccept(response -> logger.info("Successfully deleted {} objects under prefix: {}", objectIdentifiers.size(), prefix))
            .exceptionally(e -> {
              logger.error("Failed to delete objects from S3", e);
              return null;
            });
        });
    }

    public void deleteImagesForProduct(String bucket, String sellerName, String productName) {
        deleteMultipleImages(bucket, sellerName + "/" + productName + "/");
    }

    public void deleteImagesForSeller(String bucket, String sellerName) {
        deleteMultipleImages(bucket, sellerName + "/");
    }

    public void deleteAllImages(String bucket) {
        deleteMultipleImages(bucket, "");
    }
}
