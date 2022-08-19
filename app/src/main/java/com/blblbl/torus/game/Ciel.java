package com.blblbl.torus.game;


import android.opengl.Matrix;
import com.blblbl.torus.utils.Vect;

public class Ciel {

    public Ciel() {
        mRotationMatrix=new float[16];
        Matrix.setIdentityM(mRotationMatrix,0);
        mScaleBall=1000;
    }

    public void turn(Vect avancement) {
        turn(avancement.cross(new Vect(0,1,0)),avancement.norme()/10f);
    }

    public void turn(Vect axis, float angle) {
        float axeF[] =new float[4];
        float invRotationMatrix[]=new float[16];
        Matrix.transposeM(invRotationMatrix,0,mRotationMatrix,0);
        Matrix.multiplyMV(axeF,0,invRotationMatrix,0,axis.toVec4(),0);
        Matrix.rotateM(mRotationMatrix,0,angle,axeF[0],axeF[1],axeF[2]);
    }

    public float[] getRotationMatrix() {
        return mRotationMatrix;
    }

    public float getScaleBall() {
        return mScaleBall;
    }

    private float[] mRotationMatrix;
    private float mScaleBall;

}
