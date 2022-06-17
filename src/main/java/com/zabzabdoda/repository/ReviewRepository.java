package com.zabzabdoda.repository;

import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByUser_id(int userId);

    List<Review> findByMovie_movieId(int movieId);

    @Query(value = "SELECT DISTINCT e.movie.id, COUNT(e.movie.id) \n" +
            "FROM Review e \n" +
            "GROUP BY e.movie.id \n" +
            "ORDER BY COUNT(e.movie.id) DESC")
    List<Integer> findMostPopular();



    Review findByReviewId(int id);
}