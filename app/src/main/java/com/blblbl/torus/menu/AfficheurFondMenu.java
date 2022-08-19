package com.blblbl.torus.menu;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.blblbl.torus.game.Afficheur;
import com.blblbl.torus.game.BalleManager;
import com.blblbl.torus.utils.ShapeCreator;
import com.blblbl.torus.utils.Vect;

public class AfficheurFondMenu extends Afficheur {
    public AfficheurFondMenu(Context context) {
        super(context);
    }
    public void drawBall(BalleManager balleManager) {
        /**
         * This function needs the angle and the direction of the ball.
         * Theses informations aren't vital enough for the gameplay to warn for "thread-safe"
         */
        Matrix.setIdentityM(mTransMatrix,0);//for now we put identity
        Matrix.scaleM(mTransMatrix,0,1.5f,1.5f,1.5f);
        GLES20.glUniformMatrix4fv(mTransMatrixBallHandle, 1, false, mTransMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, ShapeCreator.NOMBRE_POINTS_BALL);
    }
}
