package nl.alexanderfreeman.geoquester.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.utility.Utility;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class AccountInfoFragment extends Fragment implements OnMapReadyCallback {

    MapView mapview;
	GoogleMap map;

	TextView level;
	TextView points;
	TextView count;
    ProgressBar tracker;

	private static final int PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.account_info_fragment, container, false);

        tracker = (ProgressBar) root.findViewById(R.id.level_tracker);
		level = (TextView) root.findViewById(R.id.account_level_text);
		points = (TextView) root.findViewById(R.id.account_points_text);
		count = (TextView) root.findViewById(R.id.account_total_quests);

		String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + uid);
		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Your user does not exist. Please log in again. You will be logged out.");
                    builder1.setCancelable(false);

                    builder1.setNegativeButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ((MainScreenActivity) getActivity()).signout();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    return;
                }

                tracker.setProgress(Utility.safeLongToInt((long) dataSnapshot.child("points").getValue()));
                Log.d("DEBUG", "" + dataSnapshot.child("level").getValue());
				level.setText("Level " + dataSnapshot.child("level").getValue() + " Quester");
                points.setText("" + dataSnapshot.child("points").getValue() + " QuestPoints earned!");
                count.setText("" + dataSnapshot.child("count").getValue() + " GeoQuest(s) completed!");
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_LONG);
			}
		});

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
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					mapview.onResume();
				} else {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("This app can't function without location services. Please grant permission; closing the app for now.")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().finish();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
				}
				return;
			}
		}
	}

    @Override
    public void onMapReady(final GoogleMap map) {
		this.map = map;
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {} else {
			ActivityCompat.requestPermissions(this.getActivity(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSION_REQUEST);

		}

        LatLng middle = new LatLng(52.205761, 5.316239);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(middle, 6));

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("quests/");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    GeoQuest quest = child.getValue(GeoQuest.class);
                    if (!child.child("found/" + user.getUid()).exists()) {

                        LatLng quest_latlng = new LatLng(quest.getLatitude(), quest.getLongitude());


                        Marker marker = map.addMarker(new MarkerOptions()
                                        .position(quest_latlng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                        .draggable(false)
                                        .visible(true)
                                        .title(quest.getName()));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

		mapview.onResume();
    }
}
