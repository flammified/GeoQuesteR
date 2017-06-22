package nl.alexanderfreeman.geoquester.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.singletons.NavigationSingleton;
import nl.alexanderfreeman.geoquester.Utility.Utility;
import nl.alexanderfreeman.geoquester.views.CompassView;

public class NavigationFragment extends Fragment implements SensorEventListener, android.location.LocationListener {

    LocationManager lmanager;
    SensorManager smanager;
    PowerManager.WakeLock wl;

    View root;

    String locationprovider = "gps";
    //private Sensor mAccelerometer;
    //private Sensor mMagnetometer;
    private Sensor orientatiesensor;

    NumberFormat nf;

    Location targetLocation;
    float targetRichting;
    float deviceRichting;

    TextView name;
    TextView coords;
    TextView distance;

    CompassView kv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.navigate_fragment, null);
        root = v;
        kv = (CompassView) v.findViewById(R.id.compass);

        if (targetLocation != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return v;
            }
        }

        name = (TextView) root.findViewById(R.id.navigate_quest_name);
        coords = (TextView) root.findViewById(R.id.navigate_coordinates);
        distance = (TextView) root.findViewById(R.id.navigate_distance);

        refresh();


        onLocationChanged(lmanager.getLastKnownLocation(locationprovider));
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);

        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Compass");

        lmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationprovider = lmanager.getBestProvider(criteria, true);

        smanager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        orientatiesensor = smanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    }

    private void refresh() {
        Location l = new Location("");
        GeoQuest q = NavigationSingleton.getInstance().getQuest();
        if (q == null) {
            name.setText("No GeoQuest selected");
            coords.setText("");
            distance.setText("");
            return;
        }

        this.setTargetLocation(q.getLongitude(), q.getLatitude());
        name.setText(q.getName());
        coords.setText(Utility.convert(q.getLatitude(), q.getLongitude()));
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    public void setTargetLocation(double lon, double lat) {
        targetLocation = new Location(locationprovider);
        targetLocation.setLongitude(lon);
        targetLocation.setLatitude(lat);
    }

    public void onResume() {
        super.onResume();
        wl.acquire();
        lmanager.requestLocationUpdates(locationprovider, 0, 0, this);
        smanager.registerListener(this, orientatiesensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        onLocationChanged(lmanager.getLastKnownLocation(locationprovider));
    }

    public void onPause() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        lmanager.removeUpdates((android.location.LocationListener) this);
        smanager.unregisterListener(this);
        wl.release();
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
        try {
            targetRichting = currentLocation.bearingTo(targetLocation);
            kv.setDirection(targetRichting - deviceRichting);

            float distance = currentLocation.distanceTo(targetLocation);
            this.distance.setText("" + distance + " m");
        }
        catch(NullPointerException e){
            if(currentLocation == null) Log.e("onLocationChanged", "currentLocation is null");
            if(targetLocation == null)Log.e("onLocationChanged", "targetLocation is null");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int type = event.sensor.getType();
		/* werkt niet op Samsung Galaxy Ace
		float[] mags = {0f,0f,0f};
		if(type == Sensor.TYPE_MAGNETIC_FIELD){
			for(int i = 0; i < 3; i++){mags[i] = event.values[i];}
		}
		float[] accels = {0f,0f,0f};
		if(type == Sensor.TYPE_ACCELEROMETER){
			for(int i = 0; i < 3; i++){accels[i] = event.values[i];}
		}
		float[] R = new float[9];
		float[] I = new float[9];
		boolean b = SensorManager.getRotationMatrix(R, I, accels, mags);
		Log.d("Kompas", "getRotationMatrixSucces: " + b); //false
		float[] attitude = {0f, 0f, 0f};
		SensorManager.getOrientation(R, attitude);
		deviceRichting = attitude[0];
		kv.setDirection(targetRichting - deviceRichting);
		*/
        if (type == Sensor.TYPE_ORIENTATION){
            float[] x = event.values;
            deviceRichting = x[0];
            kv.setDirection(targetRichting - deviceRichting);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSaveInstanceState(Bundle state){
        state.putParcelable("targetlocation", targetLocation);
    }



}
