package com.blblbl.torus.game;

import android.opengl.Matrix;

import com.blblbl.torus.utils.TimeHandler;
import com.blblbl.torus.utils.Vect;


public class BalleManager {
    public BalleManager() {}
    
    public void init() {
        mRotationMatrix=new float[16];
        Matrix.setIdentityM(mRotationMatrix,0);
        mUseAngularSpeed=false;

        mNewTime = TimeHandler.getTime();
        mPos = new Vect(470,5,475);
        mVitesseRotation=VITESSE_ROTATION_BALLE;
        mVitesseChute=0;
        mDirection = Vect.normalize(new Vect(-1f,0,0f));
        mLastDir= new Vect(mDirection);
        mNextDir= new Vect(mDirection);
        mNextAngle=90;
        mLastAngle=0;
        mEndTimeChangedDir=0;
        mLastTimeChangedDir=0;
        mAngleRotation=0;
        mAngle=0;
    }

    public Vect avancement() {
        long diff= mNewTime-mLastUpdate;
        Vect ret = mDirection.by(rayon*mVitesseRotation/1000f);
        ret.add(new Vect(0,-mVitesseChute/1000f,0));
        return ret.by(diff);
    }

    public void setSpeed(float speed) {
        mVitesseRotation=speed;
    }

    public void keepTime() {
        /**
         * Save the new and last time for updates, and change mDirection and mAngleRotation.
         *
         */
        mLastUpdate = mNewTime;
        mNewTime=TimeHandler.getTime();
        mVitesseChute += 98.1f*(mNewTime - mLastUpdate)/1000f;
        if (mUseAngularSpeed) {
            float addAngle = mAngleKeepTurning*((float)(mNewTime-mLastUpdate)/100f);
            Matrix.setRotateM(mRotationMatrix,0,addAngle,0,1,0);
            mAngleRotation+=addAngle;
            float retDir[] = new float[4];
            Matrix.multiplyMV(retDir,0,mRotationMatrix,0,mDirection.toVec4(),0);
            mDirection= Vect.normalize(new Vect(retDir[0],retDir[1],retDir[2]));

        } else {
            if (mNewTime < mEndTimeChangedDir) {
                mDirection = Vect.normalize(mNextDir.by(mNewTime - mLastTimeChangedDir)
                        .plus(mLastDir.by(mEndTimeChangedDir - mNewTime)));//(t*last dir+(1-t)new dir)
                mAngleRotation = (mNextAngle * (mNewTime - mLastTimeChangedDir)
                        + (mLastAngle * (mEndTimeChangedDir - mNewTime))) / ((float) mEndTimeChangedDir - mLastTimeChangedDir);//(t*last dir+(1-t)new dir)
            } else {
                mAngleRotation = mNextAngle;
                if (!mLastDir.equals(mNextDir)) {
                    mLastDir = new Vect(mNextDir);
                    mDirection = new Vect(mLastDir);
                }
            }
        }
    }

    public void updateRotationAngle() {
        Vect perpDir= Vect.normalize(mDirection.cross(new Vect(0,1,0)));
        mAngleRotation=360-(float)Math.acos(perpDir.at(0))*Math.signum(perpDir.at(2))*180/3.1415f;
    }


    public void updatePos(Environnement env) {
        mPos.add(avancement());
        mPos.setX((mPos.at(0)+env.getTailleGrille()) % env.getTailleGrille());
        mPos.setZ((mPos.at(2)+env.getTailleGrille()) % env.getTailleGrille());
    }

    public void changeAngle(){
        mAngle -= (mNewTime-mLastUpdate)*180*mVitesseRotation/3141.5f;
    }
    public void addAngles(float[] transMatrix) {
        Matrix.rotateM(transMatrix, 0, mAngleRotation, 0, 1, 0);//rotation of the axis Rotation
        Matrix.rotateM(transMatrix, 0, mAngle,1,0,0);//rotation around the axis of rotation
    }

    public Vect getDir() {
        return mDirection;
    }

    public void changeDir(float angle) {
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 1, 0);
        //if (keepTurning) {
            mAngleKeepTurning=angle;//là on doit changer la vitesse angulaire normalement...
            mUseAngularSpeed=true;
        /*} else{
            if (angle == 0)
                return;
            changeNextDir(angle);
        }*/
    }

    private void changeNextDir(float angle) {
        mNextAngle += angle;
        mLastDir = new Vect(mDirection);
        mLastTimeChangedDir = mNewTime;

        long tempsRotation =  ((int)Math.abs(angle) *3);

        mEndTimeChangedDir = mNewTime + tempsRotation;

        float lastDir[] = mNextDir.toVec4();
        float newDir[] = new float[4];
        Matrix.multiplyMV(newDir, 0, mRotationMatrix, 0, lastDir, 0);
        mNextDir.setX(newDir[0]);
        mNextDir.setY(newDir[1]);
        mNextDir.setZ(newDir[2]);
    }

    public Vect getPos() { return mPos; }
    public static float getRayon() { return rayon; }
    public void resetVitesseChute() {mVitesseChute=0;}
    public void jump() {mVitesseChute=-50;}
    public long getNewTime() { return mNewTime; }

    private Vect mDirection;
    private Vect mLastDir,mNextDir;
    private final static float rayon=1.9f*1.5f;
    private float mAngleRotation,mAngle;
    private float mNextAngle,mLastAngle;
    private float mVitesseChute=0;
    private float mVitesseRotation;
    private Vect mPos;
    private long mLastUpdate;//with uptimeMillis()
    private long mNewTime,mLastTimeChangedDir,mEndTimeChangedDir;

    private float mAngleKeepTurning;//vitesse angulaire
    private float mRotationMatrix[];
    private boolean mUseAngularSpeed;
    public final float VITESSE_ROTATION_BALLE=30f;
}