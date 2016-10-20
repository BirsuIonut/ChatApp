package com.example.firewaves.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;

import Model.Model;
import Model.User;
import Util.API;
import Util.Constants;


public class ContactFragment extends Fragment {
    private ProgressBar mProgressBar;
    private FirebaseListAdapter<User> mFirebaseAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);

        // setup the list view
        ListView usersListView = (ListView) getView().findViewById(R.id.usersListView);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the selected user

                User userClicked = mFirebaseAdapter.getItem(position);

                    // replace the contacts fragment with the chat fragment
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                            .beginTransaction();
                    Fragment chatFragment = ChatFragment.newInstance(userClicked);
                    fragmentTransaction.replace(R.id.home_container, chatFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

            }
        });

        // create the firebase adapter for the listview
        mFirebaseAdapter = new FirebaseListAdapter<User>(getActivity(), User.class,
                R.layout.layout_user, API.getInstance().getDatabaseReference()
                .child(Constants.USERS_CHILD)) {
            @Override
            protected void populateView(View v, User model, int position) {
                // hide he progress bar
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                    // setup the user view
                    TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
                    ImageView photoImageView = (ImageView) v.findViewById(R.id.photoImageView);

                    nameTextView.setText(model.getName());

                    if (!model.getPhotoUrl().isEmpty()) {
                        Glide.with(ContactFragment.this)
                                .load(model.getPhotoUrl())
                                .placeholder(R.drawable.ic_person_black_24dp)
                                .into(photoImageView);
                    }

            }
        };
        usersListView.setAdapter(mFirebaseAdapter);
    }

}
