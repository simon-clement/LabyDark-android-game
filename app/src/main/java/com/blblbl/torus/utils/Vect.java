package com.blblbl.torus.utils;

public class Vect {
    public Vect(float xInit,float yInit, float zInit) {
        x=xInit;
        y=yInit;
        z=zInit;
    }
    public Vect(Vect b) {
        x=b.x;
        y=b.y;
        z=b.z;
    }
    public float at(int index) {
        switch (index){
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                return 0;
        }
    }

    public float dot(Vect b) {
        return x*b.x+y*b.y+z*b.z;
    }

    public Vect cross(Vect b) {
        return new Vect(y*b.z-z*b.y, z*b.x - x*b.z, x*b.y-y*b.x);
    }

    public float norme() {
        return (float)Math.sqrt(x*x+y*y+z*z);
    }

    public float normeCarree() { return x*x + y*y + z*z; }

    public Vect plus(Vect b) {
        return new Vect(x+b.x,y+b.y,z+b.z);
    }

    public void multiplyBy(float b) {
        x*=b;
        y*=b;
        z*=b;
    }

    public float[] toVec4() {
        float ret[] = new float[4];
        ret[0]=x;
        ret[1]=y;
        ret[2]=z;
        ret[3]=1;
        return ret;
    }

    public Vect by(float b) {
        return new Vect(x*b,y*b,z*b);
    }

    public void add(Vect b) {
        x+=b.x;
        y+=b.y;
        z+=b.z;
    }

    public void setX(float xPos) {
        x = xPos;
    }
    public void setY(float yPos) {
        y = yPos;
    }
    public void setZ(float zPos) {
        z = zPos;
    }

    public static Vect normalize(Vect a) {
        float norm=a.norme();
        if (norm != 0) {
            return a.by(1/norm);
        }
        else
            return a;
    }

    public boolean equals(Vect b) {
        return (x==b.x && y==b.y && z==b.z);
    }
    protected float x,y,z;//coords must not be empty
}
