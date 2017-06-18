package nl.alexanderfreeman.geoquester.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import nl.alexanderfreeman.geoquester.GeoQuestAdapter;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class FoundFragment extends Fragment {

    private GeoQuestAdapter questadapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.notfound_fragment, container, false);

        ArrayList<GeoQuest> list = new ArrayList<GeoQuest>();
        GeoQuest e = new GeoQuest();
        e.setName("Eemnes");
        e.setDistance(5);
        e.setCoordinates(new LatLng(5.131231, 5.13414123));
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);
        list.add(e);




        questadapter = new GeoQuestAdapter(list);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.not_found_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(questadapter);
        return root;
    }
}
