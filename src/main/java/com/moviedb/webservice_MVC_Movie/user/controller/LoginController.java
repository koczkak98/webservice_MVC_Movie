package com.moviedb.webservice_MVC_Movie.user.controller;

import com.moviedb.webservice_MVC_Movie.user.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepo;
    private String message = "";

    @GetMapping("/")
    public String welcome(Model model) {
        model.addAttribute("message", message);

        return "welcome.html";
    }



    @PostMapping("/")
    public String login (@RequestParam("email") String email,
                         @RequestParam("pwd") String pwd,
                         HttpServletResponse response){

        String destinationURL = "";

        List<User> accounts = new ArrayList<User>();
        /** Check User */
        try {
            accounts = this.userRepo.findByEmailAndPwd(email, pwd);
            System.out.println(accounts.get(0).getName() + " login.");
            if(accounts.size() == 1)
            {
                /** Valid */
                Cookie cookie = new Cookie("user", email);
                response.addCookie(cookie);
                System.out.println("go to link");

                destinationURL = "redirect:/getuser/" + accounts.get(0).getUserID();
            }
            else
            {
                /** Invalid */
                this.message = "Invalid";
                destinationURL = "welcome.html";
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            this.message = "Invalid";
            destinationURL = "welcome.html";
        }
        catch (Exception e)
        {
            this.message = "Invalid";
            destinationURL = "welcome.html";
        }

        return destinationURL;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        Cookie[] cookies = request.getCookies();
        for(int i = 0; i< cookies.length; i++)
        {
            Cookie cookie = cookies[i];
            if (cookie.getName().equals("user")){
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return "welcome.html";
    }


}
