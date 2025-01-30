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

import java.nio.file.Paths;
import java.time.Duration;
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

    CompletableFuture<PutObjectResponse> response = getAsyncClient().putObject(objectRequest, AsyncRequestBody.fromFile(Paths.get(objectPath)));
    return response.whenComplete((resp, ex) -> {
      if (ex != null) {
        throw new RuntimeException("Failed to upload file", ex);
      }
    });
  }

  public void close() {
    s3AsyncClient.close();
  }

  public static void main(String[] args) {
    SpringApplication.run(TheDripApplication.class, args);

    TheDripApplication uploader = new TheDripApplication();
    String bucketName = "thedrip";
    String key = "product_1_img_2.webp";
    String localPath = "/home/yd/Downloads/product_1_img_2.webp";

    uploader.uploadLocalFileAsync(bucketName, key, localPath)
      .thenRun(() -> System.out.println("Upload completed successfully!"))
      .exceptionally(ex -> {
        System.err.println("Upload failed: " + ex.getMessage());
        return null;
      });
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
