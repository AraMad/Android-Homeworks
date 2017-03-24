package ua.arina.task4.models;

import android.graphics.Bitmap;

/**
 * Created by Arina on 04.03.2017
 */

public class ItemModel {

    private String name;
    private Bitmap icon = null;

    public ItemModel(String name, Bitmap icon) {
        this.name = name;
        this.icon = icon;
    }

    public ItemModel(String name) {
        this.name = name;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }
}
