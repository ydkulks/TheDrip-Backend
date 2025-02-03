package dev.ydkulks.TheDrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
// import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
// import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SpringBootApplication
public class TheDripApplication {

  private static final Logger logger = LoggerFactory.getLogger(TheDripApplication.class);
  private static S3AsyncClient s3AsyncClient;

  public static S3AsyncClient getAsyncClient() {
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

  public CompletableFuture<PutObjectResponse> uploadLocalFileAsync(String bucketName, String key, String objectPath) {
    PutObjectRequest objectRequest = PutObjectRequest.builder()
      .bucket(bucketName)
      .key(key)
      .build();

    CompletableFuture<PutObjectResponse> response = getAsyncClient().putObject(objectRequest,
        AsyncRequestBody.fromFile(Paths.get(objectPath)));
    return response.whenComplete((resp, ex) -> {
      if (ex != null) {
        throw new RuntimeException("Failed to upload file", ex);
      }
    });
  }

  /**
   * Lists all the S3 buckets associated with the provided AWS S3 client.
   *
   * @param s3 the S3Client instance used to interact with the AWS S3 service
   */
  public static void listBuckets(S3Client s3) {
    try {
      ListBucketsResponse response = s3.listBuckets();
      List<Bucket> bucketList = response.buckets();
      bucketList.forEach(bucket -> {
        System.out.println("Bucket Name: " + bucket.name());
      });

    } catch (S3Exception e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }

  public String createPresignedGetUrl(String bucketName, String keyName) {
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
            logger.info("Presigned URL: [{}]", presignedRequest.url().toString());
            logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        }
    }

  public void close() {
    s3AsyncClient.close();
  }

  public static void main(String[] args) {
    SpringApplication.run(TheDripApplication.class, args);

    // NOTE: Upload Image
    TheDripApplication myS3Modules = new TheDripApplication();
    String bucketName = "thedrip";
    String key = "product_1_img_2.webp";
    String localPath = "/home/yd/Downloads/product_1_img_2.webp";

    /*
    uploader.uploadLocalFileAsync(bucketName, key, localPath)
      .thenRun(() -> System.out.println("Upload completed successfully!"))
      .exceptionally(ex -> {
        System.err.println("Upload failed: " + ex.getMessage());
        return null;
      });
    */

    // NOTE: List buckets
    // S3Client s3 = S3Client.builder()
    //   .region(Region.AP_SOUTH_1)
    //   .build();
    // listBuckets(s3);

    // NOTE: Get presigned URL
    String presignedUrl = myS3Modules.createPresignedGetUrl(bucketName, key);
    System.out.println("Generated presigned URL: " + presignedUrl);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // WARN: Change front-end URL in prod
        registry.addMapping("/**").allowedOrigins("http://localhost:5173");
      }
    };
  }

}
