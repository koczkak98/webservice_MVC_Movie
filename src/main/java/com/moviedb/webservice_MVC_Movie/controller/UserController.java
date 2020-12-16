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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
                Model model) throws SQLException {

            Security security = new Security();

            HttpSession session = request.getSession(false);
            User user = new User();
            System.out.println("get/userid");
            System.out.println("SESSIONID: " + session.getId());

            try {
                String var = session.getAttribute("user").toString();
                System.out.println(var);
                user = this.userRepo.findByEmail(var);
            } catch (NullPointerException e)
            {
                return "redirect:/login";
            }


            if (session == null)
            {
                System.out.println("... Null ...");
                System.out.println("session == null");
                return "redirect:/login";
            }
            else if ( (!sessionId.equals("INVALID_USER")) && (!sessionId.equals(session.getId())))
            {
                System.out.println(sessionId);
                System.out.println("sessionId.equals(INVALID_USER))");

                return "redirect:/login";
            }
            else if (user.getUserID() != userID)
            {
                System.out.println("user.getUserID() != userID");
                return "redirect:/login";
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

                user.setName(security.decrypt(user.getName().getBytes()));

                model.addAttribute("myMovies", mi);
                model.addAttribute("users", user);

                return "mainpage.html";
            }
        }


        @PostMapping("/addmovie/")
        public String finishAddMovie (
                                @RequestParam ("movieId") int movieId,
                                @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                                HttpServletRequest request)
        {

            System.out.println("finishAddMovie");
            HttpSession session = request.getSession(false);
            System.out.println("---> SESSIONID: " + session.getId());

            String var = session.getAttribute("user").toString();
            User user = this.userRepo.findByEmail(var);

            if (session == null)
            {
                return "redirect:/";
            }
            else if ( (!sessionId.equals("INVALID_USER")) && (!sessionId.equals(session.getId())) )
            {
                return "redirect:/";
            }
            else
            {
                List<Integer> userMovieIds = user.getMovieIds();
                boolean has = false;
                for (int i = 0; i<userMovieIds.size(); i++)
                {
                    if(movieId == userMovieIds.get(i))
                    {
                        has = true;
                        break;
                    }
                }
                if(has == false) {
                    user.addMovieIds(movieId);
                    this.userRepo.save(user);
                }
            }

            return "redirect:/getuser/" + user.getUserID();
        }

        @PostMapping("/deletemovie/")
        public String deleteMovie (@RequestParam("movietitle") String movieTitle,
                                   @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                                   HttpServletRequest request,
                                   Model model)
        {
            System.out.println("/deletemovie");
            HttpSession session = request.getSession(false);
            System.out.println("---> SESSIONID: " + session.getId());

            String var = session.getAttribute("user").toString();
            User user = this.userRepo.findByEmail(var);

            String destinationURL = "";

            if (session == null)
            {
                destinationURL = "redirect:/";
            }
            else if ( (!sessionId.equals("INVALID_USER")) && (!sessionId.equals(session.getId())) )
            {
                destinationURL = "redirect:/";
            }
            else {

                /** SEARCH MOVIE */
                MovieInfo mi = new MovieInfo(user.getUserID());
                List<Integer> myMovieIDs = user.getMovieIds();
                System.out.println(myMovieIDs);

                RestTemplate restTemplate = new RestTemplate();
                for (int idx = 0; idx < myMovieIDs.size(); idx++) {
                    Movie movie = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + myMovieIDs.get(idx) + "?api_key=05e00aec1b6318f6f5a4702d18a8f725", Movie.class);

                    mi.addMovie(movie);
                }

                int counter = 0;
                String message = "";
                Movie resultMovie = null;
                for (int i = 0; i < mi.getMyMovies().size(); i++)
                {
                    if (movieTitle.contains(mi.getMyMovies().get(i).getTitle()))
                    {
                        counter++;
                        resultMovie = mi.getMyMovies().get(i);
                    }
                }
                mi.getMyMovies().clear();

                /** DELETE MOVIE */
                if(counter == 1) {
                    System.out.println(resultMovie.getId());
                    user.deleteMovieIds(resultMovie.getId());
                    userRepo.save(user);
                    message = "Successfully";
                }
                else
                {
                    message = "Unsuccessfully";
                }
                System.out.println(message);
            }

            return "redirect:/getuser/"+user.getUserID();
        }

        @GetMapping("/searchmovie")
        public String searchMovieByTitle (
                                        @RequestParam("movieTitle") String movieTitle,
                                        HttpServletRequest request,
                                        @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                                        Model model)
        {

            HttpSession session = request.getSession(false);
            String var = session.getAttribute("user").toString();
            User user = this.userRepo.findByEmail(var);


            MovieInfo mi = new MovieInfo(user.getUserID());
            List<Integer> myMovieIDs = user.getMovieIds();

            RestTemplate restTemplate = new RestTemplate();
            for (int idx = 0; idx < myMovieIDs.size(); idx++) {
                Movie movie = restTemplate.getForObject("https://api.themoviedb.org/3/movie/" + myMovieIDs.get(idx) + "?api_key=05e00aec1b6318f6f5a4702d18a8f725", Movie.class);

                mi.addMovie(movie);
            }

            int counter = 0;
            String message = "";
            Movie resultMovie = null;
            for (int i = 0; i < mi.getMyMovies().size(); i++)
            {
                if (movieTitle.contains(mi.getMyMovies().get(i).getTitle()))
                {
                    counter++;
                    resultMovie = mi.getMyMovies().get(i);
                }
            }

            if (counter == 1)
            {
                message = "Successful!";
            }
            else
            {
                message = "Unsuccessful!";
            }

            System.out.println(resultMovie.getTitle());
            System.out.println(message);

            model.addAttribute("hits", resultMovie);
            model.addAttribute("message", message);

            return "redirect:/getuser/" + user.getUserID();
        }

        @GetMapping("/")
        public String mainPage (
                HttpServletRequest request,
                @CookieValue(value = "JSESSIONID", defaultValue = "INVALID_USER") String sessionId,
                Model model) {

            String returnLink = "";

            /** CHECK SESSION AND USER */
            HttpSession session = request.getSession(false);
            try {
                String var = session.getAttribute("user").toString();
                User user = this.userRepo.findByEmail(var);

                model.addAttribute("users", user);

                returnLink = "welcome.html";
            }
            catch (NullPointerException e)
            {
                returnLink = "toprating.html";
            }
            catch (Exception e)
            {
                returnLink = "toprating.html";
            }


            RestTemplate restTemplate = new RestTemplate();
            Movies movies = restTemplate.getForObject("https://api.themoviedb.org/3/movie/top_rated?api_key=05e00aec1b6318f6f5a4702d18a8f725", Movies.class);
            model.addAttribute("topRating", movies);

            return returnLink;
        }




        /** RSA */
        @GetMapping("/example/getmovie/{movieID}")
        public Movie getMovieByID (@PathVariable("movieID") String movieID) throws NoSuchAlgorithmException, InvalidKeySpecException {

            RestTemplate rs = new RestTemplate();
            String publicKey_Base64 = rs.getForObject("http://localhost:8081/getPublicKey", String.class);
            SecurityRSA rsa = new SecurityRSA(publicKey_Base64);

            String encryptedMovieId = rsa.encrypt(movieID);

            Movie movie = rs.getForObject("http://localhost:8081/getmovie/" + encryptedMovieId, Movie.class);

            return movie;
        }




}
