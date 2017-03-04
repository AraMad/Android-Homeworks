package ua.arina.task4.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Arina on 03.03.2017.
 */

public class ImageLoader extends AsyncTaskLoader<Bitmap>{

    private final String TAG = getClass().getSimpleName();

    private String path;

    public ImageLoader(Context context, String path_to_file) {
        super(context);

        path = path_to_file;
    }

    @Override
    public Bitmap loadInBackground() {
        return BitmapFactory.decodeFile(path,
                new BitmapFactory.Options());
    }
}
