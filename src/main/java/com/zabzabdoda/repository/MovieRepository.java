package com.zabzabdoda.repository;

import com.zabzabdoda.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Integer> {

    Movie findByMovieId(int movie_id);



    Page<Movie> findBymovieNameContainingIgnoreCase(String title, Pageable pageable);

}
