package android.example.earth;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class EarthQuakeLoader extends AsyncTaskLoader<List<EarthQuake>> {
    private static final String LOG_TAG = EarthQuakeLoader.class.getName();
    private String mUrl;
    public EarthQuakeLoader(Context context , String url) {
        super(context);
        mUrl =url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<EarthQuake> loadInBackground() {
        if(mUrl == null){
            return null;
        }
        List<EarthQuake> eq = QueryUtils.fetchEarthQuakeData(mUrl);
        return eq;
    }
}
