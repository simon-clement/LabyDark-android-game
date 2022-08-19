package com.blblbl.torus.game;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.blblbl.torus.interaction_game.MainActivity;

public class GameManager extends IntentService{
    /*
    * This class must be used through its static members:
    * reinit and tryAgain, to create, resume or restart a game,
    * changeDir to change the angularSpeed and prepareJump... to jump.
    * you will need to call startService to begin the calculs: (in onResume)
        Activity.startService(new Intent(mActivity,GameManager.class));
     */
    private final static String TAG="GameManager";


    public static void reinit(MainActivity activity) {//resume a game
        allocateIfNull();
        mActivity=activity;
        initialized=true;
    }
    public static void tryAgain(MainActivity activity) {//(re)start a game
        mActivity=activity;
        initialized=false;
        allocateIfNull();
        Physics.mSlowed = false;
        Physics.mDead = false;
        mBalleManager.init();
        mEnvironnement.init(mBalleManager.getPos());
        mActivity.resetDied();
        initialized=true;
    }

    public static void changeDir(float angle) {
        if (initialized)
            mBalleManager.changeDir(angle);
    }
    public static void prepareJump() {
        Physics.mJumping=true;
    }

    public static boolean stopCollisions;
    public static int mCase;
    public static BalleManager getBall() { return mBalleManager; }
    public static Environnement getEnvironnement() { return mEnvironnement; }



    private void onDrawFrame() {
        if (initialized) {
            if (!Physics.mDead) {
                Physics.update(mEnvironnement, mBalleManager,mActivity);
                mBalleManager.updateRotationAngle();
                int newCase = mEnvironnement.numeroCaseBalle(mBalleManager.getPos());
                if (newCase != mCase) {
                    mCase = newCase;
                    //envoyerServeur("pos:"+mCase);
                }
            } else {
                //envoyerServeur("dead");
            }
        }
    }

    public GameManager() {
        super("ServiceCollision");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //mUDP_Client = new UDP_Client();
        Log.d(TAG,"initialization");
        stopCollisions=false;
        //TCP_Server.StartServer();
        //(new UDP_Client()).NachrichtSenden();
        while (!stopCollisions && initialized) {
            try {
                onDrawFrame();
            } catch (Exception e) {
                sendToast(e.toString());
                e.printStackTrace();
                stopCollisions=true;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                stopCollisions=true;
            }
        }
        invalidateData();
    }

    private void invalidateData() {
        initialized=false;
        stopCollisions=true;
    }

    private void sendToast(String mess) {
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                String error =(String) message.obj;
                Toast.makeText(mActivity,error,Toast.LENGTH_SHORT).show();
            }
        };
        Message message = mHandler.obtainMessage();
        message.obj=mess;
        message.sendToTarget();
    }

    private static void allocateIfNull() {
        /**
         * Here we allocate the Ball and the environnement if they are null.
         * It is called in the different functions that init the GameManager!
         */
        if (mBalleManager == null)
            mBalleManager = new BalleManager();
        if (mEnvironnement == null)
            mEnvironnement = new Environnement(20,45);//TODO mettre 100 pour debug
    }
/*
    private void envoyerServeur(String message) {
        mUDP_Client.setMessage(message);
        mUDP_Client.NachrichtSenden();
    }*/

    private static boolean initialized;
    private static Environnement mEnvironnement;
    private static BalleManager mBalleManager;
    private static MainActivity mActivity;
   // private UDP_Client mUDP_Client;


    //note: since we are in a different thread, don't use mAct directly
}
