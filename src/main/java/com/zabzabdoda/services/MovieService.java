package com.zabzabdoda.services;

import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.MovieTemp;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    public Movie mapMovie(MovieTemp movieTemp){
        Movie movie = new Movie();
        movie.setMovieName(movieTemp.getMovieName());
        movie.setPosterUrl(movieTemp.getPosterUrl());
        movie.setBudget(movieTemp.getBudget());
        movie.setHomepage(movieTemp.getHomepage());
        movie.setImdbId(movieTemp.getImdbId());
        movie.setOriginalLanguage(movieTemp.getOriginalLanguage());
        movie.setOriginalTitle(movieTemp.getOriginalTitle());
        movie.setOverview(movieTemp.getOverview());
        movie.setPopularity(movieTemp.getPopularity());
        movie.setReleaseDate(movieTemp.getReleaseDate());
        movie.setRevenue(movieTemp.getRevenue());
        movie.setRuntime(movieTemp.getRuntime());
        movie.setTagline(movieTemp.getTagline());
        return movie;
    }


}
