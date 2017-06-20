package nl.alexanderfreeman.geoquester.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.quest.Quest;

import java.util.ArrayList;

import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by A on 18-6-2017.
 */

public class GeoQuestAdapter extends RecyclerView.Adapter<GeoQuestHolder> {

    private ArrayList<GeoQuest> quests;
    private QuestClickListener listener;

    public GeoQuestAdapter(ArrayList<GeoQuest> quests, QuestClickListener listener) {
        this.quests = quests;
        this.listener = listener;
    }

    @Override
    public GeoQuestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.geoquest_item, parent, false);
        return new GeoQuestHolder(inflatedView, listener);
    }

    @Override
    public void onBindViewHolder(GeoQuestHolder holder, int position) {
        GeoQuest quest = quests.get(position);
        holder.bindQuest(quest);
    }

    @Override
    public int getItemCount() {
        return quests.size();
    }
}
