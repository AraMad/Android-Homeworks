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
import ua.arina.task4.interfaces.ClickListener;
import ua.arina.task4.models.ItemModel;

public class MainActivity extends AppCompatActivity implements ClickListener{

    private final String TAG = getClass().getSimpleName();

    private final int RESULT_CODE = 0;
    private final String FILE_NAME_PREFIX = "photo_";
    private final String FILE_EXTENSION = ".jpg";
    private final String NAME_TEMPLATE = "yyyyMMdd_HHmmss";

    private ImageView imageView;
    private RecyclerView recyclerView;
    private File photo = null;
    private ArrayList<ItemModel> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        items = new ArrayList<>();
        initItems();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        settingRecycleView();

        findViewById(R.id.button).setOnClickListener(v ->
        {
            try {
                photo = createImageFile();
            } catch (IOException e){
                Log.d(TAG, e.toString());
            }

            if (photo != null){
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo)), RESULT_CODE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE && resultCode == RESULT_OK) {
            addPictureIntoGallery();
            items.add(new ItemModel(photo.getName()));
            recyclerView.getAdapter().notifyDataSetChanged();

            setImage(photo.getPath());
        }
    }

    //TODO: saveInstanceState

    @Override
    public void itemClicked(View view, int position) {

        setImage(new File(takeDirectoryForPhoto().getAbsolutePath())
                + "/"
                + items.get(position).getName());
    }

    private void setImage(String path){
        new Thread(() -> {
            final Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.post(() -> imageView.setImageBitmap(bitmap));
        }).start();
    }

    private void initItems(){
        for (int i = 0; i < takeDirectoryForPhoto().listFiles().length; i++){
            items.add(new ItemModel(takeDirectoryForPhoto().listFiles()[i].getName()));
        }
    }

    private void settingRecycleView(){

        ItemsAdapter itemsAdapter = new ItemsAdapter(items);
        itemsAdapter.setClickListener(this);

        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    private void addPictureIntoGallery() {
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                .setData(Uri.fromFile(photo)));
    }

    private File takeDirectoryForPhoto(){
        File directory = new File(
                Environment
                        .getExternalStorageDirectory(),
                getString(R.string.app_name));
        if (!directory.exists()){
            directory.mkdir();
        }

        return directory;
    }

    private File createImageFile() throws IOException {
        return File.createTempFile(
                FILE_NAME_PREFIX + new SimpleDateFormat(NAME_TEMPLATE, Locale.US)
                        .format(new Date()),
                FILE_EXTENSION,
                takeDirectoryForPhoto()
        );
    }
}
