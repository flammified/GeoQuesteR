package nl.alexanderfreeman.geoquester.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import nl.alexanderfreeman.geoquester.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class AccountInfoFragment extends Fragment implements OnMapReadyCallback {

    MapView mapview;
	GoogleMap map;
	private static final int PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.account_info_fragment, container, false);


        mapview = (MapView) root.findViewById(R.id.map);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return root;
    };

    public void onResume() {
        super.onResume();
        mapview.onResume();
    }

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// Getting LocationManager object from System Service LOCATION_SERVICE
					LocationManager locationManager = (LocationManager) this.getContext().getSystemService(LOCATION_SERVICE);

					// Creating a criteria object to retrieve provider
					Criteria criteria = new Criteria();

					// Getting the name of the best provider
					String provider = locationManager.getBestProvider(criteria, true);

					// Getting Current Location
					Location location = null;
					try {
						location = locationManager.getLastKnownLocation(provider);
						this.map.setMyLocationEnabled(true);
					}
					catch(SecurityException e) {

					}

					if (location != null) {
						// Getting latitude of the current location
						double latitude = location.getLatitude();

						// Getting longitude of the current location
						double longitude = location.getLongitude();

						// Creating a LatLng object for the current location
						LatLng latLng = new LatLng(latitude, longitude);
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
					}

					mapview.onResume();

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

    @Override
    public void onMapReady(GoogleMap map) {
		this.map = map;
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        } else {
			ActivityCompat.requestPermissions(this.getActivity(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSION_REQUEST);

		}

		mapview.onResume();
    }
}
