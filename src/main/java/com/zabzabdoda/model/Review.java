package com.zabzabdoda.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity{

    @Id
    @Column(name = "review_id")
    private int reviewId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private String reviewText;

    @Min(1)
    @Max(5)
    private int starRating;

    @Column(name = "is_critic_review")
    private boolean isCriticReview;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

}
