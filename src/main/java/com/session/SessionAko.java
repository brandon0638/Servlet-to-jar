package com.session;

import javax.servlet.http.HttpSession;

public class SessionAko {

    private HttpSession httpSession;

    public SessionAko(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void set(String key, Object value) {
        httpSession.setAttribute(key, value);
    }

    public Object get(String key) {
        return httpSession.getAttribute(key);
    }

    public void remove(String key) {
        httpSession.removeAttribute(key);
    }

    public void invalidate() {
        httpSession.invalidate();
    }

    public String getId() {
        return httpSession.getId();
    }
}