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

        ServletContext context = getServletContext();

        String basePackage = context.getInitParameter("package");

        controllers = AnnotationUtil.getControllers(basePackage);

        System.out.println("Package : " + basePackage);
        System.out.println("Controllers trouvés : " + controllers.size());
        System.out.println("Classes trouvées: " + AnnotationUtil.getClasses(basePackage).size());
        System.out.println("INIT FRONT CONTROLLER OK");
System.out.println("PACKAGE = " + basePackage);
System.out.println("CLASSES = " + AnnotationUtil.getClasses(basePackage).size());
System.out.println("CONTROLLERS = " + controllers.size());
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
            // Récupérer uniquement le nom simple de la classe (sans le package)
            String simpleName = c.getSimpleName();
            out.println("<li>" + simpleName + "</li>");
        }

        out.println("</ul>");
        
    }

    


}