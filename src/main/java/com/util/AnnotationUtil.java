package com.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.net.URL;

import com.annotation.Controllerako;

public class AnnotationUtil {

    public static List<Class<?>> getControllers(String basePackage) {
        List<Class<?>> result = new ArrayList<>();
        List<Class<?>> classes = getClasses(basePackage);
        
        for (Class<?> c : classes) {
            if (c.isAnnotationPresent(Controllerako.class)) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<Class<?>> getClasses(String basePackage) {

        List<Class<?>> classes = new ArrayList<>();

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            String path = basePackage.replace(".", "/");

            Enumeration<URL> resources = cl.getResources(path);

            while (resources.hasMoreElements()) {

                URL resource = resources.nextElement();

                File dir = new File(resource.toURI());

                if (!dir.exists()) continue;

                for (File file : dir.listFiles()) {

                    if (file.getName().endsWith(".class")) {

                        String className = basePackage + "." +
                                file.getName().replace(".class", "");

                        try {
                            classes.add(Class.forName(className));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static List<Method> getAnnotatedMethods(Class<?> c,
            Class annotation) {

        List<Method> result = new ArrayList<>();

        for (Method m : c.getDeclaredMethods()) {

            if (m.isAnnotationPresent(annotation)) {
                result.add(m);
            }
        }

        return result;
    }

    public static List<Field> getAnnotatedFields(Class<?> c,
            Class annotation) {

        List<Field> result = new ArrayList<>();

        for (Field f : c.getDeclaredFields()) {

            if (f.isAnnotationPresent(annotation)) {
                result.add(f);
            }
        }

        return result;
    }

   
    
}