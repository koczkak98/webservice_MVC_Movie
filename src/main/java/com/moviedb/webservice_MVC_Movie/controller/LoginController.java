package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

            /** email */
            byte[] encryptEmail = encrypt(email);
            System.out.println("encryptEmail[]: ");
            for(int i = 0; i < encryptEmail.length; i++)
            {
                System.out.print(encryptEmail[i]);
            }
            System.out.println();
            String encryptEmailString = new String(encryptEmail);
            System.out.println("encryptEmailString: " + encryptEmailString);

            /** pwd */
            byte[] encryptPwd = encrypt(pwd);
            System.out.println("encryptPwd[]: ");
            for(int i = 0; i < encryptPwd.length; i++)
            {
                System.out.print(encryptPwd[i]);
            }
            System.out.println();

            String encryptPwdString = new String(encryptPwd);
            System.out.println("encryptPwdString: " + encryptPwdString);


            /** name */
            byte[] encryptName = encrypt(name);
            System.out.println("encryptName[]: ");
            for(int i = 0; i < encryptName.length; i++)
            {
                System.out.print(encryptName[i]);
            }
            System.out.println();

            String encryptNameString = new String(encryptName);
            System.out.println("encryptNameString: " + encryptNameString);


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

            /** email */

            byte [] encryptEmail = encrypt(email);
            String encryptEmailString = new String (encryptEmail);

            /** pwd */
            byte [] encryptPwd = encrypt(pwd);
            String encryptPwdString = new String (encryptPwd);

             /** check user */
            accounts = this.userRepo.findByEmailAndPwd(encryptEmailString, encryptPwdString);
            /** */


            System.out.println("email: ");
            System.out.println("encryptEmail[] : ");
            for(int i = 0; i < encryptEmail.length; i++)
            {
                System.out.print(encryptEmail[i]);
            }

            System.out.println();
            System.out.println("encryptEmailString: " + encryptEmailString);
            byte[] decryptEmail = accounts.get(0).getEmail().getBytes();
            System.out.println();

            System.out.println("after db Email[] : ");
            for(int i = 0; i < decryptEmail.length; i++)
            {
                System.out.print(decryptEmail[i]);
            }

            System.out.println();

            System.out.println("after db pwd String: " + accounts.get(0).getEmail());

            System.out.println("-------------------------------------------");

            System.out.println("pwd: ");

            System.out.println("encryptPwd[] : ");
            for(int i = 0; i < encryptPwd.length; i++)
            {
                System.out.print(encryptPwd[i]);
            }
            System.out.println();
            System.out.println("encryptPwdString: " + encryptPwdString);

            byte [] decryptPwd = accounts.get(0).getPwd().getBytes();

            System.out.println("after db Pwd[] : ");
            for(int i = 0; i < decryptPwd.length; i++)
            {
                System.out.print(decryptPwd[i]);
            }
            System.out.println();
            System.out.println("after db pwd String: " + accounts.get(0).getPwd());
            

            System.out.println("-------------------------------------------");

            //System.out.println(accounts.get(0).getName().getBytes());
            if(accounts.size() == 1)
            {
                /** Valid */
                /** 1. */
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30);
                System.out.println(session.getId());

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


    public byte[] encrypt(String toBeEncrypted) {

        byte[] encrypted = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            encrypted = cipher.doFinal(toBeEncrypted.getBytes());
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return encrypted;
    }

    public String decrypt(byte[] encrypted) {

        String original = null;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            System.out.println( "encrypted in decrypt: " + encrypted);
            original = new String(cipher.doFinal(encrypted));
            System.out.println("original in decrypt: " + original);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return original;
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
