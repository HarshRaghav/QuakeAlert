package android.example.earth;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.example.earth.MainActivity.LOG_TAG;

public class QueryUtils {
    private static final String USGS_REQUEST_URL ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=0&limit=50";

    public static  ArrayList<EarthQuake> fetchEarthQuakeData(String url){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URL Url = createUrl(url);
        String jsonResponse =null;
        try{
            jsonResponse = makeHttpRequest(Url);
        }catch(IOException e){
            Log.e(LOG_TAG, "Error closing input stream" ,e);
        }
        ArrayList<EarthQuake> eq = extractFeatureFromJson(jsonResponse);
        return eq;
    }

    public static URL createUrl(String str){
        URL url =null;
        try{
            url =new URL(str);
        }catch(MalformedURLException e){
            Log.e(LOG_TAG , "error in url creation" , e);
        }
        return url;
    }
    private static String makeHttpRequest(URL url)throws IOException {
        String jsonResponse ="";
        if(url==null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream =null;
        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream !=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream is) throws IOException{
        StringBuilder output = new StringBuilder();
        if(is!=null){
            BufferedReader br = new BufferedReader(new InputStreamReader(is , Charset.forName("UTF-8")));
            String line =br.readLine();
            while(line != null){
                output.append(line);
                line = br.readLine();
            }
        }
        return output.toString();
    }
    private static ArrayList<EarthQuake> extractFeatureFromJson(String earthquakeJSON){
        if(TextUtils.isEmpty(earthquakeJSON)){
            return null;
        }
        try{
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray Arr = baseJsonResponse.getJSONArray("features");
            ArrayList<EarthQuake> eq = new ArrayList<>();
            for(int i=0;i<Arr.length();i++){
                JSONObject jo = Arr.getJSONObject(i);
                JSONObject properties = jo.getJSONObject("properties");
                eq.add(new EarthQuake(properties.getDouble("mag"),properties.getString("place"),properties.getLong("time"),properties.getString("url")));
            }
            if(!eq.isEmpty()){
                return eq;
            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results" ,e);
        }
        return null;
    }
}
