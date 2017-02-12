package ua.arina.task1;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "SecondTask_MainActivity";

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(getResources().getString(R.string.text_title));
                textView.append
                        (readFromAssetsFile(getResources().getString(R.string.text_file_name)));
            }
        });
    }


    private String readFromAssetsFile(String file_name){

        StringBuilder result = new StringBuilder();
        AssetManager assetManager = getAssets();

        try {

            InputStream inputStream = assetManager
                    .open(file_name);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null){
                result.append(buffer);
                result.append('\n');
            }
            inputStream.close();

        } catch (IOException e){
            Log.i(TAG, "readFromAssetsFile: " + e.toString());
        }

        return result.toString();
    }
}
