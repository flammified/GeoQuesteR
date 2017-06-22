package nl.alexanderfreeman.geoquester.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.compassview.CompassSensorManager;
import nl.alexanderfreeman.geoquester.compassview.widget.CompassView;
import nl.alexanderfreeman.geoquester.singletons.NavigationSingleton;
import nl.alexanderfreeman.geoquester.utility.Utility;

public class NavigationFragment extends Fragment{

    protected CompassSensorManager compassSensorManager;

    TextView name;
    TextView coords;
    TextView distance;

    CompassView cv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.navigate_fragment, null);

        compassSensorManager = new CompassSensorManager(getActivity());
        cv = (CompassView) root.findViewById(R.id.compass);

        name = (TextView) root.findViewById(R.id.navigate_quest_name);
        coords = (TextView) root.findViewById(R.id.navigate_coordinates);
        distance = (TextView) root.findViewById(R.id.navigate_distance);

        GeoQuest quest = NavigationSingleton.getInstance().getQuest();
        refreshUI(quest);

        return root;
    }

    private void refreshUI(GeoQuest quest) {
        if (quest == null) {
            name.setText(R.string.no_quest);
            coords.setText("");
            distance.setText("");
        }
        else {
            name.setText(quest.getName());
            coords.setText(Utility.convert(quest.getLatitude(), quest.getLongitude()));
            distance.setText("");

            Location l = new Location("");
            l.setLatitude(quest.getLatitude());
            l.setLongitude(quest.getLongitude());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        compassSensorManager.onResume();

        SmartLocation.with(getContext()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        if (location.isFromMockProvider()) {
                            Utility.fakeLocationDialogAndQuit(getActivity());
                        }
                        GeoQuest quest = NavigationSingleton.getInstance().getQuest();
                        if (quest != null) {
                            Location l = new Location("");
                            l.setLatitude(quest.getLatitude());
                            l.setLongitude(quest.getLongitude());
                            distance.setText("" + l.distanceTo(location));
                            cv.initializeCompass(compassSensorManager, location, l, R.drawable.pijl);
                        }

                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        compassSensorManager.onPause();
        SmartLocation.with(getContext()).location().stop();
    }

}
