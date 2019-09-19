package org.webapp.config;

import org.apache.jasper.servlet.JspServlet;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.*;
import java.util.EnumSet;

@Configuration
@ComponentScan(basePackages = "org.webapp")
@EnableWebMvc
public class MyApplicationStarter implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context =
                new AnnotationConfigWebApplicationContext();
        context.scan("org.webapp.config");
        context.register(org.webapp.config.RootContextConfiguration.class);
        servletContext.addListener(new ContextLoaderListener(context));

        AnnotationConfigWebApplicationContext dispatcherContext =
                new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(WebContextConfiguration.class);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        ServletRegistration.Dynamic htmlServlet = servletContext.addServlet("htmlServlet", new JspServlet());
        htmlServlet.setLoadOnStartup(2);
        htmlServlet.addMapping("/");

        FilterRegistration charEncodingFilter = servletContext.addFilter("CharacterEncodingFilter", new CharacterEncodingFilter());
        charEncodingFilter.setInitParameter("encoding", "utf-8");
        charEncodingFilter.setInitParameter("forceEncoding", "true");
        charEncodingFilter.addMappingForUrlPatterns(null, true, "/*");
    }
}
