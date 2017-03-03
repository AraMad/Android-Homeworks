package ua.arina.task4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private ImageView imageView;
    private final int RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.button).setOnClickListener(v ->
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), RESULT_CODE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable) imageView.getDrawable())
                .getBitmap()
                .compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] imageInByte = stream.toByteArray();

        outState.putByteArray("image", imageInByte);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            try {
                byte[] imageInByte = savedInstanceState.getByteArray("image");
                imageView.setImageBitmap(BitmapFactory
                        .decodeByteArray(imageInByte, 0, imageInByte.length));
            } catch (NullPointerException e){
                Log.d(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            if (resultCode == RESULT_OK){
                imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
            }
        }
    }
}
