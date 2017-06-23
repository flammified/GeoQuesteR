package nl.alexanderfreeman.geoquester.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.compassview.CompassSensorManager;
import nl.alexanderfreeman.geoquester.compassview.widget.CompassView;
import nl.alexanderfreeman.geoquester.singletons.NavigationSingleton;
import nl.alexanderfreeman.geoquester.utility.Utility;

public class CompassNavigationFragment extends Fragment implements OnLocationUpdatedListener {

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

		SmartLocation.with(getContext()).location().start(this);

		setHasOptionsMenu(true);

        return root;
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.to_map_menu, menu);
		super.onCreateOptionsMenu(menu,inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		((MainScreenActivity) getActivity()).switch_to_map();
		return true;
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        compassSensorManager.onResume();

        SmartLocation.with(getContext()).location().start(this);
    }

	@Override
	public void onLocationUpdated(Location my_location) {
//		if (my_location.isFromMockProvider()) {
//			Utility.fakeLocationDialogAndQuit(getActivity());
//		}
		GeoQuest quest = NavigationSingleton.getInstance().getQuest();
		if (quest != null) {
			distance.setText("" + quest.getLocation().distanceTo(my_location));
			cv.initializeCompass(compassSensorManager, my_location, quest.getLocation(), R.drawable.pijl);
		}

	}

    @Override
    public void onPause() {
        super.onPause();
        compassSensorManager.onPause();
        SmartLocation.with(getContext()).location().stop();
    }

}
