package com.blblbl.torus.utils;

import android.util.Log;

import java.util.Vector;

public abstract class SphereCreator {
    public static float[] makeSphere(int nbreSubdivisions) {
        float phi=(1+(float)Math.sqrt(5))/2;
        Vect tableau[] = new Vect[12];
        int offset=0;
        for (int i=-1; i<2; i+=2) {
            for (int u=-1; u<2; u+=2) {
                tableau[offset] = new Vect(i*phi,u,0);//icosaèdre de base
                tableau[offset+1] = new Vect(u,0,i*phi);
                tableau[offset+2] = new Vect(0,i*phi,u);
                offset+=3;
            }
        }
        int indices_voisins[][] = new int[12][5];
        for (int i=0; i<tableau.length; ++i) {
            offset=0;
            for (int u=0; u<tableau.length; ++u) {
                if (u != i) {
                    double norme = (tableau[u].plus(tableau[i].by(-1))).norme();
                    if (norme < 2.1 && norme > 1.9) {
                        indices_voisins[i][offset] = u;
                        offset++;
                    }
                }
            }
        }

        //maintenant on va construire les triangles de notre icosaèdre de base!

        Vector<Triangle> trianglesIcosaedre = new Vector<>();
        for (int i=0; i<tableau.length; ++i) {//pour chaque point (y'en a 12)
            for (int u=0; u<5; ++u) {
                if (indices_voisins[i][u] > i) {//si on a pas déjà vu ce point
                    Vector<Integer> voisinsCommuns = intersection(indices_voisins[i],indices_voisins[indices_voisins[i][u]]);//intersection des voisins avec les voisins du voisin (mé oui c'est clair...)
                    for (int v=0; v<voisinsCommuns.size(); ++v) {
                        Triangle temp = new Triangle(tableau[i],tableau[indices_voisins[i][u]],tableau[voisinsCommuns.get(v)]);
                        boolean stopSearch=false;
                        for (int w=0; w<trianglesIcosaedre.size() && !stopSearch; ++w) {
                            if (temp.equals(trianglesIcosaedre.get(w))) {
                                stopSearch=true;
                            }
                        }
                        if (!stopSearch) {
                            trianglesIcosaedre.add(temp);
                        }
                    }
                }
            }
        }

        Vector<Triangle> trianglessphere = new Vector<>();//maintenant qu'on a un bel icosaèdre on peut le subdiviser autant de fois qu'on veut (expérience: 2 fois c'est bien)
        for (int i=0; i<nbreSubdivisions; ++i) {
            for (int u=0; u<trianglesIcosaedre.size(); ++u) {
                //on rajoute plein de triangles :D
                Vect pt1 = new Vect(trianglesIcosaedre.get(u).vects[0]);
                Vect pt2 = new Vect(trianglesIcosaedre.get(u).vects[1]);
                Vect pt3 = new Vect(trianglesIcosaedre.get(u).vects[2]);
                Vect pt12 = pt1.plus(pt2);
                Vect pt13 = pt1.plus(pt3);
                Vect pt23 = pt2.plus(pt3);
                pt12.setNorme(pt1.norme());
                pt13.setNorme(pt1.norme());
                pt23.setNorme(pt1.norme());//on projette sur la sphère
                trianglessphere.add( new Triangle(pt1,pt12,pt13));
                trianglessphere.add( new Triangle(pt2,pt12,pt23));
                trianglessphere.add( new Triangle(pt3,pt13,pt23));
                trianglessphere.add( new Triangle(pt12,pt23,pt13));
            }
            Vector<Triangle> temp = trianglesIcosaedre;
            trianglesIcosaedre = trianglessphere;
            trianglessphere = temp;
            trianglessphere.clear();
        }

        int iMax=trianglesIcosaedre.size();
        for (int i=0; i<iMax; ++i) {//pour chaque triangle, si il est au millieu on le dédouble
            trianglesIcosaedre.get(i).ajouterTriangleIfMillieu(trianglesIcosaedre);
        }

        float[] ret = new float[5*3*trianglesIcosaedre.size()];

        for (int i=0; i<trianglesIcosaedre.size(); ++i) {
            trianglesIcosaedre.get(i).order();
            trianglesIcosaedre.get(i).findTexCoords();
            trianglesIcosaedre.get(i).print(ret,5*3*i);
        }
        Log.e("SphereCreator points",""+ret.length);
        return ret;
    }

