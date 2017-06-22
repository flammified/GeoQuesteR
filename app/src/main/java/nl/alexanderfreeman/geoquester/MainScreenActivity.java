package nl.alexanderfreeman.geoquester;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.alexanderfreeman.geoquester.beans.GeoQuest;
import nl.alexanderfreeman.geoquester.fragments.AccountInfoFragment;
import nl.alexanderfreeman.geoquester.fragments.CongratsFragment;
import nl.alexanderfreeman.geoquester.fragments.GeoQuestInformationFragment;
import nl.alexanderfreeman.geoquester.fragments.NavigationFragment;
import nl.alexanderfreeman.geoquester.fragments.QuestListFragment;
import nl.alexanderfreeman.geoquester.fragments.ScanFragment;

/**
 * Created by Alexander Freeman on 11-6-2017.
 */

public class MainScreenActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navview;

    // Create a field that marks that something needs to be displayed
    // after drawer closes. When null means no action (drawer closed
    // by the user, not by selecting an item)
    Integer itemIdWhenClosed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navview = (NavigationView) findViewById(R.id.navigation);

        configureFragment();
        configureToolbar();
        configureNavigationDrawer();
    }

    private void configureFragment() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.frame, new AccountInfoFragment());
        tx.commit();
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayShowTitleEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,              /* host Activity */
                drawerLayout,      /* DrawerLayout object */
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(mDrawerToggle);
        drawerLayout.addDrawerListener(this);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mDrawerToggle.syncState();
    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                itemIdWhenClosed = item.getItemId(); // Mark action when closed
                drawerLayout.closeDrawer(GravityCompat.START); // And drawer closing simultaneously

                int size = navview.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navview.getMenu().getItem(i).setChecked(false);
                }

                navview.getMenu().findItem(item.getItemId()).setChecked(true);
                return true;
            }
        });

        navview.getMenu().clear();
        navview.inflateMenu(R.menu.main_navigation_menu);
        navview.getMenu().getItem(0).setChecked(true);

        View header = navview.getHeaderView(0);
        CircleImageView circleimageview = (CircleImageView) header.findViewById(R.id.header_profile_image);
        TextView email = (TextView) header.findViewById(R.id.header_email);
        TextView username = (TextView) header.findViewById(R.id.header_username);

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        email.setText(fbUser.getEmail());
        username.setText(fbUser.getDisplayName());
        Picasso.with(this).load(fbUser.getPhotoUrl().toString()).into(circleimageview);
    }

    /* Signs a user out gracefully */
    public void signout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("DEBUG", "User Logged out");
                                Intent intent = new Intent(MainScreenActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("DEBUG", "Google API Client Connection Suspended");
            }
        });
    }

    //Needed to make my navigationdrawer fluid ;)
    @Override
    public void onDrawerClosed(View view) {
        // If id to show is marked, perform action
        if (itemIdWhenClosed != null) {
            displaySelectedScreen(itemIdWhenClosed);
            // Reset value
            itemIdWhenClosed = null;
        }
    }

    // Now here just commit, don't close the drawer since this is already
    // fired when it's closed
    private void displaySelectedScreen(int itemId) {

        final Fragment fragment;

        if (itemId == R.id.drawer_account) {
            fragment = new AccountInfoFragment();
        }
        else if (itemId == R.id.drawer_quests) {
            fragment = new QuestListFragment();
        }
        else if (itemId == R.id.drawer_navigation) {
            fragment = new NavigationFragment();
        }
        else if (itemId == R.id.drawer_scan) {
            fragment = new ScanFragment();
        }
        else if (itemId == R.id.drawer_signout) {
            signout();
            return;
        }
        else {
            fragment = new AccountInfoFragment();
            Toast.makeText(getApplicationContext(), "Congratulations, you broke it.", Toast.LENGTH_LONG);
        }
//        getSupportFragmentManager().popBackStack();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    public void switch_to_quest(GeoQuest q) {
        Bundle data = new Bundle();//create bundle instance
        data.putSerializable("quest", q);
        GeoQuestInformationFragment gqif = new GeoQuestInformationFragment();
        gqif.setArguments(data);

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, gqif, "GeoQuestInformationFragment");
        ft.commit();
    }

    //Unneeded overrides, shoo.

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }


    public void set_to_navigation() {
        displaySelectedScreen(R.id.drawer_navigation);
    }

    public void switch_to_congrats(GeoQuest quest, String id) {
        Bundle data = new Bundle();//create bundle instance
        data.putSerializable("quest", quest);
        data.putSerializable("questid", id);
        CongratsFragment congrats = new CongratsFragment();
        congrats.setArguments(data);

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, congrats, "congrats");
        ft.commit();
    }
}
