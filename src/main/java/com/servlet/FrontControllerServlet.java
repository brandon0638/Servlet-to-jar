package com.servlet;

import com.model.ModelAndView;
import com.util.AnnotationUtil;
import com.util.RouteMapping;
import com.util.UrlMethod;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import org.springframework.context.ApplicationContext;


public class FrontControllerServlet extends HttpServlet {


    private Map<UrlMethod,RouteMapping> routes;
    private String prefix;
    private String suffix;
    private ApplicationContext springContext;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();


        routes = (Map<UrlMethod,RouteMapping>)context.getAttribute("routes");
        springContext = (ApplicationContext)context.getAttribute("springContext");
        prefix = (String)context.getAttribute("prefix");
        suffix = (String)context.getAttribute("suffix");

        if(routes == null){

            throw new ServletException("Routes non initialisees");

        }
        
        System.out.println("FrontController initialise");

    }

    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        processRequest(request,response);

    }

    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        processRequest(request,response);

    }

    private void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String url = uri.substring(contextPath.length());
        String method = request.getMethod().toUpperCase();

        UrlMethod key = new UrlMethod(url,method);

        try {

            RouteMapping route = AnnotationUtil.getRoute(key,routes);

            Object result = AnnotationUtil.invoke(route,springContext);

            if(result instanceof ModelAndView){

                ModelAndView mv = (ModelAndView) result;

                for(Map.Entry<String,Object> entry : mv.getListAttributes().entrySet()){
                    request.setAttribute(entry.getKey(),entry.getValue());
                }

                String page = prefix + mv.getUrl() + suffix;
                RequestDispatcher dispatcher = request.getRequestDispatcher(page);
                dispatcher.forward(request,response);
            }else{
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println(result);
            }
        }catch(Exception e){
            throw new ServletException("Erreur invocation controller",e);
        }
    }
}