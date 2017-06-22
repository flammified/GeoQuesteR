package nl.alexanderfreeman.geoquester;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;

public class GeoQuestLocationFetchService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 60 * 1000; // 10 minutes

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    // Check user first, most efficient.
                    if (user == null) {
                        return;
                    }

                    SmartLocation.with(getApplicationContext()).location().oneFix().start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(final Location location) {

                            final DatabaseReference questref = FirebaseDatabase.getInstance().getReference("quests");
                            questref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    ArrayList<GeoQuest> close_quests = new ArrayList<GeoQuest>();

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                                        if (!child.child("found/" + user.getUid()).exists()) {
                                            GeoQuest quest = child.getValue(GeoQuest.class);
                                            Location l = new Location("");
                                            l.setLongitude(quest.getLongitude());
                                            l.setLatitude(quest.getLatitude());

                                            if (l.distanceTo(location) < 100) {
                                                close_quests.add(quest);
                                            }
                                        }
                                    }

                                    String text;

                                    if (close_quests.size() == 1) {
                                        text = "There is " + close_quests.size() + " GeoQuest nearby.";
                                    } else {
                                        text = "There are " + close_quests.size() + " GeoQuest(s) nearby.";
                                    }

                                    text = text + "Click to go to the app.";

                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(GeoQuestLocationFetchService.this)
                                                    .setSmallIcon(R.drawable.ic_stat_explore)
                                                    .setContentTitle("GeoQuests nearby")
                                                    .setContentText("");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
//


//@Override
//public void onCancelled(DatabaseError databaseError) {
//
//        }
//        });
//        }
//        });
