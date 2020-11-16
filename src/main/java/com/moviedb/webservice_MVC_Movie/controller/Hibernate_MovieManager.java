package com.moviedb.webservice_MVC_Movie.controller;

import com.moviedb.webservice_MVC_Movie.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Hibernate_MovieManager {

    private SessionFactory sessionFactory;


    public void open() {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();

        sessionFactory =
                new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public User getUserById(int userId) {
        User user = new User();

        Session session = sessionFactory.openSession();
        session.beginTransaction();


        user = session.get(User.class, userId);


        session.getTransaction().commit(); // needed for Create, Update, Delete transactions
        session.close();


        return user;
    }


    public void close() {
        sessionFactory.close();
    }
}
