package com.moviedb.webservice_MVC_Movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
public class WebserviceMvcMovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebserviceMvcMovieApplication.class, args);
	}

}
