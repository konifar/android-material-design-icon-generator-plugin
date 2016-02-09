package com.konifar.material_icon_generator;

import com.google.gson.Gson;

/**
 * Created by family_lee on 2016/2/8.
 */
public class IconInfo {
    private String name;
    private String color;

    public IconInfo(){
    }

    public IconInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
       Gson gson=new Gson();
        return gson.toJson(this,IconInfo.class);
    }
}
