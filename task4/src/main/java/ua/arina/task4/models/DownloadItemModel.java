package ua.arina.task4.models;

import android.graphics.Bitmap;

/**
 * Created by Arina on 19.03.2017
 */

public class DownloadItemModel {
    private Bitmap image;
    private String path;
    private int index;

    public DownloadItemModel(Bitmap image, String path, int index) {
        this.image = image;
        this.path = path;
        this.index = index;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }
}
