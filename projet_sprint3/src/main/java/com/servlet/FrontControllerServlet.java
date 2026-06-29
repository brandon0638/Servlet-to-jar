package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.AnnotationUtil;
import com.util.UrlMethod;


public class FrontControllerServlet extends HttpServlet{

    private List<Class<?>> controllers;
    private Map<String, UrlMethod> urlMappings;

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
        String httpMethod = request.getMethod(); // ← NOUVEAU : Récupérer la méthode HTTP
        
        try {
            if (urlMappings.containsKey(url)) {
                UrlMethod um = urlMappings.get(url); // ← CHANGÉ : UrlMethod au lieu de Method
                Method m = um.getMethod(); // ← NOUVEAU : Récupérer la méthode Java
                String expectedMethod = um.getHttpMethod(); // ← NOUVEAU : Méthode HTTP attendue
                
                out.println("<hr>");
                out.println("<h3>URL demandee : " + url + "</h3>");
                out.println("<hr>");
                out.println("<ul>");
                out.println("<li>" + m.getDeclaringClass().getSimpleName() + "." + m.getName() + "() </li>");
                out.println("</ul>");
                
                // NOUVEAU : Vérification de la méthode HTTP 
                if (!expectedMethod.equalsIgnoreCase(httpMethod) && !expectedMethod.equalsIgnoreCase("ALL")) {
                    throw new Exception("Methode HTTP incorrecte. Attendu: " + expectedMethod + ", Reçu: " + httpMethod);
                }
               
                
                //NOUVEAU : Exécution de la méthode
                try {
                    Object controllerInstance = m.getDeclaringClass().newInstance();
                    m.invoke(controllerInstance);
                    out.println("<p style='color:green'> Methode executee avec succes</p>");
                } catch (Exception ex) {
                    out.println("<p style='color:orange'> Erreur execution: " + ex.getMessage() + "</p>");
                }
               

            } else {
                throw new Exception("<hr><h3>URL inexistante: " + url + "</h3>");
            }
        } catch (Exception e) {
            out.println("<p><b>" + e.getMessage() + "</b></p>");
            out.println("<hr>");
            out.println("<h3>URLs disponibles</h3>");
            out.println("<ul>");
            for (String u : urlMappings.keySet()) {
                UrlMethod um = urlMappings.get(u); // ← NOUVEAU : Pour afficher la méthode HTTP
                out.println("<li>" + u + " (" + um.getHttpMethod() + ")</li>"); // ← MODIFIÉ : Ajout de la méthode HTTP
            }
            out.println("</ul>");

        }
        
    }

    


}