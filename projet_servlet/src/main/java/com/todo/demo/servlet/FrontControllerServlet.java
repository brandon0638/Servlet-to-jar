package com.todo.demo.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class FrontControllerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupérer l'URL (AFFICHER)
        StringBuffer requestURL = request.getRequestURL();
        System.out.println("=== nouvelle requete ====");
        System.out.println("URL: " + requestURL);
        
        // 2. Récupérer les paramètres (SANS LES AFFICHER)
        Map<String, String[]> paramMap = request.getParameterMap();
        
        
        
        // 3. Afficher les paramètres de contexte
        String appName = getServletContext().getInitParameter("app-name");
        String appVersion = getServletContext().getInitParameter("version");
        
        if(appName != null){
            System.out.println("Context param app-name: " + appName);
        }
        if(appVersion != null){
            System.out.println("Contexte param version: " + appVersion);
        }
        
        // 4. Réponse HTML (juste l'URL, PAS les paramètres)
        response.setContentType("text/html");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html>");
        response.getWriter().println("<head><title>FrontController Servlet</title></head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<h1>FrontController Servlet</h1>");
        response.getWriter().println("<p>URL : " + requestURL + "</p>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    }
}