    static Vector<Integer> intersection(int[] a, int[] b) {
        Vector<Integer> ret = new Vector<>();
        for (int i=0;i<a.length; ++i) {
            for (int u=0; u<b.length; ++u) {
                if (a[i] == b[u]) {
                    ret.add(a[i]);
                }
            }
        }
        return ret;//TODO improve cross algorithm (even for 12 points it's still slow)
    }


    private static final double RAYON_SPHERE = 1.91211303259;
    
    private static class Vect{
        public Vect(float a, float b, float c) {
            x=a;
            y=b;
            z=c;
        }
        public Vect(Vect b) {
            x=b.x;
            y=b.y;
            z=b.z;
        }

        public Vect vectoriel(Vect b) {
            return new Vect(y*b.z-z*b.y, z*b.x-x*b.z, x*b.y-b.x*y);
        }

        public Vect plus(Vect b) {
            return new Vect(x+b.x,y+b.y,z+b.z);
        }

        public boolean equal(Vect b) {
            return x==b.x && y==b.y && z==b.z;
        }

        public Vect by(float a) {
            return new Vect(a*x,a*y,a*z);
        }

        public float dot(Vect b) {
            return x*b.x + y*b.y + z*b.z;
        }

        public float norme() {
            return (float) Math.sqrt(x*x+y*y+z*z);
        }

        void setNorme(float norm) {
            float normeActu = norme();
            x *= norm/normeActu;
            y *= norm/normeActu;
            z *= norm/normeActu;
        }

        float x,y,z;
    }


    private static class Triangle{
        public Triangle(Vect a, Vect b, Vect c) {
            vects = new Vect[3];
            vects[0] = new Vect(a);
            vects[1] = new Vect(b);
            vects[2] = new Vect(c);
            tex = new float[6];
        }
        Vect identifier() {
            return vects[0].plus(vects[1]).plus(vects[2]);
        }

        boolean equals(Triangle b) {
            return identifier().plus(b.identifier().by(-1)).norme() < 0.01f;
        }

        /**
         * Comme on va les afficher, il faut que chaque triangle soit écrit dans l'ordre direct!
         */
        void order(){
            Vect normale = new Vect((vects[1].plus(vects[0].by(-1))).vectoriel(vects[2].plus(vects[0].by(-1))));
            if (identifier().dot(normale) < 0) {//si la normale est dirigée vers l'intérieur
                Vect vecTemp=vects[1];
                vects[1]=vects[2];
                vects[2]=vecTemp;//on échange l'ordre de déclaration des points
            }//on touche pas aux tex parceque y'en a pas *encore* besoin
        }

        void findTexCoords() {
            for (int i=0; i<3; ++i) {
                float partY = (float) Math.asin((RAYON_SPHERE-Math.abs(vects[i].y))/(RAYON_SPHERE+1))/ (float)Math.PI;
                float normeBase = (float) Math.sqrt(vects[i].x*vects[i].x + vects[i].z*vects[i].z);
                if (normeBase == 0) {
                    tex[2*i]=0.5f;
                    tex[2*i+1] = 0.5f;
                } else {
                    tex[2*i] = 0.5f + vects[i].x*partY/normeBase;
                    tex[2*i+1] = 0.5f+vects[i].z*partY/normeBase;
                }
            }
        }

        void ajouterTriangleIfMillieu(Vector<Triangle> trianglesIcosaedre) {
            for (int i=0; i<3; ++i) {
                if (new Vect(vects[i].x,0,vects[i].z).equal(new Vect(vects[(i+1)%3].x,0,vects[(i+1)%3].z))) {//il faut transformer ça en deux triangles
                    Vect nouvVect = new Vect(vects[i].x,0,vects[i].z);
                    trianglesIcosaedre.add(new Triangle(nouvVect,vects[(i+1)%3],vects[(i+2)%3]));
                    resetY((i+1)%3);
                    return;
                }
            }
        }
        void resetY(int i) {
            vects[i].y = 0;
        }


        void print(float[] tableau, int offset) {
            /**
             * ajoute 15 coordonnées au tableau.attention, pas de vérification de taille du tableau!
             */
            for (int i=0; i<3; ++i) {
                tableau[offset + 5*i]   = vects[i].x;
                tableau[offset + 5*i+1] = vects[i].y;
                tableau[offset + 5*i+2] = vects[i].z;
                tableau[offset + 5*i+3] = tex[2*i];
                tableau[offset + 5*i+4] = tex[2*i+1];
            }
        }

        Vect[] vects;
        float[] tex;
    }
}
