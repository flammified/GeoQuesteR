package nl.alexanderfreeman.geoquester.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.singletons.NavigationSingleton;
import nl.alexanderfreeman.geoquester.utility.Utility;

/**
 * Created by Alexander Freeman on 6/23/17.
 */

public class MapNavigationFragment extends Fragment implements OnMapReadyCallback, OnLocationUpdatedListener {

	TextView name;
	TextView coords;
	TextView distance;

	MapView mapview;
	Location lastKnown;
	Polyline last_poly;
	Marker last_marker;
	GoogleMap map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.map_nav_fragment, container, false);

		GeoQuest quest = NavigationSingleton.getInstance().getQuest();

		name = (TextView) root.findViewById(R.id.navigate_map_quest_name);
		coords = (TextView) root.findViewById(R.id.navigate_map_coords);
		distance = (TextView) root.findViewById(R.id.navigate_map_distance);


		if (quest == null) {
			name.setText("No GeoQuest selected");
			coords.setText("");
			distance.setText("");

		}
		else {
			name.setText(quest.getName());
			coords.setText(Utility.convert(quest.getLatitude(), quest.getLongitude()));
			distance.setText("? meter away");
		}

		setHasOptionsMenu(true);

		mapview = (MapView) root.findViewById(R.id.map_map);
		mapview.onCreate(savedInstanceState);
		mapview.getMapAsync(this);

		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.to_map_menu, menu);
		super.onCreateOptionsMenu(menu,inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((MainScreenActivity) getActivity()).switch_to_compass();
		return true;
	}


	public void onResume() {
		super.onResume();
		GeoQuest quest = NavigationSingleton.getInstance().getQuest();
		if (quest != null) {
			SmartLocation.with(getContext()).location().start(this);
		}
	}

	public void onPause() {
		super.onPause();
		SmartLocation.with(getContext()).location().stop();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		this.map = map;
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(true);
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		map.setMyLocationEnabled(true);
		mapview.onResume();
	}

	@Override
	public void onLocationUpdated(Location location) {
		GeoQuest quest = NavigationSingleton.getInstance().getQuest();
		if (quest == null) {
			return;
		}

		if (last_poly != null) {
			last_poly.remove();
			last_marker.remove();
		}
		lastKnown = location;
		LatLng my_location = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
		LatLng quest_latlng = new LatLng(quest.getLatitude(), quest.getLongitude());

		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(my_location);
		points.add(quest_latlng);

		last_poly = map.addPolyline(new PolylineOptions()
				.width(5)
				.addAll(points)
				.color(Color.RED));
		last_marker = map.addMarker(new MarkerOptions()
				.position(quest_latlng)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				.draggable(false)
				.visible(true));

		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(my_location);
		builder.include(quest_latlng);
		LatLngBounds bounds = builder.build();

		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
		map.moveCamera(cu);

		distance.setText(""  + quest.getLocation().distanceTo(lastKnown) + " m");

		last_poly.setPoints(points);
	}
}
