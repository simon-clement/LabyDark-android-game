package com.blblbl.torus.interaction_game;

import android.view.GestureDetector;
import android.view.MotionEvent;


class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_YDISTANCE=100;
    private static final int FLING_MIN_VELOCITY=10;
    private static final int FLING_MIN_XDISTANCE=50;
    private static final int FLING_MIN_YDISTANCE=50;
    private boolean careR=true,careL=true,careJump=true;
    private GameView mGameView;

    public MyGestureDetector(GameView view) {
        super();
        mGameView=view;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        careR=true;
        careL=true;
        careJump=true;
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getY() - e2.getY()) > FLING_MIN_YDISTANCE) {
            if (velocityY < -FLING_MIN_VELOCITY && careJump) {
                mGameView.jump();
                return true;
            }
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {

        if (distanceY<-SWIPE_MAX_YDISTANCE && careJump) {
            careL=true;
            careR=true;
            careJump=false;
            mGameView.jump();
            return true;
        }
        return super.onScroll(e1,e2,distanceX,distanceY);
    }

}