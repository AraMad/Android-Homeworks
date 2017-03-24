package ua.arina.task4.dataloaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.concurrent.Callable;

import ua.arina.task4.models.DownloadItemModel;

/**
 * Created by Arina on 19.03.2017
 */

public class ImageDownloadTask implements Callable<DownloadItemModel>{

    public static final int IMAGE_VIEW_INDEX = -1;
    public static final int NEW_ITEM_INDEX = -2;

    public static final int SCALE_FACTOR_PREVIEW = 20;
    public static final int SCALE_FACTOR_FULL_IMAGE = 5;
    public static final float ROTATE_DEGREES = 90.0f;

    private String path;
    private int index;
    private int scaleFactor;
    private float rotateDegree;

    public ImageDownloadTask(String path, int index, int scaleFactor, float rotateDegree) {
        this.path = path;
        this.index = index;
        this.scaleFactor = scaleFactor;
        this.rotateDegree = rotateDegree;
    }

    public ImageDownloadTask(String path, int index, int scaleFactor) {
        this.path = path;
        this.index = index;
        this.scaleFactor = scaleFactor;
        this.rotateDegree = 0.0f;
    }

    @Override
    public DownloadItemModel call() throws Exception {
        return downloadImageFromDisk(path, index);
    }

    private DownloadItemModel downloadImageFromDisk(String path, int index){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);

        return new DownloadItemModel(
                Bitmap.createBitmap(bitmap,0,0,
                        bitmap.getWidth(),
                        bitmap.getHeight(), matrix, false),
                path, index);
    }
}
