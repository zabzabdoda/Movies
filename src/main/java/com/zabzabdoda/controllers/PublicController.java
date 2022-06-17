package com.zabzabdoda.controllers;

import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.Review;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.MovieRepository;
import com.zabzabdoda.repository.ReviewRepository;
import com.zabzabdoda.repository.UserRepository;
import com.zabzabdoda.services.PosterService;
import com.zabzabdoda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("public")
public class PublicController {

    @Autowired
    UserService userService;

    @Autowired
    PosterService posterService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping({"","/"})
    public ModelAndView showHome(Model model){
        return new ModelAndView("/public/home");
    }

    @RequestMapping("/home")
    public ModelAndView showHomePage(Model model, @RequestParam(required = false) String sortby){
        ModelAndView modelAndView = new ModelAndView("home.html");
        List<Movie> movies = new ArrayList<>();
        System.out.println(sortby);
        if(sortby == null) {
            sortby = "popularity";
        }
        if (sortby.equals("popularity")) {
            movies = movieRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "popularity"))).toList();
        } else if (sortby.equals("ratinghigh")) {
            movies = movieRepository.findTopmoviesByRating(PageRequest.of(0, 10));
        } else if (sortby.equals("ratinglow")) {
            movies = movieRepository.findLowestmoviesByRating(PageRequest.of(0, 10));
        } else if (sortby.equals("reviews")) {
            List<Integer> reviews = reviewRepository.findMostPopular();
            for (Integer r : reviews) {
                movies.add(movieRepository.findByMovieId(r.intValue()));
            }
        }
        try {
            for (Movie m : movies) {
                if (m.getPosterUrl() == null) {
                    m.setPosterUrl(posterService.getPoster(m.getImdbId()));
                }
            }
        } catch (Exception e) {
        }
        movieRepository.saveAll(movies);
        modelAndView.addObject("topMovies",movies);
        return modelAndView;
    }

    @PostMapping("/home")
    public ModelAndView showHomePagePost(Model model, @RequestParam(required = false) String sortby){
        ModelAndView modelAndView = new ModelAndView("redirect:/public/home?sortby="+sortby);
        return modelAndView;
    }

    @RequestMapping("/register")
    public ModelAndView showRegisterPage(Model model){
        ModelAndView modelAndView = new ModelAndView("register.html");
        if(model.containsAttribute("user")){
            modelAndView.addObject("user",model.getAttribute("user"));
        }else {
            modelAndView.addObject("user", new User());
        }
        if(model.containsAttribute("errors")){
            System.out.println(model.getAttribute("errors"));
            modelAndView.addObject("errors",model.getAttribute("errors"));
        }
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView addNewUser(ModelAndView model, @Valid @ModelAttribute("user") User user, Errors errors, BindingResult result, RedirectAttributes redirectAttributes){
        ModelAndView modelAndView = new ModelAndView("login");
        if(userService.usernameExists(user)){
            errors.reject("username","User with that username already exists");
        }
        if(userService.emailExists(user)){
            errors.reject("email","User with that email already exists");
        }
        if(errors.hasErrors()){
            //redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePassword", result);
            redirectAttributes.addFlashAttribute("user", user);

            List<String> ers = new ArrayList<>();
            for(ObjectError e : errors.getAllErrors()){
                ers.add(e.getDefaultMessage());
                System.out.println(e.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("errors",ers);
            modelAndView.setViewName("redirect:/public/register");
            return modelAndView;
        }
        userService.createNewUser(user);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView showLogin(Model model, Authentication authentication, HttpSession session, @RequestParam(required = false) String error, @RequestParam(required = false) String logout, @RequestParam(required = false) String registered){
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("login.html");
        if(error != null){
            errorMessage = "Incorrect login information";
        }
        if(registered != null){
            errorMessage = "Successfully registered, please log in below";
        }
        if(logout != null){
            errorMessage = "you have been successfully logged out";
        }
        modelAndView.addObject("errorMessage",errorMessage);
        return modelAndView;
    }

    @RequestMapping("/movie")
    public ModelAndView showMovie(Model model, @RequestParam int id){
        Movie movie = movieRepository.findByMovieId(id);
        List<Review> reviews = reviewRepository.findByMovie_movieId(id);
        ModelAndView modelAndView = new ModelAndView("movie.html");
        modelAndView.addObject("movie",movie);
        modelAndView.addObject("reviews",reviews);
        modelAndView.addObject("review",new Review());
        modelAndView.addObject("movieAverage",movie.getRating());
        return modelAndView;
    }

    @RequestMapping("/dashboard")
    public ModelAndView showDashboard(Model model, Authentication authentication, @RequestParam String username){
        User user = userRepository.readByUsername(username);
        ModelAndView modelAndView = new ModelAndView("dashboard.html");
        if(authentication != null) {
            User visitor = userRepository.readByUsername(authentication.getName());
            modelAndView.addObject("user",visitor);
        }else{
            modelAndView.addObject("user",new User());
        }
        List<Review> reviews = reviewRepository.findByUser_id(user.getId());
        modelAndView.addObject("dashboardUser",user);
        modelAndView.addObject("reviews",reviews);
        return modelAndView;
    }

    @RequestMapping(value = "/search")
    public ModelAndView showMovieSearch(Model model){
        ModelAndView modelAndView = new ModelAndView("movieSearch.html");
        if(model.containsAttribute("movies")){
            modelAndView.addObject("movies",model.getAttribute("movies"));
        }
        return modelAndView;
    }

    @PostMapping("/search")
    public ModelAndView returnMovieSearch(Model model, @RequestParam(required = false) String search){
        ModelAndView modelAndView = new ModelAndView("movieSearch.html");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC,"popularity"));
        List<Movie> movies = movieRepository.findBymovieNameContainingIgnoreCase(search,pageable);
        if(movies.size() > 0) {
            try {
                for (Movie m : movies) {
                    if (m.getPosterUrl() == null) {
                        m.setPosterUrl(posterService.getPoster(m.getImdbId()));
                    }
                }
            } catch (Exception e) {
            }
            movieRepository.saveAll(movies);
            modelAndView.addObject("movies",movies);
        }else{
            modelAndView.addObject("errorMessage","No results found");
        }


        return modelAndView;
    }

}
