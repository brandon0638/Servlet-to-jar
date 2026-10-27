package com.listener;

import com.annotation.Auth;
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

            // 1. Scan des controllers
            AnnotationUtil.scanControllersInPackage(basePackage,routes);

            // 2. Spring
            String configClass = context.getInitParameter("springConfig");
            Class<?> clazz = Class.forName(configClass);
            AnnotationConfigApplicationContext springContext =
                new AnnotationConfigApplicationContext(clazz);

            // 3. prefix / suffix
            context.setAttribute("prefix", context.getInitParameter("prefix"));
            context.setAttribute("suffix", context.getInitParameter("suffix"));

            // 4. Sprint 10 : authSessionKey
            String authSessionKey = context.getInitParameter("authSessionKey");
            context.setAttribute("authSessionKey", authSessionKey);

            // 4.bis Validation : si @Auth est utilisée mais authSessionKey absente
            if (authSessionKey == null || authSessionKey.trim().isEmpty()) {
                boolean anyAuth = false;
                for (RouteMapping r : routes.values()) {
                    if (r.getMethod().isAnnotationPresent(Auth.class)) {
                        anyAuth = true;
                        break;
                    }
                }
                if (anyAuth) {
                    throw new RuntimeException(
                        "La cle 'authSessionKey' doit etre configuree dans web.xml "
                        + "car des methodes utilisent @Auth"
                    );
                }
            }

            // 5. Stockage
            context.setAttribute("routes", routes);
            context.setAttribute("springContext", springContext);

            System.out.println("Framework + Spring initialise");

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce){
        ServletContext context = sce.getServletContext();
        AnnotationConfigApplicationContext springContext =
            (AnnotationConfigApplicationContext) context.getAttribute("springContext");
        if(springContext != null){
            springContext.close();
        }
    }
}