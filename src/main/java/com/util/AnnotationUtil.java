package com.util;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;
import com.annotation.WebAPI;
import com.annotation.Param;
import com.exception.UrlNotFoundException;
import com.model.MapFile;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.Part;
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

       
        PropertyDescriptor[] properties =
            Introspector.getBeanInfo(type, Object.class)
                        .getPropertyDescriptors();

        for (PropertyDescriptor property : properties) {

            Method setter = property.getWriteMethod();

            // Ignorer les propriétés sans setter
            if (setter == null) {
                continue;
            }

            String propertyName = property.getName();
            String parameterName = prefix + "." + propertyName;
            Class<?> propertyType = property.getPropertyType();

            // CAS 1 : propriété simple (String, Integer, int...)
            if (isSimpleType(propertyType)) {

                String value = request.getParameter(parameterName);

                if (value == null) {
                    continue;
                }

                Object convertedValue =
                    convertParameter(value, propertyType);

                setter.invoke(object, convertedValue);
            }

            // CAS 2 : propriété de type MapFile (upload de fichier)
            else if (propertyType == MapFile.class) {

                MapFile mapFile = bindFile(parameterName, request);

                if (mapFile != null) {
                    setter.invoke(object, mapFile);
                }
            }

            // CAS 3 : propriété de type List
            else if (List.class.isAssignableFrom(propertyType)) {

                if (!hasParametersWithPrefix(
                        request, parameterName + "[")) {
                    continue;
                }

                List<Object> list = bindList(
                    parameterName,
                    setter,
                    request
                );

                setter.invoke(object, list);
            }

            // CAS 4 : objet imbriqué
            else {

                if (!hasParametersWithPrefix(
                        request, parameterName + ".")) {
                    continue;
                }

                Object nestedObject = bindObject(
                    parameterName,
                    propertyType,
                    request
                );

                setter.invoke(object, nestedObject);
            }
        }

        return object;
    }



    
    private static boolean hasParametersWithPrefix(HttpServletRequest request,String prefix) {

        for (String parameterName : request.getParameterMap().keySet()) {
            if (parameterName.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    
    private static List<Object> bindList(String prefix,Method setter,HttpServletRequest request) throws Exception {

        
        Type genericType = setter.getGenericParameterTypes()[0];

        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                "La liste doit declarer son type generique : List<Adresse>"
            );
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;

        Type elementType = parameterizedType.getActualTypeArguments()[0];

        if (!(elementType instanceof Class<?>)) {
            throw new IllegalArgumentException(
                "Le type des elements de la liste n'est pas supporte"
            );
        }

        Class<?> elementClass = (Class<?>) elementType;

        
        Set<Integer> indices = new TreeSet<>();

        String indexPrefix = prefix + "[";

        for (String parameterName : request.getParameterMap().keySet()) {

            if (!parameterName.startsWith(indexPrefix)) {
                continue;
            }

            int start = indexPrefix.length();
            int end = parameterName.indexOf("]", start);

            if (end == -1) {
                continue;
            }

            String indexText = parameterName.substring(start, end);

            try {
                int index = Integer.parseInt(indexText);

                if (index < 0) {
                    continue;
                }

                // Vérifier que le paramètre suit la forme [index].propriete
                if (end + 1 < parameterName.length()
                        && parameterName.charAt(end + 1) == '.') {
                    indices.add(index);
                }

            } catch (NumberFormatException e) {
                // Ignorer les indices qui ne sont pas des nombres.
            }
        }

        List<Object> result = new ArrayList<>();

        for (Integer index : indices) {

            String objectPrefix = prefix + "[" + index + "]";

            if (isSimpleType(elementClass)) {

                
                String value = request.getParameter(objectPrefix);

                if (value == null) {
                    throw new IllegalArgumentException(
                        "Valeur manquante pour " + objectPrefix
                    );
                }

                result.add(convertParameter(value, elementClass));

            } else {

                Object element = bindObject(
                    objectPrefix,
                    elementClass,
                    request
                );

                result.add(element);
            }
        }

        return result;
    }

    private static MapFile bindFile(String parameterName,HttpServletRequest request) throws Exception {

        String contentType = request.getContentType();

        // Pas de formulaire multipart : aucun fichier à récupérer
        if (contentType == null ||
            !contentType.toLowerCase().startsWith("multipart/")) {
            return null;
        }

        Part part = request.getPart(parameterName);

        if (part == null || part.getSize() == 0) {
            return null;
        }

        String submittedFileName = part.getSubmittedFileName();

        if (submittedFileName == null ||
            submittedFileName.trim().isEmpty()) {
            return null;
        }

        String fileName = submittedFileName.replace('\\', '/');
        int lastSlash = fileName.lastIndexOf('/');

        if (lastSlash >= 0) {
            fileName = fileName.substring(lastSlash + 1);
        }

        int lastDot = fileName.lastIndexOf('.');
        String extension = "";

        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            extension = fileName.substring(lastDot + 1);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream input = part.getInputStream()) {
            byte[] buffer = new byte[4096];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }

        return new MapFile(fileName, extension, output.toByteArray());
    }



}