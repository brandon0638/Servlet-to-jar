package com.listener;

import com.util.*;


import javax.servlet.*;
import java.lang.reflect.Method;
import java.util.*;

public class AppContextListener implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent sce){
        ServletContext context = sce.getServletContext();

        //recupere package depuis web.xml
        String basePackage = context.getInitParameter("package");

        //scanne controller
        List<Class<?>> controllers = AnnotationUtil.getControllers(basePackage);

        //mapping
        Map<UrlMethod, Method> urlMappings = AnnotationUtil.getUrlMappings(controllers);

        //stocker dans le context
        context.setAttribute("controllers", controllers);
        context.setAttribute("urlMappings", urlMappings);

        System.out.println("Framework initialise via Listener !");
    }
    
}