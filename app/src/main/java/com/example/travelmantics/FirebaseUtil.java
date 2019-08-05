package com.example.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static String storageRef = "holidayImages";
    private static final int RC_SIGN_IN = 10101;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseFirestore mFirestore;
    public static CollectionReference mCollectionReference;
    private static FirebaseUtil firebaseUtil;
    public static List<TravelDeals> mDeals;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    private static Activity calledActivity;

    private FirebaseUtil() {
    }

    public static void openFirebaseReference(String ref, final Activity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirestore = FirebaseFirestore.getInstance();
            mStorage = FirebaseStorage.getInstance();
            mAuth = FirebaseAuth.getInstance();
            calledActivity = callerActivity;
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null)FirebaseUtil.signIn();
                    Toast.makeText(callerActivity.getBaseContext(),"Welcome back!",Toast.LENGTH_LONG).show();
                }
            };

        }
        mDeals = new ArrayList<>();
        mCollectionReference = mFirestore.collection(ref);
        mStorageRef = mStorage.getReference(storageRef);
    }

    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );
// Create and launch sign-in intent

        calledActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    public static void attachListener(){
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    public static void detachListener(){
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

}
