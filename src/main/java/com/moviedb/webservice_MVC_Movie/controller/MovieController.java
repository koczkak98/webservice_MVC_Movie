package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.model.Movie;
import com.moviedb.webservice_MVC_Movie.model.MovieInfo;
import com.moviedb.webservice_MVC_Movie.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class MovieController {

    @GetMapping("/getuser/{userid}")
    public String getUserMovies(
            @PathVariable("userid") int userID,
            Model model) throws SQLException
    {
        /** Hibernate */
        Hibernate_MovieManager movieManager = new Hibernate_MovieManager();
        movieManager.open();
        User user = movieManager.getUserById(userID);
        movieManager.close();



        MovieInfo mi = new MovieInfo(userID);
        List<Integer> myMovieIDs = user.getMovieIds();

        RestTemplate restTemplate = new RestTemplate();
        for (int idx = 0; idx < myMovieIDs.size(); idx++) {
            Movie movie = restTemplate.getForObject("http://localhost:8081/getmovie/" + myMovieIDs.get(idx), Movie.class);

            mi.addMovie(movie);
        }


        model.addAttribute("myMovies", mi);


        return "mymovies.html";
    }


    @GetMapping("/badmovis/{startId}/{finishId}")
    public String getBadmoviesbyId (@PathVariable("startId") int startId,
                                    @PathVariable("finishId") int finishId,
                                    Model model)
    {

        /** User replies */
        List<Integer> movieIDs = new ArrayList<Integer>();

        for(int i = startId; i <= finishId; i++)
        {
            movieIDs.add(i);
        }

        System.out.println(movieIDs);

        /** Movie */
        List<Movie> movies = new ArrayList<Movie>();
        List<Integer> moviesScored = new ArrayList<Integer>();

        RestTemplate restTemplate = new RestTemplate();
        for(int movieId = 0; movieId < movieIDs.size(); movieId++)
        {
            try {
                Movie movie = restTemplate.getForObject("http://localhost:8081/getmovie/" + movieIDs.get(movieId), Movie.class);
                movies.add(movie);
                moviesScored.add(movie.getVote_count());
            }
            catch (Exception e)
            {
                movieId++;
            }
        }

        System.out.println(moviesScored);

        int min = Collections.min(moviesScored);

        MovieInfo movieInfo = new MovieInfo(16);
        for (int i = 0; i < movies.size(); i++)
        {
            if (movies.get(i).getVote_count() == min)
            {
                movieInfo.addMovie(movies.get(i));
            }
        }

        model.addAttribute("badMovies", movieInfo);

        return "badmovie.html";

    }


}
