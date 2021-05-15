package android.example.earth;

import androidx.annotation.NonNull;
import  androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.AsyncTaskLoader;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthAdapter adapter;
    private TextView EmptyStateTextView;
    String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        adapter = new EarthAdapter(this, new ArrayList<EarthQuake>());
        earthquakeListView.setAdapter(adapter);

        EmptyStateTextView =(TextView)findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(EmptyStateTextView);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EarthQuake quake = adapter.getItem(position);
                Uri uri = Uri.parse(quake.getURL());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID,null,this);
    }
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String minMag = sp.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sp.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format" , "geojson");
        uriBuilder.appendQueryParameter("limit","10");
        uriBuilder.appendQueryParameter("minmag" , minMag);
        uriBuilder.appendQueryParameter("orderby" , "time");

        return new EarthQuakeLoader(this,uriBuilder.toString());
    }
    public void onLoadFinished(Loader<List<EarthQuake>> loader , List<EarthQuake> earthQuakes){
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        EmptyStateTextView.setText(R.string.no_earthquakes);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null ) {
            EmptyStateTextView.setText("NO Internet");
        } else if (networkInfo!=null && networkInfo.isConnected()){
//There is internet but list is still empty
            EmptyStateTextView.setText(R.string.no_earthquakes);
        }

        adapter.clear();
        if(earthQuakes!= null && !earthQuakes.isEmpty()){
            adapter.addAll(earthQuakes);
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<List<EarthQuake>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this , SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
