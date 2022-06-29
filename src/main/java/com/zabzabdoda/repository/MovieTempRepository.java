package com.zabzabdoda.repository;

import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.MovieTemp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieTempRepository extends JpaRepository<MovieTemp,Integer> {

    MovieTemp findByMovieId(int movie_id);

}
