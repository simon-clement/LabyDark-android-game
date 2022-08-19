package com.blblbl.torus.utils;

import android.util.Log;

import java.nio.FloatBuffer;

public class ShapeCreator {
    public static float[] makeBallBeautiful(int nbreSubdivision) {
        return SphereCreator.makeSphere(nbreSubdivision);// points;
    }

    public static float[] makeGridPoints(int size,int taille,float height) {
        //returns a grid with 6*size*size points.
        final int xParcours[] = {0,0,1,1,0,1};
        final int zParcours[] = {0,1,0,0,1,1};
        float points[] = new float[5*6*size*size];
        for (int i=0; i<size; ++i) {
            for (int u = 0; u < size; ++u) {
                for (int p=0; p<6; ++p) {
                    int index=5*6*size*i + 5*6*u + 5*p;
                    points[index] = (i+xParcours[p])*taille;
                    points[index+1] = height;
                    points[index+2] = (u+zParcours[p])*taille;
                    points[index+3] = xParcours[p];
                    points[index+4] = zParcours[p];
                }
            }
        }
        return points;
    }
    public static float[] makeWall(float xMax,float zMax, float deep) {
        float points[] = new float[] {
                -xMax,-deep,-zMax,0,deep/xMax,
                -xMax , 0 ,-zMax, 0,0,
                xMax , 0 ,-zMax,1,0,//triangle avant-gauche

                -xMax,-deep,-zMax,0,deep/xMax,
                xMax , 0 ,-zMax,1,0,
                xMax,-deep,-zMax,1,deep/xMax,//triangle avant-droit

                -xMax , 0 ,zMax, 0,0,
                -xMax,-deep,zMax,0,deep/xMax,
                xMax , 0 ,zMax,1,0,//triangle arriere-gauche

                xMax , 0 ,zMax,1,0,
                -xMax,-deep,zMax,0,deep/xMax,
                xMax,-deep,zMax,1,deep/xMax,//triangle arriere-droit

                -xMax , 0 ,-zMax, 0,zMax/xMax,
                -xMax , 0 , zMax, 0,0,
                 xMax , 0 , zMax, 1,0,//triangle haut-gauche

                 xMax , 0 ,-zMax, 1,zMax/xMax,
                -xMax , 0 ,-zMax, 0,zMax/xMax,
                 xMax , 0 , zMax, 1,0,//triangle haut-Droit

                xMax, -deep, -zMax,0,deep/xMax,
                xMax, 0 , -zMax,0,0,
                xMax,0,zMax,zMax/xMax,0,//triangle droit-gauche

                xMax, -deep, -zMax,0,deep/xMax,
                xMax,0,zMax,zMax/xMax,0,
                xMax,-deep, zMax, zMax/xMax,deep/xMax,//triangle droit-droit

                -xMax, 0 , -zMax,0,0,
                -xMax, -deep, -zMax,0,deep/xMax,
                -xMax,0,zMax,zMax/xMax,0,//triangle gauche-gauche

                -xMax,0,zMax,zMax/xMax,0,
                -xMax, -deep, -zMax,0,deep/xMax,
                -xMax,-deep, zMax, zMax/xMax,deep/xMax,//triangle gauche-droit
        };
        return points;
    }

    public static float[] makeCube(float l) {
        float points[] = new float[] {
                l,l,-l,1,0,//y fixé
                -l,l,-l,0,0,
                -l,l,l,0,1,

                l,l,-l,1,0,
                -l,l,l,0,1,
                l,l,l,1,1,

                -l,-l,-l,0,0,
                l,-l,-l,1,0,//y fixé
                -l,-l,l,0,1,

                -l,-l,l,0,1,
                l,-l,-l,1,0,
                l,-l,l,1,1,

                l,-l,-l,0,0,
                l,l,-l,1,0,//x fixé
                l,-l,l,0,1,

                l,-l,l,0,1,
                l,l,-l,1,0,
                l,l,l,1,1,

                -l,l,-l,1,0,//x fixé
                -l,-l,-l,0,0,
                -l,-l,l,0,1,

                -l,l,-l,1,0,
                -l,-l,l,0,1,
                -l,l,l,1,1,

                -l,-l,l,0,0,
                l,-l,l,1,0,//z fixé
                -l,l,l,0,1,

                -l,l,l,0,1,
                l,-l,l,1,0,
                l,l,l,1,1,

                l,-l,-l,1,0,//z fixé
                -l,-l,-l,0,0,
                -l,l,-l,0,1,

                l,-l,-l,1,0,
                -l,l,-l,0,1,
                l,l,-l,1,1,

        };
        return points;
    }


