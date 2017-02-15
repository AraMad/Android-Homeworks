package ua.arina.task1.Loaders;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Arina on 14.02.2017.
 */

public class TextLoader extends AsyncTaskLoader<String> {

    private final String TAG = "TextLoader";

    private final String ARGS_KEY = "file_name";
    private Context current_context;
    private String file_name;
    private String text_data;

    public TextLoader(Context context, Bundle args) {
        super(context);

        if(args != null){
            file_name = args.getString(ARGS_KEY);
        }
        current_context = context;
    }

    @Override
    public String loadInBackground() {
        return readFromAssetsFile(file_name);
    }

    @Override
    protected void onStartLoading() {
        if (text_data != null){
            deliverResult(text_data);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(String data) {

        text_data = data;

        if (isStarted()){
            super.deliverResult(data);
        }
    }

    @Override
    public void onCanceled(String data) {
        super.onCanceled(data);
    }

    private String readFromAssetsFile(String file_name){

        if (file_name == null){
            return null;
        }

        StringBuilder result = new StringBuilder();
        AssetManager assetManager = current_context.getAssets();
        InputStream inputStream = null;

        try {

            inputStream = assetManager
                    .open(file_name);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null){
                result.append(buffer);
                result.append('\n');
            }

        } catch (IOException e){
            Log.i(TAG, "readFromAssetsFile: " + e.toString());
            result = null;
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.i(TAG, "readFromAssetsFile: " + e.toString());
            }
        }

        if (result == null){
            return null;
        } else {
            return result.toString();
        }
    }
}
