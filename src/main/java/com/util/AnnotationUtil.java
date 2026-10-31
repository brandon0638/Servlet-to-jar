package com.util;

import com.annotation.Controllerako;
import com.annotation.UrlMapping;
import com.annotation.WebAPI;
import com.annotation.Param;
import com.annotation.Auth;
import com.exception.UrlNotFoundException;
import com.exception.UnauthorizedException;
import com.model.MapFile;
import com.session.SessionAko;
import com.annotation.Role;
import com.exception.ForbiddenException;

import java.util.Arrays;
import java.util.List;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationUtil {

    private static List<Class<?>> classes = new ArrayList<>();

    private static final String SESSIONS_ATTRIBUTE = "sessions";

    
    // Scan des packages
   

    public static void scanPackage(String basePackage) throws Exception {

        classes = new ArrayList<>();

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String path = basePackage.replace(".", "/");

        Enumeration<URL> resources = loader.getResources(path);

        while(resources.hasMoreElements()) {

            URL resource = resources.nextElement();

            File directory = new File(resource.toURI());

            classes.addAll(findClasses(directory, basePackage));
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

            // Sprint 1 : scan récursif des sous-packages
            if(file.isDirectory()) {
                result.addAll(findClasses(
                    file,
                    packageName + "." + file.getName()
                ));
            } else if(file.getName().endsWith(".class")) {

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

    
    // Invocation (Sprint 7 / 7-bis / 8 / 9 / 10)
    

    public static Object invoke(RouteMapping route,
                                ApplicationContext springContext,
                                HttpServletRequest request) throws Exception {

        Method method = route.getMethod();

        // Sprint 10 : vérification d'authentification (avant tout)
        checkAuth(method, request);

        Object controller = springContext.getBean(route.getController());
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();

            // Sprint 9 : injection automatique de SessionAko
            if (parameterType == SessionAko.class) {
                arguments[i] = getOrCreateSession(request);
                continue;
            }

            // Injection automatique de HttpServletRequest
            if (parameterType == HttpServletRequest.class) {
                arguments[i] = request;
                continue;
            }

            // Injection automatique de HttpSession
            if (parameterType == HttpSession.class) {
                arguments[i] = request.getSession();
                continue;
            }

            // Sprint 7 : binding obligatoire avec @Param
            if (!parameter.isAnnotationPresent(Param.class)) {
                throw new RuntimeException(
                    "Le parametre '" + parameter.getName()
                    + "' de la methode " + method.getName()
                    + " doit utiliser @Param"
                );
            }

            Param param = parameter.getAnnotation(Param.class);
            String parameterName = param.value();

            if (isSimpleType(parameterType)) {
                String value = request.getParameter(parameterName);
                arguments[i] = convertParameter(value, parameterType);
            } else {
                arguments[i] = bindObject(parameterName, parameterType, request);
            }
        }

        return method.invoke(controller, arguments);
    }

    
    // Sprint 10 : vérification d'authentification
    

    private static void checkAuth(Method method, HttpServletRequest request) {

        boolean hasAuth = method.isAnnotationPresent(Auth.class);
        boolean hasRole = method.isAnnotationPresent(Role.class);

        // Ni @Auth ni @Role → pas de vérification
        if (!hasAuth && !hasRole) {
            return;
        }

        // Récupérer la clé de session pour l'authentification
        String authSessionKey =
            (String) request.getServletContext().getAttribute("authSessionKey");

        if (authSessionKey == null || authSessionKey.trim().isEmpty()) {
            throw new UnauthorizedException(
                "Configuration manquante : 'authSessionKey' non defini dans web.xml"
            );
        }

        // Vérifier que l'utilisateur est connecté (Sprint 10)
        Object user = request.getSession().getAttribute(authSessionKey);

        if (user == null) {
            throw new UnauthorizedException(
                "Authentification requise pour acceder a " + method.getName()
            );
        }

        // Sprint 10-bis : vérifier le(s) rôle(s) si @Role présent
        if (hasRole) {

            Role roleAnnotation = method.getAnnotation(Role.class);
            String[] requiredRoles = roleAnnotation.value();

            String rolesSessionKey =
                (String) request.getServletContext().getAttribute("rolesSessionKey");

            if (rolesSessionKey == null || rolesSessionKey.trim().isEmpty()) {
                throw new ForbiddenException(
                    "Configuration manquante : 'rolesSessionKey' non defini dans web.xml"
                );
            }

            Object rolesObj = request.getSession().getAttribute(rolesSessionKey);

            if (rolesObj == null) {
                throw new ForbiddenException(
                    "Aucun role en session. Requis : " + Arrays.toString(requiredRoles)
                );
            }

            if (!(rolesObj instanceof List)) {
                throw new ForbiddenException(
                    "Le role en session doit etre une List<String>"
                );
            }

            @SuppressWarnings("unchecked")
            List<String> userRoles = (List<String>) rolesObj;

            // OR logique : au moins un rôle requis doit être présent
            boolean ok = false;
            for (String required : requiredRoles) {
                if (userRoles.contains(required)) {
                    ok = true;
                    break;
                }
            }

            if (!ok) {
                throw new ForbiddenException(
                    "Role insuffisant. Requis : " + Arrays.toString(requiredRoles)
                    + ", vous avez : " + userRoles
                );
            }
        }
    }

    
    // Sprint 9 : gestion de la session
    

    @SuppressWarnings("unchecked")
    private static SessionAko getOrCreateSession(HttpServletRequest request) {

        ServletContext context = request.getServletContext();

        Map<String, SessionAko> sessions =
            (Map<String, SessionAko>) context.getAttribute(SESSIONS_ATTRIBUTE);

        if (sessions == null) {
            sessions = new HashMap<>();
            context.setAttribute(SESSIONS_ATTRIBUTE, sessions);
        }

        HttpSession httpSession = request.getSession();
        String id = httpSession.getId();

        SessionAko existing = sessions.get(id);
        if (existing != null) {
            return existing;
        }

        SessionAko newSession = new SessionAko(httpSession);
        sessions.put(id, newSession);
        return newSession;
    }

    
    // Sprint 7 : binding simple
   

    private static Object convertParameter(String value,Class<?> type) {

        if(value == null) {
            if(type.isPrimitive()) {
                throw new IllegalArgumentException("Impossible de donner null " + "au type primitif " + type.getSimpleName());
            }
            return null;
        }

        if(type == String.class) return value;
        if(type == Integer.class) return Integer.valueOf(value);
        if(type == int.class) return Integer.parseInt(value);
        if(type == Long.class) return Long.valueOf(value);
        if(type == long.class) return Long.parseLong(value);
        if(type == Double.class) return Double.valueOf(value);
        if(type == double.class) return Double.parseDouble(value);
        if(type == Float.class) return Float.valueOf(value);
        if(type == float.class) return Float.parseFloat(value);
        if(type == Boolean.class) return Boolean.valueOf(value);
        if(type == boolean.class) return Boolean.parseBoolean(value);
        if(type == Short.class) return Short.valueOf(value);
        if(type == short.class) return Short.parseShort(value);
        if(type == Byte.class) return Byte.valueOf(value);
        if(type == byte.class) return Byte.parseByte(value);

        if(type == Character.class) {
            if(value.isEmpty()) return null;
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
            || type == Integer.class || type == int.class
            || type == Long.class || type == long.class
            || type == Double.class || type == double.class
            || type == Float.class || type == float.class
            || type == Boolean.class || type == boolean.class
            || type == Short.class || type == short.class
            || type == Byte.class || type == byte.class
            || type == Character.class || type == char.class;
    }

    
    // Sprint 7-bis : binding objet
   

    private static Object bindObject(String prefix,Class<?> type,HttpServletRequest request) throws Exception {

        forceMultipartParsing(request);

        Object object = type.getDeclaredConstructor().newInstance();

        PropertyDescriptor[] properties =
            Introspector.getBeanInfo(type, Object.class)
                        .getPropertyDescriptors();

        for (PropertyDescriptor property : properties) {

            Method setter = property.getWriteMethod();
            if (setter == null) continue;

            String propertyName = property.getName();
            String parameterName = prefix + "." + propertyName;
            Class<?> propertyType = property.getPropertyType();

            if (isSimpleType(propertyType)) {

                String value = request.getParameter(parameterName);
                if (value == null) continue;

                Object convertedValue = convertParameter(value, propertyType);
                setter.invoke(object, convertedValue);

            } else if (propertyType == MapFile.class) {

                MapFile mapFile = bindFile(parameterName, request);
                if (mapFile != null) {
                    setter.invoke(object, mapFile);
                }

            } else if (List.class.isAssignableFrom(propertyType)) {

                if (!hasParametersWithPrefix(request, parameterName + "[")) continue;

                List<Object> list = bindList(parameterName, setter, request);
                setter.invoke(object, list);

            } else {

                if (!hasParametersWithPrefix(request, parameterName + ".")) continue;

                Object nestedObject = bindObject(parameterName, propertyType, request);
                setter.invoke(object, nestedObject);
            }
        }

        return object;
    }

    private static void forceMultipartParsing(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            return;
        }
        try {
            for (Part part : request.getParts()) {
                part.getName(); // lecture = déclenche le parsing
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing multipart", e);
        }
    }

    private static boolean hasParametersWithPrefix(HttpServletRequest request,String prefix) {

        for (String parameterName : request.getParameterMap().keySet()) {
            if (parameterName.startsWith(prefix)) return true;
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

            if (!parameterName.startsWith(indexPrefix)) continue;

            int start = indexPrefix.length();
            int end = parameterName.indexOf("]", start);

            if (end == -1) continue;

            String indexText = parameterName.substring(start, end);

            try {
                int index = Integer.parseInt(indexText);
                if (index < 0) continue;

                if (end + 1 < parameterName.length()
                        && parameterName.charAt(end + 1) == '.') {
                    indices.add(index);
                }
            } catch (NumberFormatException e) {
                // ignorer
            }
        }

        List<Object> result = new ArrayList<>();

        for (Integer index : indices) {

            String objectPrefix = prefix + "[" + index + "]";

            if (isSimpleType(elementClass)) {
                String value = request.getParameter(objectPrefix);
                if (value == null) {
                    throw new IllegalArgumentException("Valeur manquante pour " + objectPrefix);
                }
                result.add(convertParameter(value, elementClass));
            } else {
                Object element = bindObject(objectPrefix, elementClass, request);
                result.add(element);
            }
        }

        return result;
    }

    
    // Sprint 8 : binding fichier
    

    private static MapFile bindFile(String parameterName,HttpServletRequest request) throws Exception {

        String contentType = request.getContentType();

        if (contentType == null ||
            !contentType.toLowerCase().startsWith("multipart/")) {
            return null;
        }

        Part part = request.getPart(parameterName);

        if (part == null || part.getSize() == 0) {
            return null;
        }

        String submittedFileName = part.getSubmittedFileName();

        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
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