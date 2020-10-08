package com.infotechnano.nanocommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {

    @GetMapping("/")
    public String index(HttpSession session, Model model){
        if(session.getAttribute("theme") == null){
            session.setAttribute("theme","white");
            session.setAttribute("themeBtn","theme-btn-off");
        }
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));
        return "index";
    }

    @GetMapping("/toggletheme")
    public String toggleTheme(HttpSession session, Model model){
        if(session.getAttribute("theme") == "white"){
            session.setAttribute("theme","rgb(45,26,112)");
            session.setAttribute("themeBtn","theme-btn-on");
        }else{
            session.setAttribute("theme","white");
            session.setAttribute("themeBtn","theme-btn-off");
        }
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));
        return "index";
    }
}
