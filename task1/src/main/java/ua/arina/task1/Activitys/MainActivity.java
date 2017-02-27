package ua.arina.task1.activitys;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ua.arina.task1.loaders.TextLoader;
import ua.arina.task1.settings.Constants;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    private final String TAG = "SecondTask_MainActivity";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text_view);

        initLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new TextLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        addTextToTextView((data != null)? data : getString(R.string.error_message));
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void initLoad(){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS_KEY,
                getResources().getString(R.string.text_file_name));

        getSupportLoaderManager().initLoader(Constants.TEXT_LOADER_ID, bundle, this);
    }

    private void addTextToTextView(String text){
        textView.setText(text);
    }
}
