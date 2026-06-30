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

        out.println("<h1>URL actuelle</h1>");
        out.println("<p>" + request.getRequestURL() + "</p>");

        out.println("<h2>Listes des controllers</h2>");
        out.println("<ul>");
        for (Class<?> c : controllers) {
            out.println("<li>" + c.getSimpleName() + "</li>");
        }
        out.println("</ul>");

        try {
            if (urlMappings.containsKey(url)) {
                UrlMethod um = urlMappings.get(url);
                Method m = um.getMethod();
                String expectedMethod = um.getHttpMethod();

                out.println("<hr>");
                out.println("<h3>URL demandee : " + url + "</h3>");
                out.println("<ul>");
                out.println("<li><b>Methode trouvee :</b> " + m.getDeclaringClass().getSimpleName() + "." + m.getName() + "()</li>");
                out.println("<li><b>Methode HTTP attendue :</b> " + expectedMethod + "</li>");
                out.println("<li><b>Methode HTTP recue :</b> " + httpMethod + "</li>");
                out.println("</ul>");

                //AFFICHER LE FORMULAIRE POUR LA RACINE
                if (url.equals("/")) {
                    out.println("<hr>");
                    out.println("<h3>Formulaire POST</h3>");
                    out.println("<form action=\"" + request.getContextPath() + "/emp/new\" method=\"POST\">");
                    out.println("Nom : <input type=\"text\" name=\"nom\" placeholder=\"entre ce que tu veux\">");
                    out.println("<input type=\"submit\" value=\"Creer\">");
                    out.println("</form>");
                    
                }
                

                // Vérification de la méthode HTTP
                if (!expectedMethod.equalsIgnoreCase(httpMethod) && !expectedMethod.equalsIgnoreCase("ALL")) {
                    throw new Exception("Methode HTTP incorrecte. Attendu: " + expectedMethod + ", Recu: " + httpMethod);
                }

                //EXÉCUTION DE LA MÉTHODE ET RÉCUPÉRATION DE LA VALEUR RETOURNÉE
                Object result = null;
                try {
                    Object controllerInstance = m.getDeclaringClass().newInstance();
                    result = m.invoke(controllerInstance);
                } catch (Exception ex) {
                    out.println("<p style='color:orange'>Erreur execution: " + ex.getMessage() + "</p>");
                }

                out.println("<h4 style='color:green'>SUCCES</h4>");
                out.println("<p style='color:green'>Methode executee avec succes</p>");

                //AFFICHER LA VALEUR RETOURNÉE
                if (result != null) {
                    out.println("<hr>");
                    out.println("<h4 style='color:blue'>Valeur retournee :</h4>");
                    out.println("<div style='background: #f0f8ff; padding: 15px; border-radius: 5px; border: 1px solid #b0d4e3;'>");
                    out.println("<b>" + result.toString() + "</b>");
                    out.println("</div>");
                } else {
                    out.println("<p style='color:gray'>Aucune valeur retournee (void)</p>");
                }
                

            } else {
                throw new Exception("URL inexistante: " + url);
            }
        } catch (Exception e) {
            out.println("<hr>");
            out.println("<h4 style='color:red'>ERREUR</h4>");
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