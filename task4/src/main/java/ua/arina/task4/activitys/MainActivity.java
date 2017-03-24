package ua.arina.task4.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ua.arina.task4.R;
import ua.arina.task4.adapters.ItemsAdapter;
import ua.arina.task4.interfaces.ClickListener;
import ua.arina.task4.models.DownloadItemModel;
import ua.arina.task4.models.ItemModel;
import ua.arina.task4.dataloaders.DownloadComplete;
import ua.arina.task4.dataloaders.ImageDownloadTask;
import ua.arina.task4.settings.Constants;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.arina.task4.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity implements ClickListener {

    private final String TAG = getClass().getSimpleName();

    private final int RESULT_CODE = 0;

    private ImageView imageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private File photo;
    private ArrayList<ItemModel> items;
    private DownloadComplete downloadComplete;
    private CustomerThread customerThread;

    private String currentItemPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_2);
        progressBar.setVisibility(View.INVISIBLE);

        downloadComplete = new DownloadComplete(Executors
                .newScheduledThreadPool(5));
        customerThread =  new CustomerThread(downloadComplete);
        customerThread.start();

        items = new ArrayList<>();
        initItems();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        settingRecycleView();

        findViewById(R.id.button).setOnClickListener(v ->
        {
            photo = takeImageFile();
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo)), RESULT_CODE);

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentItemPath = getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.LAST_SHOWED_ITEM_PATH_KEY, null);

        if (currentItemPath != null){
            loadImage(currentItemPath);
        } else if(items.size() != 0){
            loadImage(takeDirectoryForPhoto().getAbsolutePath()
                    + "/"
                    + items.get(0).getName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.LAST_SHOWED_ITEM_PATH_KEY, currentItemPath)
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        downloadComplete.shutdownNow();
        customerThread.interrupt();
    }

    @Override
    public void itemClicked(View view, int position) {
        if (!currentItemPath.equals(takeDirectoryForPhoto().getAbsolutePath()
                + "/"
                + items.get(position).getName())){
            loadImage(takeDirectoryForPhoto().getAbsolutePath()
                    + "/"
                    + items.get(position).getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE && resultCode == RESULT_OK){
            if (DEBUG) {
                Log.d("fgfg", "result");
            }
                addPictureIntoGallery(photo);
                addItemToItems(photo.getPath());
                loadImage(photo.getPath());
        }
    }

    private void loadImage(String path){
        progressBar.setVisibility(View.VISIBLE);
        downloadComplete.submit(new ImageDownloadTask(path,
                ImageDownloadTask.IMAGE_VIEW_INDEX,
                ImageDownloadTask.SCALE_FACTOR_FULL_IMAGE));
    }

    private void loadPreviews(){
        for (int i = 0; i < takeDirectoryForPhoto().listFiles().length; i++) {
            downloadComplete.submit(
                    new ImageDownloadTask(takeDirectoryForPhoto().listFiles()[i].getPath(), i,
                            ImageDownloadTask.SCALE_FACTOR_PREVIEW,
                            ImageDownloadTask.ROTATE_DEGREES));
        }
    }

    private void initItems(){
        if (takeDirectoryForPhoto().listFiles() != null){
            for (int i = 0; i < takeDirectoryForPhoto().listFiles().length; i++) {
                items.add(new ItemModel(takeDirectoryForPhoto().listFiles()[i].getName()));
            }
            loadPreviews();
        }
    }

    private void addItemToItems(String path){
        downloadComplete.submit(
                new ImageDownloadTask(path, ImageDownloadTask.NEW_ITEM_INDEX,
                        ImageDownloadTask.SCALE_FACTOR_PREVIEW,
                        ImageDownloadTask.ROTATE_DEGREES));
    }

    private void settingRecycleView(){

        ItemsAdapter itemsAdapter = new ItemsAdapter(items);
        itemsAdapter.setClickListener(this);
        recyclerView.setAdapter(itemsAdapter);

        recyclerView.setAnimation(new Animation() {
            @Override
            protected Animation clone() throws CloneNotSupportedException {
                return super.clone();
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.HORIZONTAL));
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    private File takeDirectoryForPhoto(){
        File directory = new File(
                Environment.getExternalStorageDirectory(),
                getString(R.string.app_name));
        if (!directory.exists()){
            if (directory.mkdir()){
                directory = getDir(getString(R.string.app_name),
                        MODE_PRIVATE);
            }
        }
        return directory;
    }

    private File takeImageFile(){
        return new File(takeDirectoryForPhoto().getAbsolutePath()
                + "/"
                + Constants.FILE_NAME_PREFIX
                + new SimpleDateFormat(Constants.NAME_TEMPLATE, Locale.US).format(new Date())
                + Constants.FILE_EXTENSION);
    }

    private void addImage(DownloadItemModel newItem){

        runOnUiThread(() -> {

            if (newItem.getIndex() == ImageDownloadTask.IMAGE_VIEW_INDEX){
                if (DEBUG) {
                    Log.d("fgfg", "set image: index " + newItem.getIndex() + "path: " + newItem.getPath());
                }
                imageView.setImageBitmap(newItem.getImage());
                currentItemPath = newItem.getPath();
                progressBar.setVisibility(View.INVISIBLE);
            } else {

                if (DEBUG) {
                    Log.d("fgfg", "+++set else " + newItem.getIndex() + " path: " + newItem.getPath());
                }

                if (newItem.getIndex() == ImageDownloadTask.NEW_ITEM_INDEX){

                    if (DEBUG) {
                        Log.d("fgfg", "set: new item " + newItem.getIndex() + "path: " + newItem.getPath());
                    }
                    items.add(new ItemModel(photo.getName(), newItem.getImage()));
                } else if (newItem.getPath().contains(items.get(newItem.getIndex()).getName())){
                    items.get(newItem.getIndex()).setIcon(newItem.getImage());
                } else {
                    for (int i = 0; i < items.size(); i++){
                        if (newItem.getPath().contains(items.get(i).getName())){
                        items.get(i).setIcon(newItem.getImage());
                        break;
                        }
                    }
                }
                recyclerView.getAdapter().notifyItemChanged(newItem.getIndex());
            }
        });
    }

    private void addPictureIntoGallery(File picture) {
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                .setData(Uri.fromFile(picture)));
    }

    private class CustomerThread extends Thread{

        private DownloadComplete downloadComplete;

        private CustomerThread(DownloadComplete downloadComplete){
            this.downloadComplete = downloadComplete;
        }

        @Override
        public void run() {
            super.run();

            try {

                while (!downloadComplete.isTerminated()){
                    Future<?> future = downloadComplete.poll(Constants.TIMEOUT, TimeUnit.SECONDS);
                    if (future != null){
                        addImage((DownloadItemModel) future.get());
                    }
                }

            }catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        }

    }
}
