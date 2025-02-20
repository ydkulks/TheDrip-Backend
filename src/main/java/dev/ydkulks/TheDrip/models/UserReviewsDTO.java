package dev.ydkulks.TheDrip.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReviewsDTO {
    private Integer user;
    private Integer product;
    private String review_title;
    private String review_text;
    private Integer rating;
}
