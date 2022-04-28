package com.example.hw1;

import android.graphics.drawable.Drawable;

public class App{
    private final String name;
    private final Drawable icon;
    private final String packageName;
    private final int id;
    private static int idInc = 0;

    public App(String name, Drawable icon, String packageName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        //each app will have its own id for sorting by default which means sorting by the order of which the apps were added to the custom ArrayAdapters list
        this.id = idInc++;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }
}
