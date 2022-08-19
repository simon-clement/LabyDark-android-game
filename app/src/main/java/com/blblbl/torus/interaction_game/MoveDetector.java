package com.blblbl.torus.interaction_game;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;

import com.blblbl.torus.R;

public class MoveDetector {
    /**
     * Cette classe doit gérer les interactions utilisateurs: toucher, orientation du téléphone...
     */

    private GestureDetectorCompat mGestureDetector;
    private OrientationDetector mOrientationDetector;
    private boolean modeTouch=false;
    private GameView mGameView;
    private float mLargeur, mHauteur;
    private Context mContext;
    private float mSensibilite;

    public MoveDetector(Context context, GameView view) {
        mContext= context;
        mGameView=view;
    }

    public boolean modeOrientation() {
        return mOrientationDetector.sensorSupported();
    }

    public void setResolution(float w, float h) {
        mLargeur = w;
        mHauteur = h;
    }

    public void onPause() {
        mOrientationDetector.onPause();
    }
    public void onResume() {
        mOrientationDetector= new OrientationDetector(mContext,mGameView,this);
        mGestureDetector = new GestureDetectorCompat(mContext, new MyGestureDetector(mGameView));

        SharedPreferences settings = mContext.getSharedPreferences(mContext.getString(R.string.stringSharedPreferencesOptions),Context.MODE_PRIVATE);
        modeTouch = (settings.getInt(mContext.getString(R.string.stringPrefsModeTouch),0)==1);
        mSensibilite= settings.getInt(mContext.getString(R.string.stringPrefsSensibilite),50);

        if (!modeTouch)
            mOrientationDetector.onResume();

        if (!mOrientationDetector.sensorSupported())
            modeTouch=true;
    }

    public boolean onTouchEvent(MotionEvent event){
        if (modeTouch) {
            if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                float position=event.getX() /mLargeur;
                float newAngle=0;
                newAngle = -Math.signum(position-0.5f)*(Math.abs(position-0.5f))*80;
                mGameView.changeAngle(applySensibilite(newAngle));
            }
            if (event.getAction() == MotionEvent.ACTION_POINTER_UP || event.getAction() == MotionEvent.ACTION_UP) {
                mGameView.changeAngle(0);
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    public float applySensibilite(float angle) {
        return  angle * (0.1f+mSensibilite/30);
    }

}
