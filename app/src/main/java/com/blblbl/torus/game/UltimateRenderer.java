package com.blblbl.torus.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import com.blblbl.torus.interaction_game.GameView;
import com.blblbl.torus.interaction_game.MainActivity;
import com.blblbl.torus.utils.Vect;

import org.apache.http.conn.ConnectTimeoutException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class UltimateRenderer implements GLSurfaceView.Renderer {
    protected final static String TAG="UltimateRenderer";
    public UltimateRenderer(Context context,GameView View) {
        try {
            mGameView=View;
            mContext = context;
            mAfficheur = new Afficheur(context);
            initialized=false;
        } catch (Exception e) {
            sendToast("can't initialize Renderer Object");
            e.printStackTrace();
        }
    }
    public UltimateRenderer(Context context) {
        try {
            mGameView=null;
            mContext = context;
            mAfficheur = new Afficheur(context);
            initialized=false;
        } catch (Exception e) {
            sendToast("can't initialize Renderer Object");
            e.printStackTrace();
        }
    }

    public void invalidate() { initialized=false;}

    public void linkWithWorld(BalleManager balleManager, Environnement environnement) {
        mBalleManager=balleManager;
        mEnvironnement=environnement;
        mCiel=new Ciel();
        initialized=true;
    }

    public void slowTime() {
        Physics.slowTime(mBalleManager);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
            if (initialized) {
                mAfficheur.initForDraw();//pure opengl to begin!
                mAfficheur.prepareForBall(Physics.mSlowed);

                mBalleManager.keepTime();
                Physics.avancerRapidement(mEnvironnement,mBalleManager,mCiel);
                mSavePosBall = mEnvironnement.getPosBalleAffichable(mBalleManager.getPos());//ball's position affichable
                int newCaseBall = mEnvironnement.numeroCaseBalle(mBalleManager.getPos());

                if (newCaseBall != mSaveCaseBalle) {
                    mEnvironnement.updateCaseObstacles(newCaseBall);
                    mSaveCaseBalle = newCaseBall;
                }

                long time=mBalleManager.getNewTime();

                Vect reculCamera = mBalleManager.getDir().by(-25);

                GLES20.glDepthFunc(GLES20.GL_ALWAYS);
                mAfficheur.setViewMatrix(reculCamera,new Vect(0,0,0));
                mAfficheur.drawSky(mCiel);
                mAfficheur.drawBall(mBalleManager);

                mAfficheur.prepareForEverythingElseThanBall(Physics.mSlowed);

                mAfficheur.setViewMatrix(reculCamera,new Vect(0,0,0));
                mAfficheur.setCentreCourbure(0,0,0);

                mEnvironnement.drawIndications(mAfficheur, mSavePosBall, mSaveCaseBalle);

                mAfficheur.setViewMatrix(reculCamera,mSavePosBall);
                mAfficheur.setCentreCourbure(mSavePosBall.at(0),mEnvironnement.getHeight(),mSavePosBall.at(2));
                GLES20.glDepthFunc(GLES20.GL_LESS);

                mEnvironnement.drawObjectifs(mAfficheur, mSavePosBall, mSaveCaseBalle, mBalleManager.getDir(),time);


                mEnvironnement.drawObstacles(mAfficheur, mSavePosBall, mBalleManager.getDir(),time);
                mAfficheur.drawGround();

                mEnvironnement.drawBonus(mAfficheur,mSaveCaseBalle,time);

                mGameView.addToCompteur();
            }
    }

    protected void sendToast(String mess) {
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                String error =(String) message.obj;
                Toast.makeText(mContext,error,Toast.LENGTH_SHORT).show();
            }
        };
        Message message = mHandler.obtainMessage();
        message.obj=mess;
        message.sendToTarget();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Log.v(TAG, "onSurfaceCreated Called");
        try {
            mAfficheur.init(mEnvironnement);
        } catch (Exception e) {
            sendToast(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (mGameView != null)
            mGameView.setNewScreen(width,height);
        float ratio = (float) width  / height;
        mAfficheur.PerspectiveM(ratio, 70);
        mAfficheur.setTailleEcran(width,height);
    }
    protected Environnement mEnvironnement;
    protected Afficheur mAfficheur;
    protected Ciel mCiel;
    protected BalleManager mBalleManager;

    protected Context mContext;
    protected GameView mGameView;
    protected Vect mSavePosBall;
    protected int mSaveCaseBalle;
    protected boolean initialized;

    //note: since we are in a different thread, don't use mAct directly
}
