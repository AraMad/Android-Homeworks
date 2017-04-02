package ua.arina.task4.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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

public class MainActivity extends AppCompatActivity implements ClickListener, View.OnTouchListener {

    private final String TAG = getClass().getSimpleName();

    private final int RESULT_CODE = 0;
    private final int PERMISSION_REQUEST_CODE = 1;

    private ImageView imageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton takePhotoButton;
    private LinearLayout recyclerViewContainer;
    private BottomSheetBehavior bottomSheetBehavior;

    private File photo;
    private File directory;
    private ArrayList<ItemModel> photos;
    private DownloadComplete downloadComplete;
    private CustomerThread customerThread;
    private String currentItemPath;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        findViewById(R.id.main_layout).setOnTouchListener(this);
        findViews();
        setPolicy();
        requestPermision();
        initDownloadThread();
        initArrayPhotosAndDirectory();
        settingViews();
    }

    private void setPolicy(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void initDownloadThread(){
        downloadComplete = new DownloadComplete(Executors
                .newScheduledThreadPool(5));
        customerThread =  new CustomerThread(downloadComplete);
        customerThread.start();
    }

    private void findViews(){
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        takePhotoButton = (FloatingActionButton) findViewById(R.id.button);
        recyclerViewContainer = (LinearLayout) findViewById(R.id.container);
    }

    private void requestPermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_DENIED) {

            final String message = getResources().getString(R.string.not_allow_permission_text);
            Snackbar.make(imageView, message, Snackbar.LENGTH_LONG)
                    .setAction( getResources().getString(R.string.permission_button_text), null)
                    .setDuration(10000)
                    .show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (timer != null){
            timer.cancel();
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        timer = new Timer();
        timer.schedule(new OwnTimerTask(), Constants.DELAY);

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentItemPath = getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.LAST_SHOWED_ITEM_PATH_KEY, null);

        if (currentItemPath != null){
            loadImage(currentItemPath);
        } else if(photos.size() != 0){
            loadImage(directory.getAbsolutePath()
                    + "/"
                    + photos.get(0).getName());
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
        if (!currentItemPath.equals(directory.getAbsolutePath()
                + "/"
                + photos.get(position).getName())){
            loadImage(directory.getAbsolutePath()
                    + "/"
                    + photos.get(position).getName());
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE && resultCode == RESULT_OK){
            if(photo != null){
                addPictureIntoGallery(photo);
                addItemToItems(photo.getPath());
                loadImage(photo.getPath());
            }
        }
    }

    private void loadImage(String path){
        progressBar.setVisibility(View.VISIBLE);
        downloadComplete.submit(new ImageDownloadTask(path,
                ImageDownloadTask.IMAGE_VIEW_INDEX,
                ImageDownloadTask.SCALE_FACTOR_FULL_IMAGE));
    }

    private void loadPreviews(){
        for (int i = 0; i < directory.listFiles().length; i++) {
            downloadComplete.submit(
                    new ImageDownloadTask(directory.listFiles()[i].getPath(), i,
                            ImageDownloadTask.SCALE_FACTOR_PREVIEW,
                            ImageDownloadTask.ROTATE_DEGREES));
        }
    }

    private void initArrayPhotosAndDirectory(){
        directory = takeDirectoryForPhoto();
        photos = new ArrayList<>();
        if (directory != null && directory.listFiles() != null){
            for (int i = 0; i < directory.listFiles().length; i++) {
                photos.add(new ItemModel(directory.listFiles()[i].getName()));
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

    private void settingViews(){
        ItemsAdapter itemsAdapter = new ItemsAdapter(photos, getApplicationContext());
        itemsAdapter.setClickListener(this);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.HORIZONTAL));
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    if (timer != null){
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new OwnTimerTask(), Constants.DELAY);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        progressBar.setVisibility(View.INVISIBLE);

        takePhotoButton.setOnClickListener(v ->
        {
            photo = takeImageFile();
            //currentItemPath =  photo.getPath();
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo)), RESULT_CODE);

        });
        takePhotoButton.animate().scaleX(0).scaleY(0).setDuration(300).start();

        bottomSheetBehavior = BottomSheetBehavior.from(recyclerViewContainer);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(recyclerViewContainer.getHeight());
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior
                .setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                        if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                            takePhotoButton
                                    .animate().scaleX(1).scaleY(1)
                                    .setDuration(300)
                                    .start();
                        } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                            takePhotoButton
                                    .animate().scaleX(0).scaleY(0)
                                    .setDuration(300)
                                    .start();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
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
        return new File(directory.getAbsolutePath()
                + "/"
                + Constants.FILE_NAME_PREFIX
                + new SimpleDateFormat(Constants.NAME_TEMPLATE, Locale.US).format(new Date())
                + Constants.FILE_EXTENSION);
    }

    private void addImage(DownloadItemModel newItem){

        runOnUiThread(() -> {

            if (newItem.getIndex() == ImageDownloadTask.IMAGE_VIEW_INDEX){
                imageView.setImageBitmap(newItem.getImage());
                currentItemPath = newItem.getPath();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                if (newItem.getIndex() == ImageDownloadTask.NEW_ITEM_INDEX){
                    photos.add(new ItemModel(photo.getName(), newItem.getImage()));
                } else if (newItem.getPath().contains(photos.get(newItem.getIndex()).getName())){
                    photos.get(newItem.getIndex()).setIcon(newItem.getImage());
                } else {
                    for (int i = 0; i < photos.size(); i++){
                        if (newItem.getPath().contains(photos.get(i).getName())){
                        photos.get(i).setIcon(newItem.getImage());
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

    private class OwnTimerTask extends TimerTask {
        @Override
        public void run() {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
