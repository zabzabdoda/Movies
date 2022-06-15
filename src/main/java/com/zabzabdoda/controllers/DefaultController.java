package com.zabzabdoda.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DefaultController {

    @RequestMapping({"","/","/home"})
    public ModelAndView defaultMapping(){
        return new ModelAndView("redirect:/public/home");
    }


}
