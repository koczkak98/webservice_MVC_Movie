package com.moviedb.webservice_MVC_Movie.db;

import com.moviedb.webservice_MVC_Movie.model.user.User;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface UserRepository extends Repository<User, Integer> {

    List<User> findAll();

    User findById(Integer userId);

    List<User> findByName(String name);

    User findByEmail(String email);

    List<User> findByEmailAndPwd(String email, String pwd);

    void save(User user);
}
