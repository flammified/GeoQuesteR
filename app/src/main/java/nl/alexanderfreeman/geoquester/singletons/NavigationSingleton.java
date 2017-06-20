package nl.alexanderfreeman.geoquester.singletons;

import nl.alexanderfreeman.geoquester.beans.GeoQuest;

/**
 * Created by A on 20-6-2017.
 */

public class NavigationSingleton {

    private static NavigationSingleton instance;

    private GeoQuest quest;

    private NavigationSingleton () {

    }

    public static NavigationSingleton getInstance() {
        if (instance == null) {
            instance = new NavigationSingleton();
        }
        return instance;
    }

    public GeoQuest getQuest() {
        return quest;
    }

    public void setQuest(GeoQuest quest) {
        this.quest = quest;
    }
}
