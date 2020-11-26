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

    @Transient
    private byte [] encryptName;


    @Column(name = "email")
    private String email;

    @Transient
    private byte [] encryptEmail;


    @Column(name = "pwd")
    private String pwd;

    @Transient
    private byte [] encryptPwd;

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

    public void setName(String name, byte[] EncryptName) {
        this.name = name;
        this.setEncryptName(EncryptName);
    }

    public byte[] getEncryptName() {
        return encryptName;
    }

    public void setEncryptName(byte[] encryptName) {
        System.out.println( "setEncryptName() --- >" + encryptName);
        this.encryptName = encryptName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email, byte[] EncryptEmail) {
        this.email = email;
        this.setEncryptEmail(EncryptEmail);
    }

    public byte[] getEncryptEmail() {
        return encryptEmail;
    }

    public void setEncryptEmail(byte[] encryptEmail) {
        this.encryptEmail = encryptEmail;
    }


    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd, byte[] EncryptPwd) {
        this.pwd = pwd;
        this.setEncryptPwd(EncryptPwd);
    }

    public byte[] getEncryptPwd() {
        return encryptPwd;
    }

    public void setEncryptPwd(byte[] encryptPwd) {
        this.encryptPwd = encryptPwd;
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