package nl.alexanderfreeman.geoquester.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.MainScreenActivity;
import nl.alexanderfreeman.geoquester.recycler.GeoQuestAdapter;
import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.recycler.QuestClickListener;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class FoundFragment extends Fragment implements OnLocationUpdatedListener, QuestClickListener {

    private GeoQuestAdapter questadapter;
    private ArrayList<GeoQuest> list;
    private Location lastKnownLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.quests_fragment, container, false);

        SmartLocation.with(getContext()).location().start(this);

        if (savedInstanceState != null) {
            ArrayList<GeoQuest> quests = (ArrayList<GeoQuest>) savedInstanceState.getSerializable("quests");
            if (quests != null) {
                list = quests;
            }
            else {
                list = new ArrayList<GeoQuest>();
            }
        }
        else {
            list = new ArrayList<GeoQuest>();
        }
        questadapter = new GeoQuestAdapter(list, this);

        Log.d("DEBUG", "OnCreateView");
        refresh();

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(questadapter);
        return root;
    }

    public void onStart() {
        super.onStart();
        Log.d("DEBUG", "Onstart?");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("quests", list);
    }

    @Override
    public void onLocationUpdated(Location location) {
        lastKnownLocation = location;
        Log.d("DEBUG", lastKnownLocation.toString());
        for (GeoQuest q : list) {
            Location l = new Location("");
            l.setLatitude(q.getLatitude());
            l.setLatitude(q.getLongitude());
            Log.d("DEBUG", l.toString());
            Log.d("DEBUG", "" + l.distanceTo(lastKnownLocation));
            q.setDistance(l.distanceTo(lastKnownLocation));
        }
        questadapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {

        questadapter.notifyDataSetChanged();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference("quests/");

        Log.d("DEBUG", "Updating database");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot listSnapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : listSnapshot.getChildren()) {
                    GeoQuest quest = dataSnapshot.getValue(GeoQuest.class);
                    Location quest_location = new Location("");
                    Log.d("DEBUG", ""+ quest.getLongitude());
                    quest_location.setLongitude(quest.getLongitude());
                    quest_location.setLatitude(quest.getLatitude());

                    if (lastKnownLocation != null) {
                        quest.setDistance(quest_location.distanceTo(lastKnownLocation));
                    } else {
                        quest.setDistance(-1);
                    }
                    list.add(quest);
                }

                questadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void OnQuestClicked(GeoQuest q) {

        ((MainScreenActivity) getActivity()).switch_to_quest(q);

    }
}
