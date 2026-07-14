package com.util;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;
import com.exception.UrlNotFoundException;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import org.springframework.context.ApplicationContext;

public class AnnotationUtil {

    private static List<Class<?>> classes = new ArrayList<>();

    public static void scanPackage(String basePackage) throws Exception {

        classes = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String path = basePackage.replace(".", "/");

        Enumeration<URL> resources = loader.getResources(path);

        while(resources.hasMoreElements()){

            URL resource = resources.nextElement();

            File directory = new File(resource.toURI());

            classes.addAll(findClasses(directory, basePackage));
        }
    }


    private static List<Class<?>> findClasses(File directory,String packageName) throws Exception{

        List<Class<?>> result = new ArrayList<>();

        if(!directory.exists())
            return result;


        File[] files = directory.listFiles();

        if(files == null)
            return result;


        for(File file : files){

            if(file.getName().endsWith(".class")){

                String className = packageName + "." + file.getName().replace(".class","");

                result.add(Class.forName(className));

            }
        }

        return result;
    }


    public static void scanControllersInPackage(String basePackage,Map<UrlMethod,RouteMapping> routes) throws Exception {
        scanPackage(basePackage);

        for(Class<?> c : classes){

            if(c.isAnnotationPresent(Controllerako.class)){

                scanMethod(routes,c);

            }
        }
    }


    private static void scanMethod(Map<UrlMethod,RouteMapping> routes,Class<?> controller){


        for(Method method : controller.getDeclaredMethods()){

            if(method.isAnnotationPresent(UrlMapping.class)){
                UrlMapping mapping = method.getAnnotation(UrlMapping.class);
                UrlMethod key = new UrlMethod(mapping.value(),mapping.method());


                RouteMapping route = new RouteMapping(controller,method);

                if(routes.containsKey(key)){

                    throw new RuntimeException("Route déjà utilisée : " + key.getMethod() + " " + key.getUrl());
                }


                routes.put(key,route);


                System.out.println(
                        "Route enregistrée : "
                        + key.getMethod()
                        + " "
                        + key.getUrl()
                        + " -> "
                        + controller.getSimpleName()
                        + "." + method.getName()
                );

            }
        }
    }


    public static Object invoke(RouteMapping route,ApplicationContext springContext) throws Exception {


        Object controller = springContext.getBean(route.getController());


        return route.getMethod()
                    .invoke(controller);

    }


    public static RouteMapping getRoute(UrlMethod key,Map<UrlMethod,RouteMapping> routes){

        RouteMapping route = routes.get(key);

        if(route == null){

            StringBuilder msg = new StringBuilder();
            msg.append("Route inexistante : ")
               .append(key.getMethod())
               .append(" ")
               .append(key.getUrl())
               .append("\n\nRoutes disponibles :\n");


            for(Map.Entry<UrlMethod,RouteMapping> e : routes.entrySet()){

                msg.append(e.getKey().getMethod())
                   .append(" ")
                   .append(e.getKey().getUrl())
                   .append(" -> ")
                   .append(e.getValue().getController().getSimpleName())
                   .append(".")
                   .append(e.getValue().getMethod().getName())
                   .append("\n");
            }


            throw new UrlNotFoundException(msg.toString());
        }


        return route;
    }
}