package com.util;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;
import com.annotation.WebAPI;
import com.annotation.Param;
import com.exception.UrlNotFoundException;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationUtil {

    private static List<Class<?>> classes = new ArrayList<>();

    public static void scanPackage(String basePackage) throws Exception {

        classes = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String path = basePackage.replace(".", "/");

        Enumeration<URL> resources = loader.getResources(path);

        while(resources.hasMoreElements()) {

            URL resource = resources.nextElement();

            File directory = new File(resource.toURI());

            classes.addAll(findClasses(directory, basePackage)
            );
        }
    }

    private static List<Class<?>> findClasses(File directory,String packageName) throws Exception {

        List<Class<?>> result = new ArrayList<>();

        if(!directory.exists()) {
            return result;
        }

        File[] files = directory.listFiles();

        if(files == null) {
            return result;
        }

        for(File file : files) {

            if(file.getName().endsWith(".class")) {

                String className = packageName + "." + file.getName().replace(".class", "");

                result.add(Class.forName(className));
            }
        }

        return result;
    }

    public static void scanControllersInPackage(String basePackage,Map<UrlMethod, RouteMapping> routes) throws Exception {

        scanPackage(basePackage);

        for(Class<?> c : classes) {

            if(c.isAnnotationPresent(Controllerako.class)) {

                scanMethod(routes, c);
            }
        }
    }

    private static void scanMethod(Map<UrlMethod, RouteMapping> routes,Class<?> controller) {

        for(Method method : controller.getDeclaredMethods()) {

            if(method.isAnnotationPresent(UrlMapping.class)) {

                UrlMapping mapping = method.getAnnotation(UrlMapping.class);

                UrlMethod key = new UrlMethod(mapping.value(),mapping.method());

                RouteMapping route = new RouteMapping(controller,method);

                if(routes.containsKey(key)) {

                    throw new RuntimeException("Route deja utilisée : " + key.getMethod() + " " + key.getUrl());
                }

                routes.put(key, route);

                System.out.println("Route enregistree : " + key.getMethod() + " " + key.getUrl() + " -> " + controller.getSimpleName() + "." + method.getName());
            }
        }
    }

    
    public static Object invoke(RouteMapping route,ApplicationContext springContext,HttpServletRequest request) throws Exception {

        Object controller = springContext.getBean(route.getController());

        Method method = route.getMethod();
        Parameter[] parameters = method.getParameters();

        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            Parameter parameter = parameters[i];

            if (!parameter.isAnnotationPresent(Param.class)) {
                throw new RuntimeException(
                    "Le parametre '" + parameter.getName()
                    + "' de la methode " + method.getName()
                    + " doit utiliser @Param"
                );
            }

            Param param = parameter.getAnnotation(Param.class);
            String parameterName = param.value();
            Class<?> parameterType = parameter.getType();

            if (isSimpleType(parameterType)) {

                // Sprint 7 : binding d'une valeur simple
                String value = request.getParameter(parameterName);

                arguments[i] = convertParameter(value, parameterType);

            } else {

                // Sprint 7-bis : binding d'un objet simple
                arguments[i] = bindObject(
                    parameterName,
                    parameterType,
                    request
                );
            }
        }

        return method.invoke(controller, arguments);
    }

    
    private static Object convertParameter(String value,Class<?> type) {

        
        if(value == null) {

            if(type.isPrimitive()) {

                throw new IllegalArgumentException("Impossible de donner null " + "au type primitif " + type.getSimpleName());
            }

            return null;
        }

       
        if(type == String.class) {
            return value;
        }

        
        if(type == Integer.class) {
            return Integer.valueOf(value);
        }

        
        if(type == int.class) {
            return Integer.parseInt(value);
        }

        if(type == Long.class) {
            return Long.valueOf(value);
        }

        
        if(type == long.class) {
            return Long.parseLong(value);
        }

        
        if(type == Double.class) {
            return Double.valueOf(value);
        }

       
        if(type == double.class) {
            return Double.parseDouble(value);
        }

        
        if(type == Float.class) {
            return Float.valueOf(value);
        }

        
        if(type == float.class) {
            return Float.parseFloat(value);
        }

        
        if(type == Boolean.class) {
            return Boolean.valueOf(value);
        }

        
        if(type == boolean.class) {
            return Boolean.parseBoolean(value);
        }

        
        if(type == Short.class) {
            return Short.valueOf(value);
        }

       
        if(type == short.class) {
            return Short.parseShort(value);
        }

      
        if(type == Byte.class) {
            return Byte.valueOf(value);
        }

        
        if(type == byte.class) {
            return Byte.parseByte(value);
        }

        if(type == Character.class) {

            if(value.isEmpty()) {
                return null;
            }

            return value.charAt(0);
        }

        
        if(type == char.class) {

            if(value.isEmpty()) {

                throw new IllegalArgumentException("Impossible de convertir une " + "valeur vide en char");
            }

            return value.charAt(0);
        }

        throw new IllegalArgumentException("Type non supporte pour le binding : " + type.getName());
    }

    public static RouteMapping getRoute(UrlMethod key,Map<UrlMethod, RouteMapping> routes) {

        RouteMapping route = routes.get(key);

        if(route == null) {

            StringBuilder msg = new StringBuilder();

            msg.append("Route inexistante : ").append(key.getMethod()).append(" ").append(key.getUrl()).append("\n\nRoutes disponibles :\n");

            for(Map.Entry<UrlMethod,RouteMapping> e : routes.entrySet()) {

                msg.append(e.getKey().getMethod()).append(" ").append(e.getKey().getUrl()).append(" -> ").append(e.getValue().getController().getSimpleName()).append(".").append(e.getValue().getMethod().getName()).append("\n");
            }

            throw new UrlNotFoundException(msg.toString());
        }

        return route;
    }

   
    public static boolean isMethodReturnJSON(Method method) {

        return method.isAnnotationPresent(WebAPI.class);
    }

    public static String toJSON(Object object) {

        ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);

        } catch(JsonProcessingException e) {

            throw new RuntimeException("Erreur lors de la conversion en JSON",e);
        }
    }

    private static boolean isSimpleType(Class<?> type) {

        return type == String.class
            || type == Integer.class
            || type == int.class
            || type == Long.class
            || type == long.class
            || type == Double.class
            || type == double.class
            || type == Float.class
            || type == float.class
            || type == Boolean.class
            || type == boolean.class
            || type == Short.class
            || type == short.class
            || type == Byte.class
            || type == byte.class
            || type == Character.class
            || type == char.class;
    }

    private static Object bindObject(String prefix,Class<?> type,HttpServletRequest request) throws Exception {

        
        Object object = type.getDeclaredConstructor().newInstance();

        // Examiner les propriétés JavaBean : id, nom, etc.
        PropertyDescriptor[] properties =
            Introspector.getBeanInfo(type, Object.class)
                        .getPropertyDescriptors();

        for (PropertyDescriptor property : properties) {

            Method setter = property.getWriteMethod();

            // Ignorer les propriétés qui n'ont pas de setter
            if (setter == null) {
                continue;
            }

            String propertyName = property.getName();

            
            String parameterName = prefix + "." + propertyName;

            String value = request.getParameter(parameterName);

            // Si le paramètre n'a pas été envoyé, on garde la valeur par défaut de la propriété.
            if (value == null) {
                continue;
            }

            Class<?> propertyType = property.getPropertyType();

            
            if (!isSimpleType(propertyType)) {
                throw new IllegalArgumentException(
                    "La propriete '" + propertyName
                    + "' de " + type.getSimpleName()
                    + " n'est pas un type simple. "
                    + "Les objets imbriques seront traites dans une prochaine etape."
                );
            }

            Object convertedValue = convertParameter(value, propertyType);

            // Appeler automatiquement le setter, par exemple setNom(...)
            setter.invoke(object, convertedValue);
        }

        return object;
    }
}