package com.util;

import java.util.Objects;

public class UrlMethod {

    private String url;
    private String method;

    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlMethod)) return false;
        UrlMethod that = (UrlMethod) o;
        return url.equals(that.url) && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }
}