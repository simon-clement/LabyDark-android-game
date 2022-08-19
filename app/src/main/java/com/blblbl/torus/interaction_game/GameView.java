package com.blblbl.torus.interaction_game;

import android.content.Intent;
import android.opengl.GLSurfaceView;

import android.os.SystemClock;
import android.view.MotionEvent;

import com.blblbl.torus.game.GameManager;
import com.blblbl.torus.game.Physics;
import com.blblbl.torus.game.UltimateRenderer;

/**
 * The main view, where we interact with the MainActivity, the MoveDetector, the GameManager and the UltimateRenderer
 */
public class GameView extends GLSurfaceView {
    protected static final String TAG="GLSurfaceView";

    public GameView(MainActivity context) {
        super(context);
        mActivity=context;
        setEGLContextClientVersion(2);//we checked in the manifest if the owner has gles2
        mRenderer = new UltimateRenderer(context,this);//the renderer "just" Overrides onDrawFrame to print everything
        setRenderer(mRenderer);

        letTheGameBegins();
        mRenderer.linkWithWorld(GameManager.getBall(),GameManager.getEnvironnement());//we link the elements of the game to the renderer
        mMoveDetector = new MoveDetector(context, this);//we create the MoveDetector that will react when the user touches or turn his screen
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();//not sure that it's important
        return mMoveDetector.onTouchEvent(event);//it's the job of the MoveDetector
    }

    //slowTime, jump and changeAngle are called by the MoveDetector: it's the function that are directly "called" by the user
    public void jump(){
        GameManager.prepareJump();
    }

    public void changeAngle(float angle) {
        GameManager.changeDir(angle);
    }

    public void slowTime() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.slowTime();
            }
        });
    }

    public void letTheGameBegins() {
        /**
         * begins the game: is called when the user click on try again or at the creation of the view
         */
        GameManager.tryAgain(mActivity);
    }


    public void setNewScreen(float w, float h) {
        mMoveDetector.setResolution(w,h);
    }
    //we need our renderer to tell us the resolution of the view, through this setter

    @Override
    public void onPause() {
        GameManager.stopCollisions=true;//stops the calculs
        mMoveDetector.onPause();
        mRenderer.invalidate();
    }

    public void addToCompteur() {// The FPS are in "compteur", and can be printed in place of the score!
        compteur++;
        if (SystemClock.uptimeMillis() - lastTimeUpdated > 1000) {
            mActivity.setScore(compteur);
            compteur=0;
            lastTimeUpdated=SystemClock.uptimeMillis();
        }
    }

    @Override
    public void onResume() {
        GameManager.reinit(mActivity);
        mRenderer.linkWithWorld(GameManager.getBall(),GameManager.getEnvironnement());
        mMoveDetector.onResume();
        mActivity.startService(new Intent(mActivity,GameManager.class));
    }

    private long lastTimeUpdated;
    private int compteur=0;//compteur counts the fps
    protected MainActivity mActivity;
    protected MoveDetector mMoveDetector;
    protected UltimateRenderer mRenderer;
}