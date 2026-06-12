package com.todo.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

// @WebServlet(name = "FrontControllerServlet", urlPatterns = "/*")
public class FrontControllerServlet extends HttpServlet{

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        processRequest(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        processRequest(request,response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        //recuperation url
        StringBuffer requestURL = request.getRequestURL();
        System.out.println("=== nouvelle requete ====");
        System.out.println("URL: " + requestURL + " .");

        //afficher parametre requete
        Map<String, String[]> paramMap = request.getParameterMap();

        if(paramMap != null && !paramMap.isEmpty()){
            System.out.println("Request parameters recus: ");
            for(Map.Entry<String, String[]> entry : paramMap.entrySet()){
                String paramName = entry.getKey();
                String[] paramValues = entry.getValue();

                if(paramValues != null && paramValues.length > 0){

                    System.out.println(" - " + paramName + " = " + String.join(", ", paramValues));
                }else{
                    System.out.println(" - " + paramName + " = (valeur vide)");
                }
            }
        }else{
            System.out.println("Aucun request param recu");
        }

        //Afficher les parametre de contexte
        String appName = getServletContext().getInitParameter("app-name");
        String appVersion = getServletContext().getInitParameter("version");

        if(appName != null){
            System.out.println("Context param app-name: " + appName);

        }
        if(appVersion != null){
            System.out.println("Contexte param version: " + appVersion);
        }

        //reponse html
        response.setContentType("text/html");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html>");
        response.getWriter().println("<head><title>FrontController Servlet</title></head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<h1>FrontController Servlet</h1>");
        response.getWriter().println("<p>URL : " + requestURL + "</p>");
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");

        //affihcer parametre dans html
        if (paramMap != null && !paramMap.isEmpty()) {
            response.getWriter().println("<h2>Paramètres reçus :</h2>");
            response.getWriter().println("<ul>");
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                String paramName = entry.getKey();
                String[] paramValues = entry.getValue();
                if (paramValues != null && paramValues.length > 0) {
                    response.getWriter().println("<li><strong>" + paramName + "</strong> = " + 
                        String.join(", ", paramValues) + "</li>");
                }
            }
            response.getWriter().println("</ul>");
        }
        
        response.getWriter().println("</body>");
        response.getWriter().println("</html>");
    
    }
}