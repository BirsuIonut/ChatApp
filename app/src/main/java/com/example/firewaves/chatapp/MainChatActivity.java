package com.example.firewaves.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

import Model.Model;
import Model.User;
import Util.API;

public class MainChatActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupTabLayout();

        // if the user is not logged in, start the login activity
        if (!API.getInstance().isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            // else, setup the user model
            FirebaseUser firebaseUser = API.getInstance().getCurrentUser();
            Model.user = new User(firebaseUser);

            // add the user to the database
            API.getInstance().addUserToDatabase(Model.user);

            // subscribe to notifications by using your user id as the topic
            API.getInstance().subscribeToTopic(Model.user.getId());
        }

        showContacts();
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setOnTabSelectedListener(this);
    }

    public void showContacts() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_container, ContactFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                // unsubscribe from push on this account
                API.getInstance().unsubscribeFromTopic(Model.user.getId());

                // sign out
                API.getInstance().signOut();

                // invalidate the user model
                Model.user = new User();

                // start the login activity
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }
}