    public static float[] makeArrow(float l,float h, float largeurPointe,float reduc) {
        h=1/h;
        float points[] = new float[] {
                l*2,l/h,-l*reduc,1,0,//y fixé
                -l*2,l/h,-l,0,0,
                -l*2,l/h,l,0,1,

                l*2,l/h,-l*reduc,1,0,
                -l*2,l/h,l,0,1,
                l*2,l/h,l*reduc,1,1,

                -l*2,-l/h,-l,0,0,
                l*2,-l/h,-l*reduc,1,0,//y fixé
                -l*2,-l/h,l,0,1,

                -l*2,-l/h,l,0,1,
                l*2,-l/h,-l*reduc,1,0,
                l*2,-l/h,l*reduc,1,1,

                -l*2,-l/h,l,0,0,
                l*2,-l/h,l*reduc,1,0,//z fixé
                -l*2,l/h,l,0,1,

                -l*2,l/h,l,0,1,
                l*2,-l/h,l*reduc,1,0,
                l*2,l/h,l*reduc,1,1,

                l*2,-l/h,-l*reduc,1,0,//z fixé
                -l*2,-l/h,-l,0,0,
                -l*2,l/h,-l,0,1,

                l*2,-l/h,-l*reduc,1,0,
                -l*2,l/h,-l,0,1,
                l*2,l/h,-l*reduc,1,1,


                l*2,l/h,-largeurPointe*l,1,0,//x fixé
                l*2,-l/h,-largeurPointe*l,0,0,
                l*2,-l/h,largeurPointe*l,0,1,

                l*2,l/h,-largeurPointe*l,1,0,
                l*2,-l/h,largeurPointe*l,0,1,
                l*2,l/h,largeurPointe*l,1,1,

                -l*2,l/h,-l,1,0,//x fixé
                -l*2,-l/h,-l,0,0,
                -l*2,-l/h,l,0,1,

                -l*2,l/h,-l,1,0,
                -l*2,-l/h,l,0,1,
                -l*2,l/h,l,1,1,
//maintenant la pointe: les points du bout seront: x,y,z = (xBout + l, +- l/h, 0)
                l*2,-l/h,-largeurPointe*l,0,0, //les deux triangles avants
                l*3,-l/h,0,1,0.5f,
                l*2,-l/h,largeurPointe*l,0,1,

                l*2,l/h,-largeurPointe*l,0,0,
                l*2,l/h,largeurPointe*l,0,1,
                l*3,l/h,0,1,0.5f,

//les faces rectangulaires coté x>0:
                l*2,-l/h,largeurPointe*l,0,1,
                l*3,-l/h,0,0,0,//le carré droit (z>=0)
                l*3,l/h,0,1,0,

                l*3,l/h,0,1,0,
                l*2,l/h,largeurPointe*l,1,1,
                l*2,-l/h,largeurPointe*l,0,1,

                l*3,-l/h,0,0,0,//le carré gauche (z<=0)
                l*2,-l/h,-largeurPointe*l,0,1,
                l*3,l/h,0,1,0,

                l*3,l/h,0,1,0,
                l*2,-l/h,-largeurPointe*l,0,1,
                l*2,l/h,-largeurPointe*l,1,1 };
        return points;
    }

    public static FloatBuffer makeGlobalBuffer(int sizeGrid, int tailleGrid, float heightGrid,
                                               float xMaxWall, float zMaxWall, float deepWall,
                                               float lCube,
                                               float lArrow,float hArrow,float largeurPointeArrow,float reducArrow) {
        float[] ball = makeBallBeautiful(0);
        NOMBRE_POINTS_BALL = ball.length/5;
        NOMBRE_POINTS_TOTAL= NOMBRE_POINTS_ARROW+NOMBRE_POINTS_WALL+NOMBRE_POINTS_BALL+NOMBRE_POINTS_CUBE+NOMBRE_POINTS_GRID;
        return Utils.toFloatBuffer(NOMBRE_POINTS_TOTAL*5, makeCube(lCube),
                makeWall(xMaxWall,zMaxWall,deepWall),
                makeArrow(lArrow,hArrow,largeurPointeArrow,reducArrow),
                makeGridPoints(sizeGrid,tailleGrid,heightGrid),
                ball);
    }

    public static final int NOMBRE_POINTS_CUBE=36;
    public static final int NOMBRE_POINTS_WALL=30;
    public static final int NOMBRE_POINTS_ARROW=54;
    public static final int NOMBRE_POINTS_GRID=216;
    public static int NOMBRE_POINTS_BALL=0;//19680/5;//1080;TODO voir pk y'a autant de points ^^'
    public static final int OFFSET_CUBE=0;
    public static final int OFFSET_WALL=OFFSET_CUBE + NOMBRE_POINTS_CUBE;
    public static final int OFFSET_ARROW=OFFSET_WALL + NOMBRE_POINTS_WALL;
    public static final int OFFSET_GRID=OFFSET_ARROW+ NOMBRE_POINTS_ARROW;
    public static final int OFFSET_BALL = OFFSET_GRID + NOMBRE_POINTS_GRID;
    public static int NOMBRE_POINTS_TOTAL;
}