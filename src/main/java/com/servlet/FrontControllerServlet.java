package com.servlet;

import com.util.AnnotationUtil;
import com.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;


public class FrontControllerServlet extends HttpServlet{

    private List<Class<?>> controllers;
    private Map<String, Method> urlMappings;

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

        out.println("<h1>URL actuelle</h1>");
        out.println("<p>" + request.getRequestURL() + "</p>");

        out.println("<h2>Listes des controllers</h2>");
        out.println("<ul>");

        for (Class<?> c : controllers) {
            
            String simpleName = c.getSimpleName();
            out.println("<li>" + simpleName + "</li>");
        }

        out.println("</ul>");
        
        
        String url = request.getRequestURI().substring(request.getContextPath().length());
        
        try {
            if (urlMappings.containsKey(url)) {
                Method m = urlMappings.get(url);
                out.println("<hr>");
                out.println("<h3>URL demandee : " + url + "</h3>");
                out.println("<hr>");
                out.println("<ul>");
                out.println("<li>" + m.getDeclaringClass().getSimpleName() + "." + m.getName() + "() </li>");
                out.println("</ul>");

            } else {
                

                throw new Exception("<hr><h3>URL inexistante: " + url + "</h3>");
                

            }
        } catch (Exception e) {
            out.println("<p><b>" + e.getMessage() + "</b></p>");
            out.println("<hr>");
            out.println("<h3>URLs disponibles</h3>");
            out.println("<ul>");
            for (String u : urlMappings.keySet()) {
                
            out.println("<li>" + u + "</li>");
            }
            out.println("</ul>");

        }
        
    }

    


}