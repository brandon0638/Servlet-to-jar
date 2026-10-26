package com.servlet;

import com.model.ModelAndView;
import com.util.AnnotationUtil;
import com.util.RouteMapping;
import com.util.UrlMethod;
import com.exception.UrlNotFoundException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.MultipartConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.context.ApplicationContext;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, RouteMapping> routes;

    private String prefix;

    private String suffix;

    private ApplicationContext springContext;

    @Override
    public void init() throws ServletException {

        routes = (Map<UrlMethod, RouteMapping>) getServletContext().getAttribute("routes");

        prefix = (String) getServletContext().getAttribute("prefix");

        suffix =
                (String) getServletContext().getAttribute("suffix");

        springContext =
                (ApplicationContext) getServletContext().getAttribute("springContext");

        if(routes == null) {

            throw new ServletException("Routes non initialisees");
        }

        if(springContext == null) {

            throw new ServletException("SpringContext non initialise");
        }

        System.out.println("FrontController initialise");
    }

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

        processRequest(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

        processRequest(request,response);
    }

    private void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

        String contextPath = request.getContextPath();

        String uri = request.getRequestURI();

        String url = uri.substring(contextPath.length());

        String method = request.getMethod().toUpperCase();

        UrlMethod key = new UrlMethod(url,method);

        try {

           
            RouteMapping route = AnnotationUtil.getRoute(key,routes);

            
            Object result = AnnotationUtil.invoke(route,springContext,request);

            
            if(AnnotationUtil.isMethodReturnJSON(route.getMethod())) {

                String json = AnnotationUtil.toJSON(result);

                response.setContentType("application/json;charset=UTF-8");

                PrintWriter out = response.getWriter();

                out.print(json);

                out.flush();

                return;
            }

            
            if(result instanceof ModelAndView) {

                ModelAndView mv =
                        (ModelAndView) result;

                for(Map.Entry<String, Object> entry : mv.getListAttributes().entrySet()) {

                    request.setAttribute(entry.getKey(),entry.getValue());
                }

                String page = prefix + mv.getUrl() + suffix;

                RequestDispatcher dispatcher = request.getRequestDispatcher(page);

                dispatcher.forward(request,response);

            } else {

               
                response.setContentType("text/html;charset=UTF-8");

                PrintWriter out = response.getWriter();

                out.println(result);
            }

        } catch (UrlNotFoundException e) {

            // 404 explicite
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println(e.getMessage());

        }catch(Exception e) {

            throw new ServletException("Erreur invocation controller",e);
        }
    }
}