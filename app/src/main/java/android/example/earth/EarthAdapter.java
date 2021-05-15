package android.example.earth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.GradientDrawable;

public class EarthAdapter extends ArrayAdapter<EarthQuake> {
    private static final String LOCATION_SEPERATOR = " of ";
    String locationOffset;
    String primaryLocation;

    public EarthAdapter(Context context, List<EarthQuake> earth){
        super(context , 0 ,earth);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView =convertView;
        if(listItemView==null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.earth_list, parent, false);
        }
        EarthQuake current = getItem(position);
        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        String magFormatted = formatMag(current.getMagnitude());
        magnitudeView.setText(magFormatted);

        String original_location  = current.getLocation();
        if(original_location.contains(LOCATION_SEPERATOR)){
            String[] divided = original_location.split(LOCATION_SEPERATOR);
            locationOffset = divided[0] + LOCATION_SEPERATOR;
            primaryLocation = divided[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = original_location;
        }
        TextView offset = (TextView) listItemView.findViewById(R.id.location_offset);
        offset.setText(locationOffset);
        TextView primary = (TextView) listItemView.findViewById(R.id.primary_location);
        primary.setText(primaryLocation);

        GradientDrawable magCircle = (GradientDrawable)magnitudeView.getBackground();
        int magnitudeColor = getMagColor(current.getMagnitude());
        magCircle.setColor(magnitudeColor);

        Date dateObject = new Date(current.getDate());
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String formattedDate = formatDate(dateObject);
        dateView.setText(formattedDate);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(dateObject);
        timeView.setText(formattedTime);

        return listItemView;
    }
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject).toString();
    }
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
    private String formatMag(double mag){
        DecimalFormat magFormat = new DecimalFormat("0.0");
        return magFormat.format(mag);
    }
    private int getMagColor(double mag){
        int magCororReferenceID;
        int magFloor =(int)Math.floor(mag);
        switch(magFloor){
            case 0:
            case 1:
                magCororReferenceID = R.color.magnitude1;
                break;
            case 2:
                magCororReferenceID = R.color.magnitude2;
                break;
            case 3:
                magCororReferenceID = R.color.magnitude3;
                break;
            case 4:
                magCororReferenceID = R.color.magnitude4;
                break;
            case 5:
                magCororReferenceID = R.color.magnitude5;
                break;
            case 6:
                magCororReferenceID = R.color.magnitude6;
                break;
            case 7:
                magCororReferenceID = R.color.magnitude7;
                break;
            case 8:
                magCororReferenceID = R.color.magnitude8;
                break;
            case 9:
                magCororReferenceID = R.color.magnitude9;
                break;
            default:
                magCororReferenceID = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magCororReferenceID);
    }
}
