package com.listener;


import com.util.*;


import javax.servlet.*;
import javax.servlet.annotation.WebListener; 

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce){

        ServletContext context = sce.getServletContext();
        String basePackage = context.getInitParameter("package");
        Map<UrlMethod,RouteMapping> routes = new HashMap<>();

        try {

            AnnotationUtil.scanControllersInPackage(basePackage,routes);
            String configClass = context.getInitParameter("springConfig");


            Class<?> clazz = Class.forName(configClass);


            AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext(clazz);



            context.setAttribute("routes",routes);

            context.setAttribute("springContext",springContext);

            context.setAttribute(
                    "prefix",
                    context.getInitParameter("prefix")
            );

            context.setAttribute(
                    "suffix",
                    context.getInitParameter("suffix")
            );


            System.out.println("Framework + Spring initialise");


        } catch(Exception e){

            throw new RuntimeException(e);

        }
    }

    public void contextDestroyed(ServletContextEvent sce){
        ServletContext context = sce.getServletContext();


        AnnotationConfigApplicationContext springContext = (AnnotationConfigApplicationContext)context.getAttribute("springContext");


        if(springContext != null){
            springContext.close();
        }

    }

}