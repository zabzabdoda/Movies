package com.zabzabdoda.controllers;

import com.zabzabdoda.MovieConstants;
import com.zabzabdoda.model.Roles;
import com.zabzabdoda.model.User;
import com.zabzabdoda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

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


}
