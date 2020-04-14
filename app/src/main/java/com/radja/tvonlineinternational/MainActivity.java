package com.radja.tvonlineinternational;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.radja.adapter.NavAdapter;
import com.radja.fragment.AllChannelFragment;
import com.radja.fragment.CategoryFragment;
import com.radja.fragment.FavouriteFragment;
import com.radja.fragment.FeaturedFragment;
import com.radja.fragment.HomeFragment;
import com.radja.fragment.LatestFragment;
import com.radja.fragment.SearchFragment;
import com.radja.fragment.VideoFragment;
import com.radja.item.ItemNav;
import com.radja.util.BannerAds;
import com.radja.util.Constant;
import com.radja.util.NetworkUtils;
import com.radja.util.RecyclerTouchListener;
import com.ixidev.gdpr.GDPRChecker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    public NavAdapter navAdapter;
    private FragmentManager fragmentManager;
    ArrayList<ItemNav> mNavItem;
    BottomNavigationView navigation;
    DrawerLayout drawer;
    MyApplication MyApp;
    TextView textName, textEmail;
    int previousSelect = 0;
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout mAdViewLayout;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    navigationItemSelected(0);
                    loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                    return true;
                case R.id.navigation_tv:
                    AllChannelFragment allChannelFragment = new AllChannelFragment();
                    navigationItemSelected(0);
                    loadFrag(allChannelFragment, getString(R.string.menu_live_tv), fragmentManager);
                    return true;
                case R.id.navigation_video:
                    VideoFragment videoFragment = new VideoFragment();
                    navigationItemSelected(4);
                    loadFrag(videoFragment, getString(R.string.menu_video), fragmentManager);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdViewLayout = findViewById(R.id.adView);
        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavItem = new ArrayList<>();
        fillNavItem();
        textName = findViewById(R.id.nav_name);
        textEmail = findViewById(R.id.nav_email);
        RecyclerView recyclerView = findViewById(R.id.navigation_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        navAdapter = new NavAdapter(MainActivity.this, mNavItem);
        recyclerView.setAdapter(navAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                navigationClick(mNavItem.get(position).getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

        if (NetworkUtils.isConnected(MainActivity.this)) {
            getAppConsent();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                hideShowBottomView(false);
                String categoryName = getString(R.string.menu_search);
                Bundle bundle = new Bundle();
                bundle.putString("search", arg0);

                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.hide(fragmentManager.findFragmentById(R.id.Container));
                ft.add(R.id.Container, searchFragment, categoryName);
                ft.addToBackStack(categoryName);
                ft.commit();
                setToolbarTitle(categoryName);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void navigationClick(int position) {
        drawer.closeDrawers();
        switch (position) {
            case 0:
                navigationItemSelected(0);
                navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
                HomeFragment homeFragment = new HomeFragment();
                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                hideShowBottomView(true);
                break;
            case 1:
                navigationItemSelected(1);
                LatestFragment latestFragment = new LatestFragment();
                loadFrag(latestFragment, getString(R.string.menu_latest), fragmentManager);
                hideShowBottomView(false);
                break;
            case 2:
                navigationItemSelected(2);
                CategoryFragment categoryFragment = new CategoryFragment();
                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                hideShowBottomView(false);
                break;
            case 3:
                navigationItemSelected(3);
                FeaturedFragment featuredFragment = new FeaturedFragment();
                loadFrag(featuredFragment, getString(R.string.menu_featured), fragmentManager);
                hideShowBottomView(false);
                break;
            case 4:
                navigation.getMenu().findItem(R.id.navigation_video).setChecked(true);
                navigationItemSelected(4);
                VideoFragment videoFragment = new VideoFragment();
                loadFrag(videoFragment, getString(R.string.menu_video), fragmentManager);
                hideShowBottomView(true);
                break;
            case 5:
                navigationItemSelected(5);
                FavouriteFragment favouriteFragment = new FavouriteFragment();
                loadFrag(favouriteFragment, getString(R.string.menu_favourite), fragmentManager);
                hideShowBottomView(false);
                break;
            case 10:
                navAdapter.setSelected(previousSelect);
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;


            case 13:
                navigationItemSelected(previousSelect);
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
    }

    private void fillNavItem() {
        mNavItem.add(new ItemNav(0, R.drawable.ic_home, getResources().getString(R.string.menu_home)));
        mNavItem.add(new ItemNav(1, R.drawable.ic_latest, getResources().getString(R.string.menu_latest)));
        mNavItem.add(new ItemNav(2, R.drawable.ic_category, getResources().getString(R.string.menu_category)));
        mNavItem.add(new ItemNav(3, R.drawable.ic_featured, getResources().getString(R.string.menu_featured)));
        mNavItem.add(new ItemNav(4, R.drawable.ic_video, getResources().getString(R.string.menu_video)));
        mNavItem.add(new ItemNav(5, R.drawable.ic_favourite, getResources().getString(R.string.menu_favourite)));
        mNavItem.add(new ItemNav(13, R.drawable.ic_setting, getResources().getString(R.string.menu_setting)));
        if (MyApp.getIsLogin()) {
            mNavItem.add(new ItemNav(10, R.drawable.ic_profile, getResources().getString(R.string.menu_profile)));
            mNavItem.add(new ItemNav(11, R.drawable.ic_logout, getResources().getString(R.string.menu_logout)));
        } else {
            mNavItem.add(new ItemNav(12, R.drawable.ic_login, getResources().getString(R.string.login)));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        View v1 = findViewById(R.id.view_fake);
        v1.requestFocus();
        if (MyApp.getIsLogin()) {
            textName.setText(MyApp.getUserName());
            textEmail.setText(MyApp.getUserEmail());
        }
    }


    private void getAppConsent() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constant.API_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    Constant.isBanner = objJson.getBoolean("banner_ad");
                    Constant.isInterstitial = objJson.getBoolean("interstital_ad");
                    Constant.adMobBannerId = objJson.getString("banner_ad_id");
                    Constant.adMobInterstitialId = objJson.getString("interstital_ad_id");
                    Constant.adMobPublisherId = objJson.getString("publisher_id");
                    Constant.AD_COUNT_SHOW = objJson.getInt("interstital_ad_click");

                    new GDPRChecker()
                            .withContext(MainActivity.this)
                            .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
                            .withPublisherIds(Constant.adMobPublisherId) // your admob account Publisher id
                            .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                            .check();

                    BannerAds.ShowBannerAds(MainActivity.this, mAdViewLayout);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        //  ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void hideShowBottomView(boolean visibility) {
        navigation.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mAdViewLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
        navAdapter.setSelected(position);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            //when search is click and goes back if home
            assert tag != null;
            if (tag.equals(getString(R.string.menu_home)) || tag.equals(getString(R.string.menu_live_tv)) || tag.equals(getString(R.string.menu_video))) {
                hideShowBottomView(true);
            }
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
