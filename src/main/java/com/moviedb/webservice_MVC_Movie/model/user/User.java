package com.moviedb.webservice_MVC_Movie.model.user;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_movie_mapping",
            joinColumns = @JoinColumn(name = "userid")
    )
    @Column(name = "movieid")
    private List<Integer> movieIds;



    @Column
    private String name;



    @Column(name = "email")
    private String email;



    @Column(name = "pwd")
    private String pwd;


    public User() {
        super();
        movieIds = new ArrayList<Integer>();
    }


    public User(int userID) {
        super();
        this.userID = userID;
        movieIds = new ArrayList<Integer>();
    }


    /** ID */

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


    /** Personal */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }



    /** MovieIDS */

    public List<Integer> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Integer> movieIds) {
        this.movieIds = movieIds;
    }

    public void addMovieIds (int movieId){ this.movieIds.add(movieId);}




}