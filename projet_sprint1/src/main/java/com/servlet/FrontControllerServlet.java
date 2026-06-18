package com.servlet;

import com.util.AnnotationUtil;
import com.annotation.Controllerako;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class FrontControllerServlet extends HttpServlet{

    private List<Class<?>> controllers;

   @Override
    public void init() {

        String basePackage = "com.test.controller";

        List<Class<?>> classes = AnnotationUtil.getClasses(basePackage);

        controllers = AnnotationUtil.getControllers(basePackage);

        System.out.println("Framework initialisé");
        System.out.println("Controllers trouvés: " + controllers.size());
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

        out.println("<h2>Controllers détectés</h2>");
        out.println("<ul>");

        for (Class<?> c : controllers) {
            out.println("<li>" + c.getName() + "</li>");
        }

        out.println("</ul>");
    }

    


}