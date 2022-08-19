package com.blblbl.torus.interaction_game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.WindowManager;

import com.blblbl.torus.utils.Vect;

public class OrientationDetector implements SensorEventListener {
    private SensorManager mSensorManager;
    private GameView mGameView;
    private Sensor mSensor;
    private Context mContext;
    private MoveDetector mMoveDetector;
    private Vect mGravity;

    OrientationDetector(Context context, GameView view, MoveDetector moveDetector) {
        mMoveDetector=moveDetector;
        mGameView = view;
        mContext= context;
        mGravity = new Vect(0,0,0);
    }
    public boolean sensorSupported() {
        return mSensor != null;
    }

    private float[] rotationVectorAction(float[] values)
    {
        float[] result = new float[3];
        float[] orientation = new float[3];
        float[] rotMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rotMat, values);
        SensorManager.getOrientation(rotMat, orientation);
        result[0] = (float) orientation[0]; //Yaw
        result[1] = (float) orientation[1]; //Pitch
        result[2] = (float) orientation[2]; //Roll
        return result;
    }

    private static float[] adjustAccelOrientation(int displayRotation, float[] eventValues)
    {
        float[] adjustedValues = new float[3];

        final int axisSwap[][] = {
                {  1,  -1,  0,  1  },     // ROTATION_0
                {-1,  -1,  1,  0  },     // ROTATION_90
                {-1,    1,  0,  1  },     // ROTATION_180
                {  1,    1,  1,  0  }  }; // ROTATION_270

        final int[] as = axisSwap[displayRotation];
        adjustedValues[0]  =  (float)as[0] * eventValues[ as[2] ];
        adjustedValues[1]  =  (float)as[1] * eventValues[ as[3] ];
        adjustedValues[2]  =  eventValues[2];

        return adjustedValues;
    }

    public void onResume() {

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        if (mSensor == null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }*/
        //if (mSensor==null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //}
        if (mSensor != null)
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.

        // Many sensors return 3 values, one for each axis.

        if( mSensor.getType() ==  Sensor.TYPE_ROTATION_VECTOR || mSensor.getType() ==  Sensor.TYPE_GAME_ROTATION_VECTOR) {
            int angle= ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            float axe1[] = rotationVectorAction(event.values);
            float axe[] = adjustAccelOrientation(angle, axe1);


            final float offsetOrient=0.07f;
            axe[2] *= 2;
            float newAngle=0;
            if (Math.abs(axe[2])>offsetOrient)
                newAngle = -Math.signum(axe[2])*(Math.abs(axe[2])-offsetOrient)*50;

            mGameView.changeAngle(mMoveDetector.applySensibilite(newAngle));

        } else {
            final float offsetOrient=0.6f;
            int angle= ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            float axe[] = adjustAccelOrientation((angle+3)%4, event.values);
            float newAngle=0;
            if (Math.abs(axe[1]) > offsetOrient)
                newAngle = Math.signum(axe[1])*(Math.abs(axe[1])-offsetOrient)*10;
            mGameView.changeAngle(mMoveDetector.applySensibilite(newAngle));
        }
    }

    public void onPause() {
        try {
            if (mSensor != null)
                mSensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
