package com.util;

import java.lang.reflect.Method;

public class UrlMethod{

    private Method method;
    private String httpMethod;

    public UrlMethod(Method method, String httpMethod){
        this.method = method;
        this.httpMethod = httpMethod;
    }

    public Method getMethod(){
        return method;
    }
    public String getHttpMethod(){
        return httpMethod;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        UrlMethod that = (UrlMethod) o;
        return Objetcs.equals(method,that.method) && Objects.equals(httpMethod,that.httpMethod);

    }

    @Override
    public int hashCode() {
        return Objects.hash(method, httpMethod);
    }
}