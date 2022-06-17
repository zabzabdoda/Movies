package com.zabzabdoda.controllers;

import com.zabzabdoda.model.Review;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.MovieRepository;
import com.zabzabdoda.repository.ReviewRepository;
import com.zabzabdoda.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("private")
public class PrivateController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    @RequestMapping("/dashboard")
    public ModelAndView showDashboard(Model model, Authentication authentication, HttpSession session){
        User user = userRepository.readByUsername(authentication.getName());
        List<Review> reviews = reviewRepository.findByUser_id(user.getId());
        ModelAndView modelAndView = new ModelAndView("dashboard.html");
        modelAndView.addObject("reviews",reviews);
        modelAndView.addObject("user",user);
        modelAndView.addObject("dashboardUser",user);
        session.setAttribute("loggedInUser",user);
        return modelAndView;
    }

    @RequestMapping("/success")
    public ModelAndView getSuccess(Model model, Authentication authentication, HttpSession session){
        User user = userRepository.readByUsername(authentication.getName());
        session.setAttribute("loggedInUser",user);
        ModelAndView modelAndView = new ModelAndView("redirect:/private/dashboard");
        return modelAndView;
    }

    @PostMapping("/postReview")
    public ModelAndView postReview(Model model, @ModelAttribute("Review") Review review, @RequestParam("movie_id") int movie_id, HttpSession session){
        User user = (User) session.getAttribute("loggedInUser");
        //User user = userRepository.readByUsername(authentication.getName());
        review.setUser(user);
        if(user.getRole().getRoleName().equals("CRITIC")) {
            review.setCriticReview(true);
        }
        review.setMovie(movieRepository.findByMovieId(movie_id));
        reviewRepository.save(review);
        ModelAndView modelAndView = new ModelAndView("redirect:/public/movie?id="+movie_id);
        return modelAndView;
    }

    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            new SecurityContextLogoutHandler().logout(request,response,auth);
        }
        return "redirect:/public/login?logout=true";
    }

    @GetMapping("/editReview")
    public ModelAndView editReview(Model model, @RequestParam int id, HttpSession session){
        ModelAndView modelAndView = new ModelAndView("editReview.html");
        Review review = reviewRepository.findByReviewId(id);
        User loggedIn = (User) session.getAttribute("loggedInUser");
        if(review != null && review.getUser().getId() == loggedIn.getId() || loggedIn.getRole().getRoleName().equals("ADMIN")){
            modelAndView.addObject("review",review);
            return modelAndView;
        }
        modelAndView.setViewName("redirect:/public/home");
        return modelAndView;
    }

    @PostMapping("/editReview")
    public ModelAndView editReviewPost(Model model, @ModelAttribute("review") Review review, @RequestParam int movie_id, @RequestParam int review_id, HttpSession session){
        User user = (User) session.getAttribute("loggedInUser");
        review.setUser(user);
        review.setReviewId(review_id);
        review.setMovie(movieRepository.findByMovieId(movie_id));
        reviewRepository.save(review);
        ModelAndView modelAndView = new ModelAndView("redirect:/public/movie?id="+movie_id);
        return modelAndView;
    }

    @RequestMapping("/deleteReview")
    public ModelAndView deleteReview(Model model, @RequestParam int id, HttpSession session, HttpServletRequest request){
        String redirect = request.getHeader("Refer");
        if(request.getHeader("Refer") == null){
            redirect = "redirect:/private/dashboard";
        }
        Review review = reviewRepository.findByReviewId(id);
        User u = ((User)session.getAttribute("loggedInUser"));
        if(u != null && review.getUser().getId() == u.getId() || u.getRole().getRoleName().equals("ADMIN")) {
            reviewRepository.delete(review);
        }
        ModelAndView modelAndView = new ModelAndView(redirect);
        return modelAndView;
    }

}
