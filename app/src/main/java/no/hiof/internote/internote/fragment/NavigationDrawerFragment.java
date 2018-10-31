package no.hiof.internote.internote.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import no.hiof.internote.internote.AboutUsActivity;
import no.hiof.internote.internote.LoginActivity;
import no.hiof.internote.internote.R;
import no.hiof.internote.internote.SettingsActivity;

public class NavigationDrawerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    public NavigationDrawerFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeTwo);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    public void setUpDrawer(DrawerLayout setDrawerLayout, Toolbar toolbar) {
        drawerLayout = setDrawerLayout;
        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_settings:
                Intent intentSettings = new Intent(getContext(), SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent startLoginActivity = new Intent(getContext(), LoginActivity.class);
                startActivity(startLoginActivity);
                break;
            case R.id.nav_about_us:
                Intent intentAboutUs = new Intent(getContext(), AboutUsActivity.class);
                startActivity(intentAboutUs);
                break;
        }
        return false;
    }
}