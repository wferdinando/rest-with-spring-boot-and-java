package br.com.wfsystems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        // Via EXTENSION. /api/person/v1/4.XML or /api/person/v1/4.JSON Deprecated on
        // Spring Boot 2.6

        // Via QUERY PARAM /api/person/v1/4?mediaType=xml
        /**
         * configurer.favorParameter(true)
         * .parameterName("mediaType")
         * .ignoreAcceptHeader(true)
         * .useRegisteredExtensionsOnly(false)
         * .defaultContentType(MediaType.APPLICATION_JSON)
         * .mediaType("json", MediaType.APPLICATION_JSON)
         * .mediaType("xml", MediaType.APPLICATION_XML);
         * 
         */

         // Via EXTENSION. /api/person/v1/4.XML or /api/person/v1/4.JSON Deprecated on
        // Spring Boot 2.6

        // Via HEADER PARAM /api/person/v1/4?mediaType=xml
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("yaml", MediaType.APPLICATION_YAML);
    }

}
