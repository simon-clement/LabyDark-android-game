package com.blblbl.torus.menu;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.blblbl.torus.game.Afficheur;
import com.blblbl.torus.game.BalleManager;
import com.blblbl.torus.game.Ciel;
import com.blblbl.torus.game.Environnement;
import com.blblbl.torus.game.UltimateRenderer;
import com.blblbl.torus.utils.Vect;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FondMenuRenderer extends UltimateRenderer implements GLSurfaceView.Renderer {
    private final static String TAG="MenuRenderer";
    public FondMenuRenderer(Context context) {
        super(context);
        mBalleManager=new BalleManager();
        mEnvironnement=new Environnement(10,45);
        mCiel=new Ciel();
        mBalleManager.init();
        mEnvironnement.init(mBalleManager.getPos());
        mAfficheur= new Afficheur(context);//new AfficheurFondMenu(context);
    }
    @Override
    public void onDrawFrame(GL10 unused) {
        if (initialized) {

                mAfficheur.initForDraw();//pure opengl to begin!
                mAfficheur.prepareForBall(false);
                mSavePosBall = mEnvironnement.getPosBalleAffichable(new Vect(0,BalleManager.getRayon()-1f,0));//ball's position affichable
                mSaveCaseBalle = mEnvironnement.numeroCaseBalle(mSavePosBall);

                Vect reculCamera = new Vect(-25,0,0);
                mAfficheur.setViewMatrix(reculCamera,new Vect(0,-1,0));
                GLES20.glDepthFunc(GLES20.GL_ALWAYS);
                mAfficheur.drawSky(mCiel);
                mAfficheur.drawBall(mBalleManager);

                GLES20.glDepthFunc(GLES20.GL_LESS);
                mAfficheur.prepareForEverythingElseThanBall(false);
                mAfficheur.setViewMatrix(reculCamera,mSavePosBall);
                mAfficheur.setCentreCourbure(mSavePosBall.at(0),mEnvironnement.getHeight(),mSavePosBall.at(2));
                mAfficheur.drawGround();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        try {
            mAfficheur.init(mEnvironnement);
        } catch (Exception e) {
            e.printStackTrace();
            sendToast("BOUUUUUUUUUUUUUUUUUUUUUUUUU");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width  / height;
        mAfficheur.PerspectiveM(ratio, 70);
        mAfficheur.setTailleEcran(width,height);
    }
    //note: since we are in a different thread, don't use mAct directly
}
