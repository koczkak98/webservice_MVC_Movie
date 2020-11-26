package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.db.UserRepository;
import com.moviedb.webservice_MVC_Movie.model.MovieInfo;
import com.moviedb.webservice_MVC_Movie.model.moviedb.Movie;
import com.moviedb.webservice_MVC_Movie.model.moviedb.Movies;
import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Repository("com.moviedb.webservice_MVC_Movie.user.db")
@Controller
public class UserController {

        @Autowired
        private UserRepository userRepo;

        @GetMapping("/getuser/{userID}")
        public String getUserMovies(
                @PathVariable("userID") int userID,
                @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                HttpServletRequest request,
                Model model) throws SQLException
        {

            HttpSession session = request.getSession(false);
            User user = new User();
            System.out.println("get/userid");

            try {

                String var = session.getAttribute("user").toString();
                System.out.println(var);
                user = this.userRepo.findByEmail(var);
            }
            catch (NullPointerException e)
            {
                System.out.println("... Null ...");
                return "redirect:/";
            }

            if (session == null)
            {
                System.out.println("... Null ...");
                return "redirect:/";
            }
            else if (!sessionId.equals(session.getId()))
            {
                return "redirect:/";
            }
            else if (user.getUserID() != userID)
            {
                return "redirect:/";
            }
            else
                {

                /** Available for logged in users only */
                /** db */
                user = this.userRepo.findById(userID);

                MovieInfo mi = new MovieInfo(userID);
                List<Integer> myMovieIDs = user.getMovieIds();

                RestTemplate restTemplate = new RestTemplate();
                for (int idx = 0; idx < myMovieIDs.size(); idx++) {
                    Movie movie = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + myMovieIDs.get(idx) + "?api_key=05e00aec1b6318f6f5a4702d18a8f725", Movie.class);

                    mi.addMovie(movie);
                }


                model.addAttribute("myMovies", mi);
                model.addAttribute("users", user);



                return "mymovies.html";
            }
        }

        @GetMapping("/addmovie/{userid}")
        public String startAddMovie (
                @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                HttpServletRequest request)
        {

            HttpSession session = request.getSession(false);
            String var = session.getAttribute("user").toString();
            User user = this.userRepo.findByEmail(var);

            String destinationURL = "";

            if (session == null)
            {
                destinationURL = "redirect:/";
            }
            else if (!sessionId.equals(session.getId()))
            {
                destinationURL = "redirect:/";
            }
            else {
                destinationURL = "addmovie.html";
            }

            return destinationURL;
        }


        @PostMapping("/addmovie/")
        public String finishAddMovie (
                                @RequestParam ("movieId") int movieId,
                                @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                                HttpServletRequest request)
        {

            HttpSession session = request.getSession(false);
            String var = session.getAttribute("user").toString();
            User user = this.userRepo.findByEmail(var);

            if (session == null)
            {
                return "redirect:/";
            }
            else if (!sessionId.equals(session.getId()))
            {
                return "redirect:/";
            }
            else
            {
                user.addMovieIds(movieId);
                this.userRepo.save(user);
            }

            return "redirect:/getuser/" + user.getUserID();
        }

        @GetMapping("/searchmovie")
        public String searchMovieByTitle (
                                        @RequestParam("movieTitle") String movieTitle)
        {


            return "";
        }

        @GetMapping("/movie/top_rated")
        public String getTopRating (
               Model model) {

            RestTemplate restTemplate = new RestTemplate();

            Movies movies = restTemplate.getForObject("https://api.themoviedb.org/3/movie/top_rated?api_key=05e00aec1b6318f6f5a4702d18a8f725", Movies.class);

            model.addAttribute("topRating", movies);

            return "toprating.html";
        }




}
