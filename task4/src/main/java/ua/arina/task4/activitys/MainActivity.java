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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ua.arina.task4.R;
import ua.arina.task4.adapters.ItemsAdapter;
import ua.arina.task4.interfaces.ClickListener;
import ua.arina.task4.models.ItemModel;

public class MainActivity extends AppCompatActivity implements ClickListener{

    private final String TAG = getClass().getSimpleName();

    private ImageView imageView;
    private File photo = null;

    private final int RESULT_CODE = 0;

    ArrayList<ItemModel> items;
    RecyclerView recyclerView;

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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        items = new ArrayList<>();

        for (int i = 0; i < createAppDirectory().listFiles().length; i++){
            items.add(new ItemModel(createAppDirectory().listFiles()[i].getName()));
        }

        settingRecycleView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE && resultCode == RESULT_OK) {
            imageView.setImageURI(Uri.fromFile(photo));
            addPictureIntoGallery();
            items.add(new ItemModel(photo.getName()));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

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

    private void settingRecycleView(){
        ItemsAdapter itemsAdapter = new ItemsAdapter(getApplicationContext(), items);
        itemsAdapter.setClickListener(this);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    public void itemClicked(View view, int position) {
        imageView.setImageURI(Uri.fromFile(new File(createAppDirectory().getAbsolutePath() + "/" + items.get(position).getName())));
        Log.d(TAG, createAppDirectory().getAbsolutePath() + items.get(position).getName());
    }
}
