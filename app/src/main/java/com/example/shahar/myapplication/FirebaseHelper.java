package com.example.shahar.myapplication;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

class FirebaseHelper {
    private static final FirebaseHelper ourInstance = new FirebaseHelper();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final String TAG = "EmailPassword";


    static FirebaseHelper getInstance() {
        return ourInstance;
    }

    private FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    FirebaseAuth getmAuth() {
        return mAuth;
    }

    FirebaseUser createAccount(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
        return mAuth.getCurrentUser();
    }

    FirebaseUser signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                    }
                });
        return mAuth.getCurrentUser();
    }

    FirebaseUser signOutmAuth(){
        mAuth.signOut();
        return mAuth.getCurrentUser();
    }

    FirebaseUser UpdateCurrentUser(){
        if(mAuth.getCurrentUser()!=null) {
            mAuth.getCurrentUser()
                    .reload();
            currentUser = mAuth.getCurrentUser();
        }
        return currentUser;
    }

    FirebaseUser getCurrentUser() {
        if(currentUser==null)
            currentUser = mAuth.getCurrentUser();
        return currentUser;
    }
}
