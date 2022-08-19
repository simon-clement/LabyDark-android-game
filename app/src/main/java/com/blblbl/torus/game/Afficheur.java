package com.blblbl.torus.game;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.blblbl.torus.utils.ShapeCreator;
import com.blblbl.torus.utils.Utils;
import com.blblbl.torus.utils.Vect;
import com.blblbl.torus.R;

import java.nio.FloatBuffer;

public class Afficheur {
    protected static final String TAG="Afficheur";
    public Afficheur(Context context) {
        mContext = context;
        Resources res = mContext.getResources();
        mVertexShaderString = res.getString(R.string.MainVertexShader);
        mFragmentShaderString = res.getString(R.string.MainFragmentShader);
        mVertexBouleShaderString = res.getString(R.string.BouleVertexShader);
        mFragmentBouleShaderString= mFragmentShaderString; //res.getString(R.string.BouleFragmentShader);
    }

    public void init(final Environnement env) throws Exception {
        mFinalMatrix = new float[16];
        mProjMatrix = new float[16];
        mTransMatrix = new float[16];
        mViewMatrix = new float[16];
        handlePrograms();
        loadVBOs(env);
    }
    public void setTailleEcran(float x, float y) {
        xEcran=x;
        yEcran=y;
    }

    public void initForDraw() {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(true);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOHandle);
    }

    public void prepareForBall(boolean isSlowedTime) {
        setBallProgram(isSlowedTime);
        int offset = Utils.FLOAT_SIZE_BYTES * ShapeCreator.OFFSET_BALL * 5;
        GLES20.glVertexAttribPointer(maPositionBallHandle, 3,
                GLES20.GL_FLOAT, false, Utils.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, Utils.DATA_POS_OFFSET + offset);
        GLES20.glEnableVertexAttribArray(maPositionBallHandle);

        GLES20.glVertexAttribPointer(maTextureBallHandle, 2,
                GLES20.GL_FLOAT, false, Utils.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, Utils.DATA_UV_OFFSET*Utils.FLOAT_SIZE_BYTES + offset);
        GLES20.glEnableVertexAttribArray(maTextureBallHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBallID);
    }

    public void prepareForEverythingElseThanBall(boolean isSlowedTime) {
        setClassicProgram(isSlowedTime);
        GLES20.glVertexAttribPointer(maPositionHandle, 3,
                GLES20.GL_FLOAT, false, Utils.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, Utils.DATA_POS_OFFSET);
        GLES20.glEnableVertexAttribArray(maPositionHandle);

        GLES20.glVertexAttribPointer(maTextureHandle, 2,
                GLES20.GL_FLOAT, false, Utils.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, Utils.DATA_UV_OFFSET*Utils.FLOAT_SIZE_BYTES);
        GLES20.glEnableVertexAttribArray(maTextureHandle);
    }

    public void drawBall(BalleManager balleManager) {
        /**
         * This function needs the angle and the direction of the ball.
         * Theses informations aren't vital enough for the gameplay to warn for "thread-safe"
         */
        Matrix.setIdentityM(mTransMatrix,0);//for now we put identity

        balleManager.addAngles(mTransMatrix);
        if (Physics.mDead) {
            long time = balleManager.getNewTime()-Physics.mTimeDeath;
            float scale=(float)time/750;
            if (time > 1000)
                scale=1.5f;
            Matrix.scaleM(mTransMatrix,0,1.5f-scale,1.5f-scale,1.5f-scale);
        }else {
            Matrix.scaleM(mTransMatrix,0,1.5f,1.5f,1.5f);
        }
        GLES20.glUniformMatrix4fv(mTransMatrixBallHandle, 1, false, mTransMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, ShapeCreator.NOMBRE_POINTS_BALL);
    }

    public void setViewMatrix(Vect reculCamera,Vect pos) {
        Matrix.setLookAtM(mViewMatrix, 0,pos.at(0)+reculCamera.at(0),pos.at(1)+ reculCamera.at(1)+8,pos.at(2)+ reculCamera.at(2),pos.at(0),pos.at(1),pos.at(2), 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mFinalMatrix, 0, mProjMatrix, 0, mViewMatrix, 0);
        if (isProgramBall)
            GLES20.glUniformMatrix4fv(mViewMatrixBallHandle, 1, false, mFinalMatrix, 0);
        else
            GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mFinalMatrix, 0);
    }

    public void drawSky(Ciel sky) {
        /**
         * This function needs the angle and the direction of the ball.
         * Theses informations aren't vital enough for the gameplay to warn for "thread-safe"
         */
        GLES20.glCullFace(GLES20.GL_FRONT);
        System.arraycopy( sky.getRotationMatrix(), 0, mTransMatrix, 0, 16 );
        Matrix.scaleM(mTransMatrix,0,sky.getScaleBall(),sky.getScaleBall(),sky.getScaleBall());
        GLES20.glUniformMatrix4fv(mTransMatrixBallHandle, 1, false, mTransMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, ShapeCreator.NOMBRE_POINTS_BALL);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    public void drawGround(){
        /**
         * In this function we need the position of the ball; it's not really bad if it's "late",
         * because the pattern is always the same (squares)
         */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureEarthID);//les fonctions prepareDraw et bindVBO sont TRÈS LENTES!!! voilà.
        Matrix.setIdentityM(mTransMatrix,0);//for now we put identity

        GLES20.glUniformMatrix4fv(mTransMatrixHandle, 1, false, mTransMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, ShapeCreator.OFFSET_GRID, ShapeCreator.NOMBRE_POINTS_GRID);
    }

    public void prepareForObstacles() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureWallID);
    }

    public void prepareForBonus() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBonusID);
    }

    public void draw(final Environnement.Obstacle obstacle, Vect coinCase,long time) {
        Matrix.setIdentityM(mTransMatrix,0);//for now we put identity
        Matrix.translateM(mTransMatrix,0,obstacle.getCenterXFast(coinCase.at(0)),obstacle.getHeight(time),obstacle.getCenterZFast(coinCase.at(2)));
        if (obstacle.isOnSide())
            Matrix.rotateM(mTransMatrix,0,90,0,1,0);

        GLES20.glUniformMatrix4fv(mTransMatrixHandle, 1, false, mTransMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, ShapeCreator.OFFSET_WALL, ShapeCreator.NOMBRE_POINTS_WALL);
    }

    public void setCentreCourbure(float x, float y, float z) {
        GLES20.glUniform3f(mCentreHandle, x,y,z);
    }

    public void draw(final Environnement.Objectif objectif, final Environnement env,int caseBalle,long time) {
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureObjectifID);ici ça ne sert à rien puisqu'on a déjà chargé la texture de la flèche =)

        Matrix.setIdentityM(mTransMatrix,0);
        Vect coinCase=env.coinInferieurPosBalleAffichable();//le coin de la case est constant! =)
        Matrix.translateM(mTransMatrix,0,objectif.getCenterX(caseBalle,coinCase.at(0)),objectif.getCenterY(time),objectif.getCenterZ(caseBalle,coinCase.at(2)));
        Matrix.rotateM(mTransMatrix,0,(float)time/30f,0.2f,1,0.2f);

        GLES20.glUniformMatrix4fv(mTransMatrixHandle, 1, false, mTransMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, ShapeCreator.OFFSET_CUBE,ShapeCreator.NOMBRE_POINTS_CUBE);
    }

    public void drawBonus(final Environnement.Bonus bonus, final Environnement env,int caseBalle,long time) {
        Matrix.setIdentityM(mTransMatrix,0);
        Vect coinCase=env.coinInferieurPosBalleAffichable();//le coin de la case est constant! =)
        Matrix.translateM(mTransMatrix,0,bonus.getCenterX(caseBalle,coinCase.at(0)),bonus.getCenterY(time),bonus.getCenterZ(caseBalle,coinCase.at(2)));
        Matrix.rotateM(mTransMatrix,0,(float)time/30f,0.2f,1,0.2f);

        GLES20.glUniformMatrix4fv(mTransMatrixHandle, 1, false, mTransMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, ShapeCreator.OFFSET_CUBE, ShapeCreator.NOMBRE_POINTS_CUBE);
    }

    public void drawArrow(float angle) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureObjectifID);
        Matrix.setIdentityM(mTransMatrix,0);
        Matrix.translateM(mTransMatrix,0,0,12,0);
        Matrix.rotateM(mTransMatrix,0,angle,0,1,0);
        Matrix.rotateM(mTransMatrix,0,-8,0,0,1);

        GLES20.glUniformMatrix4fv(mTransMatrixHandle, 1, false, mTransMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, ShapeCreator.OFFSET_ARROW, ShapeCreator.NOMBRE_POINTS_ARROW);
    }

    protected void loadVBOs(final Environnement env) {
        FloatBuffer globalBuffer = ShapeCreator.makeGlobalBuffer(Environnement.NOMBRE_CASES_AFFICHABLES,(int) env.getTailleCase(),env.getHeight(),
                env.getTailleCase()/2f,env.getCoteObstacle()/2f,env.getTailleCase(),
                4f,
                4,0.15f,1.5f,0.6f);


        final int vboHandle[] = new int[1];
        GLES20.glGenBuffers( 1, vboHandle,0 );
        mVBOHandle = vboHandle[0];
        Log.v(TAG,"création du VBO");


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, globalBuffer.capacity() * 4,
                globalBuffer, GLES20.GL_STATIC_DRAW);
    }

    public void PerspectiveM(float ratio, float angleVue) {
        float top = (float) Math.tan(angleVue * Math.PI/360.0) * 1f;
        Matrix.frustumM(mProjMatrix,0, -ratio*top,ratio*top, -top, top, 1f, 10000f);
    }

    protected void setRalenti(boolean ralenti, int Ralentihandle) {
        float ralentit=0;
        if (ralenti)
            ralentit=1;
        GLES20.glUniform1f(Ralentihandle,ralentit);
    }
    protected void setCentreEcran(int ecranHandle) {
        GLES20.glUniform2f(ecranHandle,xEcran/2,yEcran/2);
    }
    public void setClassicProgram(boolean ralenti) {
        GLES20.glUseProgram(mProgram);
        isProgramBall=false;
        setRalenti(ralenti,mRalentiHandle);
        setCentreEcran(mCentreEcranHandle);
    }
    public void setBallProgram(boolean ralenti) {
        GLES20.glUseProgram(mProgramBoule);
        isProgramBall=true;
        setRalenti(ralenti,mRalentiBouleHandle);
        setCentreEcran(mCentreEcranBouleHandle);
    }

    protected void handlePrograms() throws Exception {
        /**
         * This fonction should be called each time the surface is created
         * (throws a runtimeException if the program can't be created)
         */
        mProgram = Utils.createProgram(mVertexShaderString, mFragmentShaderString);
        mProgramBoule = Utils.createProgram(mVertexBouleShaderString, mFragmentBouleShaderString);

        LoadAttributes();
        LoadTextures();
    }
    protected void LoadTextures() throws Exception{
        int[] textures = new int[5];
        GLES20.glGenTextures(5, textures, 0);

        mTextureEarthID = textures[0];
        mTextureWallID = textures[1];
        mTextureBallID = textures[2];
        mTextureObjectifID = textures[3];
        mTextureBonusID = textures[4];
        Log.v(TAG, "loading textures..");
        Utils.BindTexture(mContext,textures[0], R.drawable.earth1);
        Utils.BindTexture(mContext,textures[1], R.drawable.wall);
        Utils.BindTexture(mContext,textures[2], R.drawable.tex_ball);
        Utils.BindTexture(mContext,textures[3], R.drawable.objectif);
        Utils.BindTexture(mContext,textures[4], R.drawable.sablier);
    }
    protected void LoadAttributes() throws Exception{//load id of attributes/uniforms
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        Utils.checkGlError("aPosition can't be loaded");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mViewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uViewMatrix");
        mTransMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uTransMatrix");
        mCentreHandle = GLES20.glGetUniformLocation(mProgram, "uCentre");
        mRalentiHandle = GLES20.glGetUniformLocation(mProgram, "uRalenti");
        mCentreEcranHandle = GLES20.glGetUniformLocation(mProgram, "uCentreEcran");
        Utils.checkGlError("attribute can't be loaded");


        maTextureBallHandle = GLES20.glGetAttribLocation(mProgramBoule, "aTextureCoord");
        mViewMatrixBallHandle = GLES20.glGetUniformLocation(mProgramBoule, "uViewMatrix");
        mTransMatrixBallHandle = GLES20.glGetUniformLocation(mProgramBoule, "uTransMatrix");
        maPositionBallHandle = GLES20.glGetAttribLocation(mProgramBoule, "aPosition");
        mRalentiBouleHandle = GLES20.glGetUniformLocation(mProgramBoule, "uRalenti");
        mCentreEcranBouleHandle = GLES20.glGetUniformLocation(mProgramBoule, "uCentreEcran");
        Utils.checkGlError("attribute in ball program can't be loaded");
    }

    protected Context mContext;
    protected final String mVertexShaderString,mFragmentShaderString,mVertexBouleShaderString,mFragmentBouleShaderString;
    protected int mProgram,
            mViewMatrixHandle,mTransMatrixHandle,maPositionHandle,maTextureHandle,
            mProgramBoule,
            mViewMatrixBallHandle,mTransMatrixBallHandle,maPositionBallHandle,maTextureBallHandle,

    mTextureEarthID, mTextureWallID, mTextureBallID,mTextureBonusID,mTextureObjectifID,mCentreHandle,mRalentiHandle,mRalentiBouleHandle,
                    mCentreEcranHandle,mCentreEcranBouleHandle, mVBOHandle;

    protected float[] mFinalMatrix,mProjMatrix,mViewMatrix,mTransMatrix;
    protected boolean isProgramBall;
    protected float xEcran,yEcran;
}
