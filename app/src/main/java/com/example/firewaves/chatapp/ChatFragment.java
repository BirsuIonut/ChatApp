
package com.example.firewaves.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import Model.Message;
import Model.Model;
import Model.User;
import Util.API;
import Util.Constants;

public class ChatFragment extends Fragment {

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private Button mSendButton;
    private EditText mMessageEditText;
    private ProgressBar mProgressBar;


    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFIrebaseAdapter;
    private String mChatKey;
    private static User mOtherUser;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(User otherUser) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        mOtherUser = otherUser;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!setupChatKey()) {
            return;
        }

        setupViews();

        setupFirebaseAdapter();

    }


    public boolean setupChatKey() {
        if (Model.user == null || mOtherUser == null) {
            Toast.makeText(getActivity(), "There was a problem setting up the users", Toast.LENGTH_LONG)
                 .show();
            return false;
        }

        // chat key?
        // user1:user2
        // the order of user 1 and 2 is given by alphabetically sorted ids

        final String[] chatUsers = new String[]{
            Model.user.getId(), mOtherUser.getId()
        };

        Arrays.sort(chatUsers);
        mChatKey = chatUsers[0] + ":" + chatUsers[1];

        return true;
    }

    private void setupViews() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        // set the message to appear from bottom to top
        mLinearLayoutManager.setStackFromEnd(true);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) getView().findViewById(R.id.messageRecyclerView);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mMessageEditText = (EditText) getView().findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // enable the send button only if there is text in the textfield
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) getView().findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a new message
                Message message = new Message(mMessageEditText.getText().toString(), Model.user
                        .getId(), mOtherUser.getId());

                // add the message to the database
                API.getInstance().getDatabaseReference()
                        .child(Constants.CHATS_CHILD)
                        .child(mChatKey)
                        .child(Constants.MESSAGES_CHILD)
                        .push().setValue(message);

                // reset the text field
                mMessageEditText.setText("");

                // send notification
                new SendNotificationTask(mOtherUser.getId(), Model.user.getName(), message.getText(), Model.user.getId()).execute();


            }
        });
    }


    private void setupFirebaseAdapter() {
        // create firebase adapter
        mFIrebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.item_message, MessageViewHolder.class,
                API.getInstance().getDatabaseReference()
                                 .child(Constants.CHATS_CHILD)
                                 .child(mChatKey)
                                 .child(Constants.MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
                // hide progress bar if we have a message
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                // set data
                viewHolder.messageTextView.setText(message.getText());
                viewHolder.nameTextView.setText(
                        message.getFrom().equals(Model.user.getId()) ? Model.user.getName(): mOtherUser.getName()
                );

                // our messages are on the right
                //
                if (message.getFrom().equals(Model.user.getId())) {
                    // set gravity to right
                    ((LinearLayout) viewHolder.messageTextView.getParent())
                            .setGravity(Gravity.RIGHT);
                    viewHolder.messageTextView.setGravity(Gravity.RIGHT);
                    viewHolder.nameTextView.setGravity((Gravity.RIGHT));
                    viewHolder.messageContainer.setGravity((Gravity.RIGHT));
                    // set bubble to right
                    viewHolder.bubbleView.setBackgroundResource(R.drawable.bubble_right);

                } else {
                    // set the bubble to left
                    viewHolder.bubbleView.setBackgroundResource(R.drawable.bubble_left);
                }
            }
        };

        mFIrebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);

                int messageCount = mFIrebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                //
                // scroll to the bottom of the list to show newly added message
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount-1) && lastVisiblePosition == (positionStart -1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }

            }
        });

        // hide progress bar
        API.getInstance().getDatabaseReference()
                .child(Constants.CHATS_CHILD)
                .child(mChatKey)
                .child(Constants.MESSAGES_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null) {
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mMessageRecyclerView.setAdapter(mFIrebaseAdapter);
    }

}
