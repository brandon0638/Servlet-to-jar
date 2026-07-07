package com.servlet;

import com.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> controllers;
    private Map<UrlMethod, Method> urlMappings;


    @Override
    public void init() throws ServletException {

        // Récupération du contexte global
        ServletContext context = getServletContext();

        // Récupération des données préparées par le Listener
        controllers = (List<Class<?>>) context.getAttribute("controllers");

        urlMappings = (Map<UrlMethod, Method>) context.getAttribute("urlMappings");


        if (controllers == null || urlMappings == null) {
            throw new ServletException(
                "Erreur : Listener non initialise ou donnees manquantes"
            );
        }

        System.out.println("FrontController initialise !");
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        processRequest(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        processRequest(request, response);
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();


        String contextPath = request.getContextPath();

        String uri = request.getRequestURI();

        String url = uri.substring(contextPath.length());


        String httpMethod = request.getMethod()
                                   .trim()
                                   .toUpperCase();



        out.println("<h1>FRONT CONTROLLER</h1>");

        out.println("<p><b>Route :</b> " + url + "</p>");

        out.println("<p><b>Methode HTTP :</b> " + httpMethod + "</p>");



        // Affichage des controllers détectés

        out.println("<h2>Controllers detectes</h2>");

        out.println("<ul>");

        for(Class<?> c : controllers){

            out.println("<li>" + c.getSimpleName() + "</li>");
        }

        out.println("</ul>");



        UrlMethod key = new UrlMethod(url, httpMethod);



        if(urlMappings.containsKey(key)){


            Method method = urlMappings.get(key);


            out.println("<hr>");
            out.println("<h2>ROUTE TROUVEE</h2>");

            out.println("<p><b>Controller :</b> " + method.getDeclaringClass().getSimpleName() + "</p>");


            out.println("<p><b>Methode :</b> " + method.getName() + "</p>");



            try {


                Object controller =
                    method.getDeclaringClass()
                          .getDeclaredConstructor()
                          .newInstance();



                Object result = method.invoke(controller);



                if(result != null){

                    out.println("<p><b>Retour :</b> " + result + "</p>");
                }



            } catch(Exception e){


                out.println("<p style='color:red;'>Erreur invocation</p>");


                out.println("<pre>");

                e.printStackTrace(out);

                out.println("</pre>");
            }



        }else{


            out.println("<hr>");

            out.println("<h2 style='color:red;'>ROUTE INEXISTANTE</h2>");



            out.println("<h3>Routes disponibles :</h3>");

            out.println("<ul>");


            for(UrlMethod u : urlMappings.keySet()){


                out.println("<li>" + u.getMethod() + " " + u.getUrl() + "</li>");

            }


            out.println("</ul>");

        }

    }

}