package com.model;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private String url;
    private Map<String,Object> attributes;


    public ModelAndView(String url){

        this.url = url;
        this.attributes = new HashMap<>();

    }


    public String getUrl(){

        return url;

    }


    public void setUrl(String url){

        this.url = url;

    }


    public void addAttribute(String key,Object value){

        attributes.put(key,value);

    }


    public Object getAttribute(String key){

        return attributes.get(key);

    }


    public Map<String,Object> getListAttributes(){

        return attributes;

    }

}