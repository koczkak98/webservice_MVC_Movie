package com.moviedb.webservice_MVC_Movie.controller;


import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;

import javax.servlet.ServletContext;

@RewriteConfiguration
public class RESTConfigurationProvider extends HttpConfigurationProvider {

    @Override
    public int priority()

    {
        return 10;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context)
    {

        return ConfigurationBuilder.begin()
                .addRule()
                .when(Direction.isInbound().and(Path.matches("jsessionid=")))
                .perform(Forward.to(""));
    }

}
