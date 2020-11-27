package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

            System.out.println("SignUp...");

            /** EMAIL */
            System.out.println("EMAIL SIGNUP...");
            String encryptEmailString = encrypt(email);
            System.out.println("encryptEmailString = " + encryptEmailString);

            /** PWD */
            System.out.println("PWD SIGNUP...");
            String encryptPwdString = encrypt(pwd);
            System.out.println("encryptPwdString = " + encryptPwdString);

            /** NAME */
            String encryptNameString = encrypt(name);

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

        String message = "";
        String destinationURL = "";

        List<User> accounts = new ArrayList<User>();
        /** Check User */
        try {

            /** EMAIL */
            System.out.println("EMAIL LOGIN...");
            String encryptEmailString = encrypt(email);
            System.out.println("encryptEmailString = " + encryptEmailString);
            System.out.println("decrypt encryptEmailString: " + decrypt(encryptEmailString.getBytes()));

            /** PWD */
            System.out.println("PWD LOGIN...");
            String encryptPwdString = encrypt(pwd);
            System.out.println("encryptPwdString = " + encryptPwdString);
            System.out.println("decrypt encryptPwdString: " + decrypt(encryptPwdString.getBytes()));

            /** check user */
            accounts = this.userRepo.findByEmailAndPwd(encryptEmailString, encryptPwdString);
            /** */

            if(accounts.size() == 1)
            {
                /** Valid */
                /** 1. */
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30);
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


    public String encrypt(String toBeEncrypted) {

        String encryptedString = null;
        byte[] encryptedByteArray = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            encryptedByteArray = cipher.doFinal(toBeEncrypted.getBytes());
            encryptedString = Base64.encodeBase64String(encryptedByteArray);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return encryptedString;
    }

    public String decrypt(byte[] encrypted) {

        String original = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            original = new String(cipher.doFinal(Base64.decodeBase64(encrypted)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return original;
    }



    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        /**
        Cookie[] cookies = request.getCookies();
        for(int i = 0; i< cookies.length; i++)
        {
            Cookie cookie = cookies[i];
            if (cookie.getName().equals("user")){
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
         */

        String link = "";

        if(request.getSession(false) !=null) {
            try
            {
                request.getSession(false).removeAttribute("logonSessData");
                request.getSession(false).invalidate();
                String pageToForward = request.getContextPath();
                response.sendRedirect(pageToForward);
            }
            catch (Exception e)
            {
                link = "error.html";
            }
            }

        else {

            link = "welcome.html";
        }


        return link;
    }



}
