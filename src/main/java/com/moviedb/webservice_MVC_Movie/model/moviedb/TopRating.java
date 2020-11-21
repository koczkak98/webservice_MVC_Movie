package com.moviedb.webservice_MVC_Movie.model.moviedb;

import java.util.List;

public class TopRating {

    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
