package ua.arina.task4.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.arina.task4.R;
import ua.arina.task4.adapters.ItemsAdapter;
import ua.arina.task4.models.ItemModel;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private ImageView imageView;
    private File photo = null;

    private final int RESULT_CODE = 0;

    ArrayList<ItemModel> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.button).setOnClickListener(v ->
        {
            try {
                photo = createImageFile();
            } catch (IOException e){
                Log.d(TAG, e.toString());
            }

            if (photo != null){
                Log.d(TAG, photo.getAbsolutePath());
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                startActivityForResult(takePictureIntent, RESULT_CODE);
            }

        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);

        items = ItemModel.createContactsList(20);
        ItemsAdapter adapter = new ItemsAdapter(getApplicationContext(), items);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE) {
            if (resultCode == RESULT_OK){
                if (data != null){
                    imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
                }
                imageView.setImageURI(Uri.fromFile(photo));
                addPictureIntoGallery();
            }
        }
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("path_to_image", photo.getAbsolutePath());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            imageView.setImageBitmap(BitmapFactory.decodeFile(savedInstanceState.getString("path_to_image"),
                    new BitmapFactory.Options()));
        }
    }*/

    private File createImageFile() throws IOException {

        return File.createTempFile(
                "photo_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()),
                ".jpg",
                createAppDirectory()
        );

    }

    private File createAppDirectory(){
        File directory = new File(
                Environment
                        .getExternalStorageDirectory(),
                getString(R.string.app_name));
        if (!directory.exists()){
            directory.mkdir();
        }

        return directory;
    }

    private void addPictureIntoGallery() {
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                .setData(Uri.fromFile(photo)));
    }
}
