package com.servlet;

import com.util.*;
import com.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;


public class FrontControllerServlet extends HttpServlet{

    private List<Class<?>> controllers;
    private Map<UrlMethod, Method> urlMappings;

   @Override
    public void init() {

        ServletContext context = getServletContext();

        String basePackage = context.getInitParameter("package");

        controllers = AnnotationUtil.getControllers(basePackage);
        urlMappings = AnnotationUtil.getUrlMappings(controllers);

        
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        processRequest(request,response);
        

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        processRequest(request,response);
        
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String url = uri.substring(contextPath.length());
        String httpMethod = request.getMethod();

        out.println("<h1>FRONT CONTROLLER</h1>");
        out.println("<p><b>URL complete :</b> " + request.getRequestURL() + "</p>");
        out.println("<p><b>Route :</b> " + url + "</p>");
        out.println("<p><b>Methode HTTP :</b> " + httpMethod + "</p>");

     
        out.println("<h2>Controllers detectes</h2>");
        out.println("<ul>");
        for (Class<?> c : controllers) {
            out.println("<li>" + c.getSimpleName() + "</li>");
        }
        out.println("</ul>");

    
        UrlMethod key = new UrlMethod(url, httpMethod);

        if (urlMappings.containsKey(key)) {

            Method m = urlMappings.get(key);

            out.println("<hr>");
            out.println("<h2>ROUTE TROUVeE</h2>");
            out.println("<p><b>Controller :</b> " + m.getDeclaringClass().getSimpleName() + "</p>");
            out.println("<p><b>Methode :</b> " + m.getName() + "</p>");
            out.println("<p><b>URL :</b> " + url + "</p>");
            out.println("<p><b>HTTP :</b> " + httpMethod + "</p>");

            try {

                Object controller = m.getDeclaringClass()
                                    .getDeclaredConstructor()
                                    .newInstance();

                Object result = m.invoke(controller);

                out.println("Valeur retournee: " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            out.println("<hr>");
            out.println("<h2>ROUTE INEXISTANTE</h2>");
            out.println("<p style='color:red;'><b>" + url + "</b></p>");

            out.println("<h3>Routes disponibles :</h3>");
            out.println("<ul>");

            for (UrlMethod u : urlMappings.keySet()) {
                out.println("<li>" + u.getMethod() + " " + u.getUrl() + "</li>");
            }

            out.println("</ul>");

        }
        
    }

    


}