package nl.alexanderfreeman.geoquester.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nl.alexanderfreeman.geoquester.recycler.GeoQuestAdapter;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.recycler.QuestClickListener;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class NotFoundFragment extends Fragment implements QuestClickListener {

    private GeoQuestAdapter questadapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quests_fragment, container, false);

        ArrayList<GeoQuest> list = new ArrayList<GeoQuest>();
        GeoQuest e = new GeoQuest("Test2", "Test", 52.2, 5.3, "www.google.nl", "5123123");
        list.add(e);




        questadapter = new GeoQuestAdapter(list, this);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(questadapter);
        return root;
    }

    @Override
    public void OnQuestClicked(GeoQuest q) {

    }
}
