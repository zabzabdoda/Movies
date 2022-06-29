package com.zabzabdoda.controllers;

import com.zabzabdoda.MovieConstants;
import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.MovieTemp;
import com.zabzabdoda.model.Roles;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.MovieRepository;
import com.zabzabdoda.repository.MovieTempRepository;
import com.zabzabdoda.repository.UserRepository;
import com.zabzabdoda.services.MovieService;
import com.zabzabdoda.services.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieTempRepository movieTempRepository;

    @Autowired
    PosterService posterService;

    @Autowired
    MovieService movieService;

    @Autowired
    MovieRepository movieRepository;

    @RequestMapping("adminPanel")
    public ModelAndView showAdminPanel(Model model){
        ModelAndView modelAndView = new ModelAndView("adminPanel.html");
        modelAndView.addObject("users",userRepository.findAll());
        return modelAndView;
    }

    @RequestMapping("changeRole")
    public ModelAndView postChangeRole(Model model, @RequestParam String changeRole, @RequestParam String username){
        User user = userRepository.readByUsername(username);
        Roles roles = new Roles();
        roles.setRoleName(changeRole);
        roles.setRoleId(MovieConstants.roleMap.get(changeRole));
        user.setRole(roles);
        userRepository.save(user);
        return new ModelAndView("redirect:/public/dashboard?username="+username);
    }

    @RequestMapping("tempMovies")
    public ModelAndView getTempMovies(Model model, @RequestParam(value = "rejected", required = false) String rejected, @RequestParam(value = "accepted", required = false) String accepted){
        ModelAndView modelAndView = new ModelAndView("tempMovies.html");
        List<MovieTemp> tempMovieList = movieTempRepository.findAll();
        modelAndView.addObject("tempMovies",tempMovieList);
        modelAndView.addObject("movie",new MovieTemp());
        if(rejected != null){
            modelAndView.addObject("message","Rejected movie");
        }else if(accepted != null){
            modelAndView.addObject("message","Added movie");
        }
        return modelAndView;
    }

    @PostMapping("tempMovies")
    public ModelAndView postTempMovies(Model model, @RequestParam("button") String button, @ModelAttribute("movie") MovieTemp tempMovie){
        ModelAndView modelAndView = new ModelAndView();
        movieTempRepository.save(tempMovie);
        if(button.equals("preview")){
            modelAndView.setViewName("redirect:/admin/previewPage?id="+tempMovie.getMovieId());
        }else if(button.equals("accept")){
            modelAndView.setViewName("redirect:/admin/acceptMovie?id="+tempMovie.getMovieId());
        }else if(button.equals("reject")){
            modelAndView.setViewName("redirect:/admin/rejectMovie?id="+tempMovie.getMovieId());
        }
        return modelAndView;
    }

    @RequestMapping("previewPage")
    public ModelAndView getPreviewPage(Model model, @RequestParam("id") int id){
        ModelAndView modelAndView = new ModelAndView("moviePreview.html");
        MovieTemp movieTemp = movieTempRepository.findByMovieId(id);
        if((movieTemp.getPosterUrl() == null || movieTemp.getPosterUrl().equals("")) && movieTemp.getImdbId() != null){
            movieTemp.setPosterUrl(posterService.getPoster(movieTemp.getImdbId()));
            movieTempRepository.save(movieTemp);
        }
        modelAndView.addObject("movie",movieTemp);

        return modelAndView;
    }

    @RequestMapping("acceptMovie")
    public ModelAndView getAcceptMovie(Model model, @RequestParam("id") int id){
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/tempMovies?accepted=true");
        MovieTemp movieTemp = movieTempRepository.findByMovieId(id);
        Movie movie = movieService.mapMovie(movieTemp);
        movieRepository.save(movie);
        movieTempRepository.delete(movieTemp);

        return modelAndView;
    }

    @RequestMapping("rejectMovie")
    public ModelAndView getRejectMovie(Model model, @RequestParam("id") int id){
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/tempMovies?rejected=true");
        MovieTemp movieTemp = movieTempRepository.findByMovieId(id);
        movieTempRepository.delete(movieTemp);
        return modelAndView;
    }

    @PostMapping("deleteMovie")
    public ModelAndView deleteMovie(Model model, @RequestParam("id") int id){
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        movieRepository.deleteById(id);
        return modelAndView;
    }

}
