package com.infotechnano.nanocommerce.controllers;

import com.infotechnano.nanocommerce.models.Address;
import com.infotechnano.nanocommerce.models.User;
import com.infotechnano.nanocommerce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final UserService userService;

    @Autowired(required = false)
    public AccountController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(path = "listall")
    public String listAll(Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        List<User> users = userService.listAll();
        model.addAttribute("users",users);
        model.addAttribute("userNum",users.size());
        return "accounts";
    }

    @GetMapping(path = "address/{userId}")
    public String userAddress(@PathVariable String userId,Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        Address address = userService.getAddress(UUID.fromString(userId));
        model.addAttribute("userAddress",address);
        return "useraddress";
    }

    @PostMapping(path = "address")
    public String changeAddress(@ModelAttribute("userAddress") Address address, Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        userService.changeAddress(address);
        return "index";
    }

    @GetMapping(path = "suspend/{userId}")
    public String suspendUser(@PathVariable String userId,Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        userService.suspendUser(UUID.fromString(userId));

        return "index";
    }

    @GetMapping(path = "unsuspend/{userId}")
    public String unsuspendUser(@PathVariable String userId,Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        userService.unsuspendUser(UUID.fromString(userId));

        return "index";
    }

    @GetMapping(path = "delete/{userId}")
    public String deleteUser(@PathVariable String userId,Model model, HttpSession session){
        model.addAttribute("theme",session.getAttribute("theme"));
        model.addAttribute("themeBtn",session.getAttribute("themeBtn"));

        userService.deleteUser(UUID.fromString(userId));

        return "index";
    }
}
