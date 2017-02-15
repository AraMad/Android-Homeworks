package ua.arina.task1.Activitys;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ua.arina.task1.Loaders.TextLoader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private final String TAG = "SecondTask_MainActivity";

    private final int TEXT_LOADER_ID = 0;

    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLoad();
            }
        });

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new TextLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null){
            addTextToTextView(data);
            button.setEnabled(false);
        } else {
            addTextToTextView(getString(R.string.error_message));
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void initLoad(){
        Bundle bundle = new Bundle();
        bundle.putString("file_name", getResources().getString(R.string.text_file_name));
        getSupportLoaderManager().initLoader(TEXT_LOADER_ID, bundle, this);
    }

    private void addTextToTextView(String text){
        textView.setText(text);
    }
}
