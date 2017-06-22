package nl.alexanderfreeman.geoquester.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import nl.alexanderfreeman.geoquester.R;
import nl.alexanderfreeman.geoquester.utility.ProgressBarAnimation;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.beans.User;

/**
 * Created by Alexander Freeman on 21-6-2017.
 */

public class CongratsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.congrats_fragment, container, false);

        final GeoQuest quest = (GeoQuest) getArguments().getSerializable("quest");
        final String quest_id = (String) getArguments().getSerializable("questid");

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference userref = FirebaseDatabase.getInstance().getReference("users/" + uid);
        final DatabaseReference questref = FirebaseDatabase.getInstance().getReference("quests/" + quest_id);

        final ProgressBar bar = (ProgressBar) root.findViewById(R.id.congrats_level);
        final TextView level = (TextView) root.findViewById(R.id.level_text);

        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // No giving points twice if the view reloads by accident.
                if (dataSnapshot.exists()) {

                    if (!dataSnapshot.child("found/" + quest_id).exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        bar.setProgress(user.getPoints());

                        int points = user.points + 30;

                        ProgressBarAnimation anim = new ProgressBarAnimation(bar, user.points, Math.min(points, 100));
                        anim.setDuration(5000);
                        bar.startAnimation(anim);

                        if (points > 100) {
                            points -= 100;
                            user.level += 1;
                        }


                        level.setText("Level " + user.level + " Quester!");

                        user.points = points;
                        user.count += 1;
                        userref.setValue(user);

                        Map<String, Object> found_quest = new HashMap<String, Object>();
                        found_quest.put("found/" + quest_id, true);
                        userref.updateChildren(found_quest);

                        Map<String, Object> user_to_add = new HashMap<String, Object>();
                        user_to_add.put("found/" + uid, true);
                        questref.updateChildren(user_to_add);
                    }
                }
                else {
                    Toast.makeText(getContext(), "user doesnt exist wtf", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }
}
