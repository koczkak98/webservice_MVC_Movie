package com.moviedb.webservice_MVC_Movie.user.controller;

import com.moviedb.webservice_MVC_Movie.user.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.user.model.Movie;
import com.moviedb.webservice_MVC_Movie.user.model.MovieInfo;
import com.moviedb.webservice_MVC_Movie.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository("com.moviedb.webservice_MVC_Movie.user.db")
@Controller
public class UserController {

        @Autowired
        private UserRepository userRepo;


        @PostMapping("/signup")
        public String singUp (@RequestParam("name") String name,
                              @RequestParam("userName") String userName,
                              @RequestParam("email") String email,
                              @RequestParam("pwd") String pwd,
                              HttpServletResponse response,
                              Model model)
        {

                String message = "";
                model.addAttribute("message", message);
                List<User> accounts = this.userRepo.findAll();
                User user = new User();

                for (int i = 0; i < accounts.size(); i++) {
                    if (email.equals(accounts.get(i).getEmail())) {

                        message = "Such user already exists!";
                    } else if (name.equals("") || userName.equals("")
                            || email.equals("") || pwd.equals("")) {

                        message = "Some fields are empty!";
                    } else {
                        /** Update user */
                        user.setName(name);
                        user.setEmail(email);
                        user.setPwd(pwd);

                        /** Add Cookies */
                        Cookie cookie = new Cookie("nickname", userName);
                        cookie.setMaxAge(108000);
                        response.addCookie(cookie);

                        /** Save DB */
                        this.userRepo.save(user);
                        message = "Successful registration!";
                    }
                }


            return "redirect:/";
        }


        @GetMapping("/getuser/{userID}")
        public String getUserMovies(
                @PathVariable("userID") int userID,
                @CookieValue(value = "user", defaultValue = "INVALID_USER") String email,
                @CookieValue(value = "nickname", defaultValue = "INVALID_USER") String nickname,
                Model model) throws SQLException
        {

            User user = this.userRepo.findByEmail(email);

            if (email.equals("INVALID_USER") || userID != user.getUserID())
            {
                return "redirect:/";
            }
            else {

                /** Available for logged in users only */

                /** db */
                user = this.userRepo.findById(userID);

                MovieInfo mi = new MovieInfo(userID);
                List<Integer> myMovieIDs = user.getMovieIds();

                RestTemplate restTemplate = new RestTemplate();
                for (int idx = 0; idx < myMovieIDs.size(); idx++) {
                    Movie movie = restTemplate.getForObject("http://localhost:8081/getmovie/" + myMovieIDs.get(idx), Movie.class);

                    mi.addMovie(movie);
                }


                model.addAttribute("myMovies", mi);
                model.addAttribute("users", user);
                model.addAttribute("nickname", "Welcome back " + nickname + " :) !");


                return "mymovies.html";
            }
        }

    @GetMapping("/movie/top_rated")
    public String getTopRating (
            Model model)
    {


            return "toprating.html";
    }


}
