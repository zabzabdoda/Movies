package com.zabzabdoda.repository;

import com.zabzabdoda.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Integer> {

    Movie findByMovieId(int movie_id);

    List<Movie> findBymovieNameContainingIgnoreCase(String title, Pageable pageable);

    //credit: PDStat stackoverflow https://stackoverflow.com/questions/70649647/jpql-query-sort-by-average-rating-then-box-office-value-for-movie
    @Query("SELECT m FROM Movie m LEFT JOIN m.reviews r GROUP BY m ORDER BY AVG(r.starRating) desc")
    List<Movie> findTopmoviesByRating(Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN m.reviews r GROUP BY m HAVING AVG(r.starRating) > 0 ORDER BY AVG(r.starRating)")
    List<Movie> findLowestmoviesByRating(Pageable pageable);


}
