package com.util;

import java.util.Objects;

public class UrlMethod {

    private String url;
    private String method;


    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method.toUpperCase();
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }


    @Override
    public boolean equals(Object obj) {

        if(this == obj) return true;

        if(!(obj instanceof UrlMethod)) return false;


        UrlMethod other = (UrlMethod)obj;


        return url.equals(other.url) && method.equalsIgnoreCase(other.method);
    }



    @Override
    public int hashCode() {

        return Objects.hash(url,method.toUpperCase());

    }

}