package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/")
    public String welcome() {

        return "welcome.html";
    }

    @PostMapping("/signup")
    public String singUp (@RequestParam("name") String name,
                          @RequestParam("userName") String userName,
                          @RequestParam("email") String email,
                          @RequestParam("pwd") String pwd,
                          HttpServletResponse response,
                          Model model)
    {
        String message = "";
        boolean isItTrue = true;

        List<User> accounts = this.userRepo.findAll();

        if (name.equals("") || userName.equals("")
                || email.equals("") || pwd.equals("")) {

            isItTrue = false;
            message = "Some fields are empty!";
        }

        /** Check the user */
        for (int i = 0; i < accounts.size(); i++) {
            if (email.equals(accounts.get(i).getEmail())) {
                isItTrue = false;
                message = "Such user already exists!";
            }
        }
        model.addAttribute("message", message);

        if (isItTrue == true) {
            /** Update user */
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPwd(pwd);
            user.setNickname(userName);

            /** Save DB */
            this.userRepo.save(user);

            /** Add Cookies */
            Cookie cookie = new Cookie("nickname", userName);
            cookie.setMaxAge(2592000);
            response.addCookie(cookie);

            message = "Successful registration!";
        }


        model.addAttribute("message", message);

        return "redirect:/";
    }

    @PostMapping("/")
    public String login (@RequestParam("email") String email,
                         @RequestParam("pwd") String pwd,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model){

        String message = "";
        String destinationURL = "";

        List<User> accounts = new ArrayList<User>();
        /** Check User */
        try {
            accounts = this.userRepo.findByEmailAndPwd(email, pwd);
            System.out.println(accounts.get(0).getName() + " login.");
            if(accounts.size() == 1)
            {
                /** Valid */
                /** 1. */
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30);
                System.out.println(session.getId());
                /** 2. */
                session.setAttribute("user", email);

                message = "Valid";

                destinationURL = "redirect:/getuser/" + accounts.get(0).getUserID();
            }
            else
            {
                /** Invalid */
                message = "Invalid";
                destinationURL = "welcome.html";
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            message = "Invalid";
            destinationURL = "welcome.html";
        }
        catch (Exception e)
        {
            message = "Invalid";
            destinationURL = "welcome.html";
        }

        model.addAttribute("message", message);

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

        request.getSession(false).invalidate();

        return "welcome.html";
    }


}
