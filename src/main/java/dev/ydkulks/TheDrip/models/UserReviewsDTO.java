package dev.ydkulks.TheDrip.models;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReviewsDTO {
    private Integer userId;
    private String userName;
    private Integer product;
    private String review_title;
    private String review_text;
    private Integer rating;
    private Timestamp created;
    private Timestamp updated;
}
