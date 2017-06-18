package nl.alexanderfreeman.geoquester;

import android.widget.Adapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import nl.alexanderfreeman.geoquester.beans.GeoQuest;

import static android.R.id.list;

/**
 * Created by Alexander Freeman on 18-6-2017.
 */

public class GeoQuestDatabaseHelper {

    public static ArrayList<GeoQuest> insert_found_quests(String uid, Adapter adapter) {
        ArrayList<GeoQuest> list = new ArrayList<GeoQuest>();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();


        return list;
    }

}
