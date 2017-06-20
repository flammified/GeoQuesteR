package nl.alexanderfreeman.geoquester.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import nl.alexanderfreeman.geoquester.singletons.Utility;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */
public class GeoQuestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView name;
    private TextView distance;
    private TextView coordinates;
    private GeoQuest quest;
    private QuestClickListener listener;

    public GeoQuestHolder(View itemView, QuestClickListener listener) {
        super(itemView);

        this.name = (TextView) itemView.findViewById(R.id.quest_name);
        this.distance = (TextView) itemView.findViewById(R.id.quest_distance);
        this.coordinates = (TextView) itemView.findViewById(R.id.quest_coordinates);
        this.itemView.setOnClickListener(this);
        this.listener = listener;
    }

    public void bindQuest(GeoQuest quest) {

        this.quest = quest;

        name.setText(quest.getName());

        if (quest.getDistance() == -1) {
            distance.setText("? km");
        }
        else {
            distance.setText("" + quest.getDistance() / 1000 + " km");
        }

        double lat = quest.getLatitude();
        double lon = quest.getLongitude();
        coordinates.setText(Utility.convert(lat, lon));
    }

    @Override
    public void onClick(View v) {
        listener.OnQuestClicked(quest);
    }
}
