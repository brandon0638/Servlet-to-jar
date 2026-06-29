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

        String url = request.getRequestURI().substring(request.getContextPath().length());
        String httpMethod = request.getMethod();

        try {
            if (urlMappings.containsKey(url)) {
                UrlMethod um = urlMappings.get(url);
                Method m = um.getMethod();
                String expectedMethod = um.getHttpMethod();

                // Afficher les infos de base
                out.println("<h1>URL actuelle</h1>");
                out.println("<p>" + request.getRequestURL() + "</p>");

                out.println("<h2>Listes des controllers</h2>");
                out.println("<ul>");
                for (Class<?> c : controllers) {
                    out.println("<li>" + c.getSimpleName() + "</li>");
                }
                out.println("</ul>");

                out.println("<hr>");
                out.println("<h3>URL demandee : " + url + "</h3>");
                out.println("<ul>");
                out.println("<li><b>Methode trouvee :</b> " + m.getDeclaringClass().getSimpleName() + "." + m.getName() + "()</li>");
                out.println("<li><b>Methode HTTP attendue :</b> " + expectedMethod + "</li>");
                out.println("<li><b>Methode HTTP recue :</b> " + httpMethod + "</li>");
                out.println("</ul>");

                // ===== SI URL = "/" AFFICHER LE FORMULAIRE =====
                if (url.equals("/")) {
                    out.println("<hr>");
                    out.println("<h3>Formulaire POST - Créer un employé</h3>");
                    out.println("<form action=\"" + request.getContextPath() + "/emp/new\" method=\"POST\">");
                    out.println("Nom : <input type=\"text\" name=\"nom\" placeholder=\"Nom de l'employé\">");
                    out.println("<input type=\"submit\" value=\"Créer\">");
                    out.println("</form>");
                    out.println("<hr>");
                    out.println("<h3>Navigation (GET)</h3>");
                    out.println("<ul>");
                    out.println("<li><a href=\"" + request.getContextPath() + "/emp/list\">Liste des employés</a></li>");
                    out.println("<li><a href=\"" + request.getContextPath() + "/emp/delete\">Supprimer un employé</a></li>");
                    out.println("</ul>");
                }
                // =================================================

                // Vérification de la méthode HTTP
                if (!expectedMethod.equalsIgnoreCase(httpMethod) && !expectedMethod.equalsIgnoreCase("ALL")) {
                    throw new Exception("Methode HTTP incorrecte. Attendu: " + expectedMethod + ", Recu: " + httpMethod);
                }

                out.println("<h2 style='color:green'>SUCCES</h2>");
                out.println("<p style='color:green'>Methode executee avec succes</p>");

                // Exécution de la méthode
                try {
                    Object controllerInstance = m.getDeclaringClass().newInstance();
                    m.invoke(controllerInstance);
                } catch (Exception ex) {
                    out.println("<p style='color:orange'>Erreur execution: " + ex.getMessage() + "</p>");
                }

            } else {
                throw new Exception("URL inexistante: " + url);
            }
        } catch (Exception e) {
            out.println("<hr>");
            out.println("<h2 style='color:red'>ERREUR</h2>");
            out.println("<p><b>" + e.getMessage() + "</b></p>");
            out.println("<hr>");
            out.println("<h3>URLs disponibles</h3>");
            out.println("<ul>");
            for (String u : urlMappings.keySet()) {
                UrlMethod um = urlMappings.get(u);
                out.println("<li>" + u + " (" + um.getHttpMethod() + ")</li>");
            }
            out.println("</ul>");

        }
        
    }

    


}