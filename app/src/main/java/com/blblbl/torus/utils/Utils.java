package com.blblbl.torus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Utils {

    public static abstract class fonction {
        public abstract float get(long time);
    }


    public static int createProgram(String vertexSource, String fragmentSource)throws Exception {
        /***
         * Create a program with both vertex and fragment Shaders.
         * since it calls checkGlError, can throw a RuntimeException!
         */
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            Utils.checkGlError("vertexShader");
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            Utils.checkGlError("pixelShader");
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader, vertexShader");//shouldn't happen

            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader, pixelShader");//shouldn't happen

            GLES20.glLinkProgram(program);
            checkGlError("glLinkProgram");

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {//can happen depending on the device
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                throw new Exception("can't link program");
            }
        } else {
            throw new Exception("can't create program");
        }
        return program;
    }

    public static void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public static void texImage2D(Context context, int idImage) {
        /**
         * throw an exception if Resource(idImage) does'nt exist!
         */
        InputStream is = context.getResources().openRawResource(idImage);//won't throw an exception if it's in R
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                Log.e(TAG,"IOException!!!");
                // Ignore.
            }
        }
        try {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            checkGlError("texImage2D");
        } catch (Exception e) {
            Log.e(TAG, "texImage2D failed.");
        }
        bitmap.recycle();
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static void BindTexture(Context context, int idTexture,int idImage) throws Exception {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, idTexture);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_MIRRORED_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_MIRRORED_REPEAT);
        //try {
            //checkGlError("BindTexture, idImage : " + Integer.toString(idImage));
       // } catch (RuntimeException e) {
         //   return;
       // }
        Utils.texImage2D(context,idImage);
    }

    public static FloatBuffer toFloatBuffer(float[] tab) {
        /**
         * Can throw an exception with allocateDirect and put
         */
        FloatBuffer ret;
        ret =   ByteBuffer.allocateDirect(tab.length*FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        ret.put(tab).position(0);//warning: the allocation is slow, so avoid it as much as possible
        return ret;
    }

    public static FloatBuffer toFloatBuffer(int totalLength,float[]... tabs) {
        /**
         * Can throw an exception with allocateDirect and put
         */
        FloatBuffer ret;
        ret =   ByteBuffer.allocateDirect(totalLength*FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (float[] tab: tabs)
            ret.put(tab);//warning: the allocation is slow, so avoid it as much as possible
        ret.position(0);
        return ret;
    }

    public static final int FLOAT_SIZE_BYTES = 4;
    public static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    public static final int DATA_POS_OFFSET = 0;
    public static final int DATA_UV_OFFSET = 3;
    private static final String TAG = "blblbl.Utils";
}
