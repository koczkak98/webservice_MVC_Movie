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

    @Column
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_movie_mapping",
            joinColumns = @JoinColumn(name = "userid")
    )
    @Column(name = "movieid")
    private List<Integer> movieIds;

    @Column(name = "email")
    private String email;

    @Column(name = "pwd")
    private String pwd;

    @Transient
    private String nickname;

    public User() {
        super();
        movieIds = new ArrayList<Integer>();
    }


    public User(int userID) {
        super();
        this.userID = userID;
        movieIds = new ArrayList<Integer>();
    }




    public List<Integer> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Integer> movieIds) {
        this.movieIds = movieIds;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addMovieIds (int movieId){ this.movieIds.add(movieId);}

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }
}