package android.example.earth;

public class EarthQuake {
    private double magnitude;
    private String location;
    private String mURL;
    private long millitime;

    public EarthQuake(double magnitude , String location ,long date ,String mURL){
        this.magnitude=magnitude;
        this.location=location;
        this.millitime=date;
        this.mURL=mURL;
    }
    public double getMagnitude(){
        return magnitude;
    }
    public String getLocation(){
        return location;
    }
    public long getDate(){
        return millitime;
    }
    public String getURL(){return mURL;}
}
