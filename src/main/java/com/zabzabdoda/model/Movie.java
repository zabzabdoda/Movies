package com.zabzabdoda.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "movies2")
public class Movie extends BaseEntity{

    @Id
    @Column(name = "id")
    private int movieId;

    @Column(name = "title")
    private String movieName;

    @Column(name = "poster_path")
    private String posterUrl;

    private String budget;

    private String homepage;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "original_language")
    private String originalLanguage;

    @Column(name = "original_title")
    private String originalTitle;

    private String overview;

    private double popularity;

    @Column(name = "release_date")
    private String releaseDate;

    private long revenue;

    private int runtime;

    private String tagline;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new LinkedHashSet<>();


    public float getRating(){
        float f = 0;
        for(Review review : reviews){
            f += review.getStarRating();
        }
        return f/reviews.size();
    }

}
