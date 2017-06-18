package nl.alexanderfreeman.geoquester;

import android.location.Location;
import android.location.LocationManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */
public class GeoQuestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView name;
    private TextView distance;
    private TextView coordinates;

    public GeoQuestHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.quest_name);
        distance = (TextView) itemView.findViewById(R.id.quest_distance);
        coordinates = (TextView) itemView.findViewById(R.id.quest_coordinates);
        itemView.setOnClickListener(this);
    }

    public void bindQuest(GeoQuest quest) {
        name.setText(quest.getName());
        distance.setText("" + quest.getDistance() + " meter");
        LatLng coord = quest.getCoordinates();
        coordinates.setText(convert(coord.latitude, coord.longitude));
    }

    private String convert(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");

        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

    @Override
    public void onClick(View v) {
        Log.d("RecyclerView", "CLICK!");
    }
}
