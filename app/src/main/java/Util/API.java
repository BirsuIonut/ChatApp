package Util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.firewaves.chatapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Vector;

import Model.User;

/**
 * Created by Ady on 7/18/2016.
 */
public class API {

    public static final String TAG = "API";

    private GoogleApiClient mGoogleApiClient;

    private static API mInstance;

    private API() {
    }

    public static API getInstance() {
        if (mInstance == null) {
            mInstance = new API();
        }

        return mInstance;
    }
    public static Vector<String> getFriends(String userId){
        Vector<String> firebaseFriends = new Vector<String>();





        return firebaseFriends;
    }
    /**
     * Is used to login with your Google account, it will display the account picker when called
     *
     * @param context
     * @param listener
     */
    public void showLoginPopup(FragmentActivity context,
                               GoogleApiClient.OnConnectionFailedListener listener) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context /* FragmentActivity */, listener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        context.startActivityForResult(signInIntent, Constants.SIGN_IN_CODE);
    }

    /**
     * Used to authenticate your firebase app with your google account
     *
     * @param context
     * @param activityToStartAfterLogin
     * @param resultIntent the result intent from the google account picker
     */
    public void firebaseAuthWithGoogle(final Activity context,
                                       final Class activityToStartAfterLogin, Intent resultIntent) {

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(resultIntent);

        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {

                Log.d(TAG, "firebaseAuthWithGooogle:" + account.getId());

                AuthCredential credential = GoogleAuthProvider
                        .getCredential(account.getIdToken(), null);

                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in
                                // succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithCredential", task.getException());
                                    Toast.makeText(context, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    context.startActivity(new Intent(context,
                                            activityToStartAfterLogin));
                                    context.finish();
                                }
                            }
                        });
            } else {
                Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Google Sign In failed
            Toast.makeText(context, "Google Sign In failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks whether the user is logged in or not
     *
     * @return
     */
    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * Returns the current logged user
     *
     * @return
     */
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Signs out
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).then (
                new ResultTransform<Status, Result>() {
                        @Nullable
                        @Override
                        public PendingResult<Result> onSuccess(@NonNull Status status) {
                            return null;
                        }
                    });
        }
    }

    /**
     * Adds the given user to the database
     *
     * @param user
     */
    public void addUserToDatabase(User user) {
        FirebaseDatabase.getInstance().getReference().child(Constants.USERS_CHILD)
                .child(user.getId()).setValue(user);
    }

    /**
     * Returns the firebase database reference
     *
     * @return
     */
    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Subscribes the user to push notifications on the given topic
     *
     * @param topic
     */
    public void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    /**
     * Unsubscribes from push notifications on the given topic
     *
     * @param topic
     */
    public void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

}