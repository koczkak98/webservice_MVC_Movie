package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepo;

    private static final String initVector = "initVectorKey123";
    private static final String key = "aesEncryptionKey";

    @GetMapping("/")
    public String welcome() {

        return "login.html";
    }

    @GetMapping("/signup")
    public String startSignup ()
    {
        return "signup.html";
    }

    @PostMapping("/signup")
    public String finishSignUp (@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("pwd") String pwd,
                          Model model)
    {
        System.out.println("finishSignUp");
        Security security = new Security();
        String message = "";
        boolean isItTrue = true;

        List<User> accounts = this.userRepo.findAll();

        if (name.equals("") || email.equals("") || pwd.equals("")) {

            isItTrue = false;
            message = "Some fields are empty!";
        }
        if (pwd.length() < 8)
        {
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

            System.out.println("SignUp...");

            /** EMAIL */
            System.out.println("EMAIL SIGNUP...");
            String encryptEmailString = security.encrypt(email);
            System.out.println("encryptEmailString = " + encryptEmailString);

            /** PWD */
            System.out.println("PWD SIGNUP...");
            String encryptPwdString = security.encrypt(pwd);
            System.out.println("encryptPwdString = " + encryptPwdString);

            /** NAME */
            String encryptNameString = security.encrypt(name);

            /** Update user */
            User user = new User();
            user.setEmail(encryptEmailString);
            user.setPwd(encryptPwdString);
            user.setName(encryptNameString);

            /** Save DB */
            this.userRepo.save(user);
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

        System.out.println("login");

        Security security = new Security();
        String message = "";
        String destinationURL = "";

        List<User> accounts = new ArrayList<User>();
        /** Check User */
        try {

            /** EMAIL */
            System.out.println("EMAIL LOGIN...");
            String encryptEmailString = security.encrypt(email);
            System.out.println("encryptEmailString = " + encryptEmailString);
            System.out.println("decrypt encryptEmailString: " + security.decrypt(encryptEmailString.getBytes()));

            /** PWD */
            System.out.println("PWD LOGIN...");
            String encryptPwdString = security.encrypt(pwd);
            System.out.println("encryptPwdString = " + encryptPwdString);
            System.out.println("decrypt encryptPwdString: " + security.decrypt(encryptPwdString.getBytes()));

            /** check user */
            accounts = this.userRepo.findByEmailAndPwd(encryptEmailString, encryptPwdString);
            /** */

            if(accounts.size() == 1)
            {
                /** Valid */
                /** 1. */
                HttpSession session = request.getSession();
                //session.setMaxInactiveInterval(30);
                System.out.println("SESSIONID: " + session.getId());

                /** 2. */
                session.setAttribute("user", encryptEmailString);
                message = "Valid";

                destinationURL = "redirect:/getuser/" + accounts.get(0).getUserID();
            }
            else
            {
                /** Invalid */
                message = "Invalid";
                destinationURL = "login.html";
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            message = "Invalid";
            destinationURL = "login.html";
        }
        catch (Exception e)
        {
            message = "Invalid";
            destinationURL = "login.html";
        }

        model.addAttribute("message", message);

        return destinationURL;
    }



    @GetMapping("/logout")
    public String startLogout(HttpServletRequest request, HttpServletResponse response){

        System.out.println("LOGOUT");

        String link = "";

        System.out.println("---> SESSIONID: " + request.getSession(false).getId());
        request.getSession(false).removeAttribute("user");
        request.getSession(false).invalidate();
        link = "redirect:/";


        return link;
    }






}
