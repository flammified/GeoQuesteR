package nl.alexanderfreeman.geoquester.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.alexanderfreeman.geoquester.singletons.NavigationSingleton;
import nl.alexanderfreeman.geoquester.singletons.Utility;
import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by A on 20-6-2017.
 */

public class GeoQuestInformationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    MapView mapview;
    GeoQuest quest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.quest_information_fragment, container, false);

        this.quest = (GeoQuest) getArguments().getSerializable("quest");

        TextView description = (TextView)root.findViewById(R.id.text_description);
        TextView name = (TextView)root.findViewById(R.id.info_quest_name);
        TextView coords = (TextView)root.findViewById(R.id.info_quest_coordinate);

        ImageButton navigate =  (ImageButton)root.findViewById(R.id.navigate);
        navigate.setOnClickListener(this);

        name.setText(quest.getName());
        coords.setText(Utility.convert(quest.getLatitude(), quest.getLongitude()));
        description.setText(quest.getDescription());

        mapview = (MapView) root.findViewById(R.id.map);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        return root;
    };

    @Override
    public void onResume(){
        super.onResume();
        Log.d("DEBUG", "Does this work?");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       LatLng quest_latlng = new LatLng(quest.getLatitude(), quest.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(quest_latlng, 12));

        map.getUiSettings().setZoomControlsEnabled(false);
        Marker marker =
                map.addMarker(new MarkerOptions()
                .position(quest_latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(false)
                .visible(true));
        mapview.onResume();
    }

    @Override
    public void onClick(View view) {
        NavigationSingleton.getInstance().setQuest(this.quest);
        ((MainScreenActivity) getActivity()).set_to_navigation();
    }
}
