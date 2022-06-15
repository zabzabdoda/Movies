package com.zabzabdoda.controllers;

import com.zabzabdoda.ConfigProperties;
import com.zabzabdoda.model.Movie;
import com.zabzabdoda.model.Review;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.MovieRepository;
import com.zabzabdoda.repository.ReviewRepository;
import com.zabzabdoda.repository.UserRepository;
import com.zabzabdoda.services.PosterService;
import com.zabzabdoda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    ConfigProperties configProperties;

    @RequestMapping({"","/"})
    public ModelAndView showHome(Model model){
        return new ModelAndView("/public/home");
    }

    @RequestMapping("/home")
    public ModelAndView showHomePage(Model model){
        ModelAndView modelAndView = new ModelAndView("home.html");
        List<Integer> reviews = reviewRepository.findMostPopular();
        List<Movie> movies = new ArrayList<>();
        for(Integer r : reviews){
            movies.add(movieRepository.findByMovieId(r.intValue()));
        }
        modelAndView.addObject("topMovies",movies);
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
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> movies = movieRepository.findBymovieNameContainingIgnoreCase(search,pageable);
        try {
            for (Movie m : movies) {
                if(m.getPosterUrl() == null) {
                    m.setPosterUrl(getPoster(m.getImdbId()));
                }
            }
        }catch (Exception e){}
        movieRepository.saveAll(movies);
        ModelAndView modelAndView = new ModelAndView("movieSearch.html");
        modelAndView.addObject("movies",movies);
        return modelAndView;
    }

    private String getPoster(String imdbUrl){
        try {
            String url = "https://movie-database-alternative.p.rapidapi.com/?r=json&i="+imdbUrl;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key",configProperties.apiKey());
            headers.set("X-RapidAPI-Host",configProperties.apiUrl());
            HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,httpEntity,String.class);
            JsonParser springParser = JsonParserFactory.getJsonParser();
            Map<String, Object> g = springParser.parseMap(result.getBody());
            return (String) g.get("Poster");
        }catch (Exception e){
            return null;
        }

    }

}
