package com.model;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {


    private String url;

    private Map<String,Object> attribute;



    public ModelAndView(String url){

        this.url = url;
        this.attribute = new HashMap<>();

    }



    public String getUrl(){

        return url;
    }



    public Map<String,Object> getAttribute(){

        return attribute;
    }



    public void addAttribute(String key,Object value){

        attribute.put(key,value);

    }

}