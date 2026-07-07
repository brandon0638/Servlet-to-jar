package com.servlet;

import com.util.*;
import com.model.ModelAndView;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class FrontControllerServlet extends HttpServlet {


    private List<Class<?>> controllers;

    private Map<UrlMethod, Method> urlMappings;

    private String prefix;

    private String suffix;



    @Override
    public void init() throws ServletException {


        ServletContext context = getServletContext();


        // Récupération des données initialisées par le Listener

        controllers = (List<Class<?>>) context.getAttribute("controllers");


        urlMappings = (Map<UrlMethod, Method>) context.getAttribute("urlMappings");



        // Récupération prefix et suffix depuis web.xml

        prefix = context.getInitParameter("prefix");

        suffix = context.getInitParameter("suffix");



        if(controllers == null || urlMappings == null){

            throw new ServletException("Erreur : Listener non initialise");

        }


        System.out.println("FrontController initialise !");

    }




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        processRequest(request,response);

    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        processRequest(request,response);

    }





    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



        String contextPath = request.getContextPath();


        String uri = request.getRequestURI();


        String url = uri.substring(contextPath.length()).trim();



        String httpMethod =
            request.getMethod()
                   .trim()
                   .toUpperCase();




        UrlMethod key = new UrlMethod(url,httpMethod);

        if(urlMappings.containsKey(key)){



            Method method = urlMappings.get(key);

            try {



                // création du controller

                Object controller =
                    method.getDeclaringClass()
                          .getDeclaredConstructor()
                          .newInstance();




                // appel de la méthode

                Object result = method.invoke(controller);

                if(result instanceof ModelAndView){



                    ModelAndView mv = (ModelAndView) result;

                    for(String attributeName : mv.getAttribute().keySet()){

                        request.setAttribute(attributeName,mv.getAttribute().get(attributeName));

                    }


                    String page = prefix + mv.getUrl() + suffix;

                    RequestDispatcher dispatcher =request.getRequestDispatcher(page);

                    dispatcher.forward(request,response);



                }else{


                    // Cas où le controller retourne autre chose

                    response.setContentType("text/html;charset=UTF-8");


                    PrintWriter out = response.getWriter();


                    out.println(result);


                }





            }catch(Exception e){


                throw new ServletException("Erreur invocation controller", e);

            }





        }else{



            response.setContentType("text/html;charset=UTF-8");


            PrintWriter out = response.getWriter();



            out.println("<h1>404 NOT FOUND</h1>");

            out.println("<p>Route inexistante : " + url + "</p>");



            out.println("<h3>Routes disponibles :</h3>");

            out.println("<ul>");



            for(UrlMethod u : urlMappings.keySet()){


                out.println("<li>" + u.getMethod() + " " + u.getUrl() + "</li>");


            }


            out.println("</ul>");

        }



    }

}