package com.blblbl.torus.game;

import com.blblbl.torus.utils.TimeHandler;
import com.blblbl.torus.utils.Utils;
import com.blblbl.torus.utils.Vect;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Environnement {
    private static final String TAG="Environnement";
    private static final int NOMBRE_MAX_OBSTACLES=550;

    public static final int NOMBRE_CASES_AFFICHABLES=6;


    public Environnement(int size,int taille) {
        mTailleGrille=size*taille;
        mTailleCase = taille;
        mNombreCases = size;
        mCoteObstacle = (float)mTailleCase/4.5f;
        mRand = new Random();
    }


    public void init(Vect pos) {
        grilleObstacles = new int[mNombreCases*2][mNombreCases];
        for (int i=0; i<mNombreCases; ++i)
            for (int u=0; u<2*mNombreCases; ++u)
                grilleObstacles[u][i] = -1;

        mObstacles=new Vector<>();
        mObjectifs=new Vector<>();
        mBonus=new Vector<>();
        mObstaclesAffichables=new Vector<>();
        addRandomObjectif(pos);
    }

    public Vect getPosBalleAffichable(Vect pos) {
        return new Vect(pos.at(0) % mTailleCase+mTailleCase*(NOMBRE_CASES_AFFICHABLES/2), pos.at(1),pos.at(2) % mTailleCase+mTailleCase*(NOMBRE_CASES_AFFICHABLES/2));
    }
    public Vect getPosBalleDansCase(Vect pos) {
        return new Vect(pos.at(0) % mTailleCase, pos.at(1),pos.at(2) % mTailleCase);
    }
    public Vect coinInferieurPosBalleAffichable() {
        return new Vect(mTailleCase*(NOMBRE_CASES_AFFICHABLES/2),mHeight,mTailleCase*(NOMBRE_CASES_AFFICHABLES/2));
    }

    public int numeroCaseBalle(Vect posBall) {
        int caseX,caseZ;
        caseX = ((int)posBall.at(0) / mTailleCase)%mNombreCases;
        caseZ = ((int)posBall.at(2) / mTailleCase)%mNombreCases;
        return caseZ*mNombreCases + caseX;
    }

    public void updateCaseObstacles(int caseBall) {

        final int VISION_SIDE=7;
        int xCase = caseBall % mNombreCases,yCase = caseBall / mNombreCases;
        int xMin=-VISION_SIDE, xMax=VISION_SIDE, yMin=-VISION_SIDE, yMax=VISION_SIDE;

        mObstaclesAffichables.clear();

        for (int i=xMin; i<=xMax; ++i) {
            for (int u=2*yMin; u<=2*yMax; ++u) {
                int index=grilleObstacles[(u+2*(yCase+mNombreCases))%(2*mNombreCases)][(i+xCase+mNombreCases)%mNombreCases];
                if (index != -1) {
                    if (index < mObstacles.size()) {
                        mObstaclesAffichables.add(index);
                        mObstacles.get(index).updateFast(caseBall);
                    } else {
                        grilleObstacles[(u+2*(yCase+mNombreCases))%(2*mNombreCases)][(i+xCase+mNombreCases)%mNombreCases]=-1;
                    }
                }
            }
        }
    }

    public void drawObstacles(Afficheur afficheur,Vect posBallDebutFrame,Vect dirBall,long time) {
        /**
         * This function must efficiently draw the obstacles. So we first choose the visibles ones then we print them
         * Problem: it's ALWAYS too slow ><
         */
        afficheur.prepareForObstacles();//prepares everything, there will just left the transMatrix and drawArray

        Vect posDebutCase = coinInferieurPosBalleAffichable();
        Vect posCamera=posBallDebutFrame.plus(dirBall.by(-25));

        for (int i: mObstaclesAffichables) {
            if (i<mObstacles.size())
                if (mObstacles.get(i).isInFrontOfBallFast(posCamera,dirBall,posDebutCase.at(0),posDebutCase.at(2)))
                    afficheur.draw(mObstacles.get(i),posDebutCase,time);
        }
    }


    public void drawObjectifs(Afficheur afficheur,Vect posBallDebutFrame,int caseBalleDebutFrame,Vect dirBall,long time) {
        /**
         * This function must efficiently draw the goal and an Arrow
         */
        for (Objectif obj : mObjectifs) {//en théorie il n'y en a qu'un!
            afficheur.draw(obj, this, caseBalleDebutFrame, time);
        }
        //Utils.checkGlError("drawObjectifs");
    }
    public void drawBonus(Afficheur afficheur,int caseBalleDebutFrame,long time) {
        /**
         * This function must efficiently draw the goal and an Arrow
         */
        afficheur.prepareForBonus();
        try {
            for (Bonus bonus : mBonus) {//en théorie il n'y en a qu'un!
                afficheur.drawBonus(bonus, this, caseBalleDebutFrame, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Utils.checkGlError("drawObjectifs");
    }

    public void drawIndications(Afficheur afficheur,Vect posBallDebutFrame,int caseBalleDebutFrame) {
        /**
         * This function must efficiently draw an Arrow
         */
        for (Objectif obj : mObjectifs) {//en théorie il n'y en a qu'un!
            afficheur.drawArrow(obj.angleFrom(posBallDebutFrame,caseBalleDebutFrame));
        }
    }

    public boolean onObjectif(Vect posBall,float rayon) {
        int caseX = (int) (posBall.at(0)%mTailleGrille)/mTailleCase;
        int caseZ = (int) (posBall.at(2)%mTailleGrille)/mTailleCase;
        Vect posBallAff= getPosBalleDansCase(posBall);
        for (int i=0; i<mObjectifs.size(); ++i) {
            if (caseX == mObjectifs.get(i).getGridX() && caseZ == mObjectifs.get(i).getGridZ()) {
                if (mObjectifs.get(i).isInside(posBallAff,rayon)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean onBonus(Vect posBall,float rayon) {
        int caseX = (int) (posBall.at(0)%mTailleGrille)/mTailleCase;
        int caseZ = (int) (posBall.at(2)%mTailleGrille)/mTailleCase;
        Vect posBallAff= getPosBalleDansCase(posBall);
        for (int i=0; i<mBonus.size(); ++i) {
            if (caseX == mBonus.get(i).getGridX() && caseZ == mBonus.get(i).getGridZ()) {
                if (mBonus.get(i).isInside(posBallAff,rayon)) {
                    mBonus.remove(i);//on considère qu'il y a pas TROP de bonus...
                    return true;
                }
            }
        }
        return false;
    }

    public boolean collisionPossibleWall(Vect posBall,float rayon) {
        /**
         * Cette fonction renvoie false si il est impossible de rencontrer un obstacle!
         * Elle est sensée être rapide et renvoyer false dans ~la moitié des cas
         */
        Vect positionDansCase = getPosBalleDansCase(posBall);
        return (positionDansCase.at(0)-rayon<mCoteObstacle || positionDansCase.at(0)+rayon > mTailleCase-mCoteObstacle
                || positionDansCase.at(2)-rayon<mCoteObstacle || positionDansCase.at(2)+rayon > mTailleCase-mCoteObstacle);
    }

    public int collisionViolente(Vect posBall1, Vect posBall2,long time){
        /**
         * Cette fonction renvoie vrai si on a traversé un mur en changeant de case:
         * Elle ne devrait jamais renvoyer vrai, car inWall devrait être à True avant.
         * On garde cette fonction car il pourrait y avoir un lag juste avant de rentrer dans un mur
         * et sans cette fonction on peut traverser le mur en une actualisation!
         */
        int case1x=((int)posBall1.at(0)/mTailleCase)%mNombreCases;
        int case1y = ((int)posBall1.at(2)/mTailleCase)%mNombreCases;
        int case2x=((int)posBall1.at(0)/mTailleCase)%mNombreCases;
        int case2y = ((int)posBall1.at(2)/mTailleCase)%mNombreCases;
        int diffX=(case2x-case1x+mNombreCases)%mNombreCases;
        int diffY=(case2y-case1y+mNombreCases)%mNombreCases;
        int index=-1;
        if (diffX == 1) {
            index = grilleObstacles[(2*case1y+1)%(2*mNombreCases)][case1x];
        } else if (diffX== mNombreCases-1) {
            index = grilleObstacles[(2*case1y+1)%(2*mNombreCases)][(case1x-1+mNombreCases)%mNombreCases];
        }
        if (diffY == 1) {
            index = grilleObstacles[(2 * case1y + 2) % (2 * mNombreCases)][case1x];
        } else if (diffY == mNombreCases-1) {
            index = grilleObstacles[2*case1y][case1x];
        }
        if (index != -1) {
            if ((posBall2.at(1)+posBall1.at(1))/2 < mObstacles.get(index).getHeight(time)) //on considère que si y'a une collision violente (déjà mégarare), ce sera sur une seule direction
                return index;
        }
        return -1;
    }

    public int isInWalls(Vect posBall, float rayon, long time) {
        int caseBalle=numeroCaseBalle(posBall);
        Vect posBallAff=getPosBalleDansCase(posBall);
        int casex = caseBalle % mNombreCases;
        int casey = caseBalle / mNombreCases;
        for (int i=-2; i<4; i++) {
            for (int u = - 1; u < 2; ++u) {
                int index = grilleObstacles[(2*(casey+mNombreCases)+i)%(2*mNombreCases)][(casex+u+mNombreCases)%mNombreCases];
                if (index != -1)
                    if (mObstacles.get(index).isInside(posBallAff,caseBalle, rayon,time)) {
                        return index;
                    }
            }
        }
        return -1;
    }


    public void addRandomObstacle(Vect pos) {
        if (mObstacles.size()<NOMBRE_MAX_OBSTACLES) {
            int xCase = mRand.nextInt(mNombreCases);
            int yCase = mRand.nextInt(2 * mNombreCases);
            int xCaseBall = (int)pos.at(0)/mTailleCase;
            int yCaseBall = 2*(int)pos.at(2)/mTailleCase;
            if (grilleObstacles[yCase][xCase] == -1 && Math.abs(xCase - xCaseBall)%mNombreCases>3 && Math.abs(yCase-yCaseBall)%mNombreCases>6) {
                grilleObstacles[yCase][xCase] = mObstacles.size();
                mObstacles.add(new Obstacle(xCase, yCase, TimeHandler.getTime()));
            }
        }
    }
    public void addRandomObjectif(Vect pos) {
        int aleaCase = randomCaseFrom(pos,7);
        mObjectifs.clear();
        mObjectifs.add(new Objectif(aleaCase%mNombreCases, aleaCase/mNombreCases));

    }
    public void addRandomBonus(Vect pos) {

        if (mBonus.size() <3) {
            int aleaCase = randomCaseFrom(pos,6);
            mBonus.add(new Bonus(aleaCase % mNombreCases, aleaCase / mNombreCases));
        }
    }

    private int randomCaseFrom(Vect pos, int stepDistance) {
        int caseBall = numeroCaseBalle(pos);
        ArrayList<Integer> choixCases = new ArrayList<>();
        choixCases.add(caseBall);
        boolean visited[] = new boolean[mNombreCases * mNombreCases];
        visited[caseBall] = true;
        choixCases = recurrFindAtteignable(stepDistance, choixCases, visited);
        return choixCases.get(mRand.nextInt(choixCases.size()));
    }

    public ArrayList<Integer> recurrFindAtteignable(int step, ArrayList<Integer> ret, boolean[] visited) {//TODO debug ça
        if (step == 0)
            return ret;
        int dirStep[] = {1,mNombreCases*mNombreCases-1,mNombreCases,mNombreCases*(mNombreCases-1)};//pas pour aller dans chaque direction
        int xTester[] = {1,0,0,0};
        int yTester[] = {1,1,2,0};
        ArrayList<Integer> retBis = new ArrayList<>();
        for (int caseAtteignable:ret) {
            for (int i=0; i<4; ++i) {//on va considérer la case d'indice (caseAtteignable+dirStep[i])%visited.length
                if (!visited[(caseAtteignable+dirStep[i])%visited.length]//si on n'est pas allé sur cette case et que il n'y a pas de mur entre celle-ci et caseAtteignable
                    && grilleObstacles[(2*(caseAtteignable/mNombreCases)+yTester[i])%(2*mNombreCases)][(caseAtteignable%mNombreCases + xTester[i])%mNombreCases] == -1) {
                    retBis.add((caseAtteignable+dirStep[i])%visited.length);
                    visited[(caseAtteignable+dirStep[i])%visited.length] = true;
                }
            }
        }
        ArrayList essai = recurrFindAtteignable(step-1,retBis,visited);
        if (essai.size() != 0)
            return essai;
        return ret;
    }

    public class Obstacle {
        /**
         * the position in grid is like this: _ _ _ _
         *                                   | | | |
         *                                    _ _ _ _
         *                                   | | | |
         *
         *
         * consequence: with a case (x,y),
         * the wall "up"    is (x,2*y),
         * the wall "left"  is (x,2*y+1),
         * the wall "right" is (x+1,2*y+1),
         * the wall "down"  is (x,2*y+1)
         */
        Obstacle(int placeInGridX, int placeInGridZ, final long timeBeginning) {
            gridX = placeInGridX;//placeInGridX go from 0 to mNombreCases.
            gridZ = placeInGridZ;//placeInGridZ go from 0 to 2*mNombreCases.
            hauteur = new Utils.fonction() {
                @Override
                public float get(long time) {
                    if (time< timeBeginning+45*500)
                        return ((float)time-timeBeginning)/500f;
                    return 45;
                }
            };
        }//the parity of gridZ determines if it's on the "side" of a square or on the back/front
        public boolean isOnSide() {
            return (gridZ % 2)==1;
        }
        public float getCenterX(int numeroCaseBalle,float xDebutCase) {
            int xCase = gridX - (numeroCaseBalle % mNombreCases);
            if (xCase>mNombreCases/2) {
                xCase-=mNombreCases;
            } else if (xCase<-mNombreCases/2) {
                xCase+=mNombreCases;
            }
            return xDebutCase + xCase*mTailleCase + mTailleCase/2f + ((gridZ)%2)*((float)mTailleCase/2f);
        }

        public float getHeight(long time) {
            return mHeight+hauteur.get(time);
        }

        public float getCenterZ(int numeroCaseBalle,float zDebutCase) {
            int yCase = numeroCaseBalle/mNombreCases;
            yCase = (gridZ - 2*yCase)%(2*mNombreCases);
            if (yCase>mNombreCases) {
                yCase-=2*mNombreCases;
            } else if (yCase<-mNombreCases) {
                yCase+=2*mNombreCases;
            }
            return zDebutCase + (float)yCase*mTailleCase/2f;
        }

        public boolean isInside(Vect posBalleRel,int caseBalle, float rayon,long time) {
            float xObstacle=mCoteObstacle/2f;
            float zObstacle=mTailleCase/2;
            float xCentre=getCenterX(caseBalle,0);
            float zCentre=getCenterZ(caseBalle,0);
            if (!isOnSide()) {
                xObstacle = mTailleCase / 2f;
                zObstacle = mCoteObstacle / 2f;
            }
            return (Math.abs(xCentre-posBalleRel.at(0))%mTailleGrille-rayon<xObstacle
                    && Math.abs(zCentre - posBalleRel.at(2))%mTailleGrille-rayon<zObstacle
                    && posBalleRel.at(1) - rayon < getHeight(time));
        }


        public float getCenterXFast(float xDebutCase) {
            return xDebutCase + keepX;
        }
        public float getCenterZFast(float zDebutCase) {
            return zDebutCase + keepZ;
        }
        public void updateFast(int numeroCaseBalle) {
            int xCase = gridX - (numeroCaseBalle % mNombreCases);
            if (xCase>mNombreCases/2) {
                xCase-=mNombreCases;
            } else if (xCase<-mNombreCases/2) {
                xCase+=mNombreCases;
            }
            keepX =  xCase*mTailleCase + mTailleCase/2f + ((gridZ)%2)*((float)mTailleCase/2f);

            int yCase = numeroCaseBalle/mNombreCases;
            yCase = (gridZ - 2*yCase)%(2*mNombreCases);
            if (yCase>mNombreCases) {
                yCase-=2*mNombreCases;
            } else if (yCase<-mNombreCases) {
                yCase+=2*mNombreCases;
            }
            keepZ = (float)yCase*mTailleCase/2f;
        }

        public boolean isInFrontOfBallFast(Vect posBall, Vect dirBall,float debutCaseX,float debutCaseZ) {
            /**
             * Cette fonction a pour but de "filtrer les obstacles qui ne seront pas à l'écran.
             * Il faut faire un filtre effecace pour augmenter les FPS OMG
             */
            float dot=(getCenterXFast(debutCaseX) - posBall.at(0))*dirBall.at(0) +(getCenterZFast(debutCaseZ) - posBall.at(2))*dirBall.at(2);
            return dot>0;
        }

        private Utils.fonction hauteur;
        private final int gridX,gridZ;
        private float keepX,keepZ;
        public int getGridX() { return gridX;}
        public int getGridZ() { return gridZ;}

    }

    public class Objectif {
        public Objectif(int xCoord,int zCoord) {
            mGridZ = zCoord;
            mGridX = xCoord;
            mRayon = 9;
            mInCase=new Vect(mTailleCase/2f,mHeight+7,mTailleCase/2f);
        }

        public float getCenterX(int numeroCaseBalle,float xDebutCase) {
            int xCase = mGridX - (numeroCaseBalle % mNombreCases);
            if (xCase>mNombreCases/2) {
                xCase-=mNombreCases;
            } else if (xCase<-mNombreCases/2) {
                xCase+=mNombreCases;
            }
            return xDebutCase + xCase*mTailleCase + mInCase.at(0);
        }

        public float getCenterY(long time) {
            return mInCase.at(1)+2*(float)Math.sin((double)time/500);
        }

        public float getCenterZ(int numeroCaseBalle,float zDebutCase) {
            int yCase = mGridZ - numeroCaseBalle/mNombreCases;
            if (yCase>mNombreCases/2) {
                yCase-=mNombreCases;
            } else if (yCase<-mNombreCases/2) {
                yCase+=mNombreCases;
            }
            return zDebutCase + (float)yCase*mTailleCase + mInCase.at(2);
        }

        public float angleFrom(Vect posBall,int caseBalle) {
            try {
                Vect diff = Vect.normalize(new Vect(getCenterX(caseBalle, 0), posBall.at(1), getCenterZ(caseBalle, 0))
                        .plus(getPosBalleDansCase(posBall).by(-1)));
                return (float) Math.acos(diff.at(2))*Math.signum(diff.at(0))*180/3.1415f -90;
            } catch (Exception e) {
                return 0;
            }
        }

        int getGridX() { return mGridX;}
        int getGridZ() { return mGridZ;}

        public boolean isInside(Vect posBall,float rayon) {
            Vect diff=mInCase.plus(posBall.by(-1));
            return (diff.norme()-rayon < mRayon);
        }

        private float mRayon;
        private Vect mInCase;
        private int mGridX,mGridZ;
    }

    public class Bonus {
        public Bonus(int xCoord,int zCoord) {
            mGridZ = zCoord;
            mGridX = xCoord;
            mRayon = 6;
            mInCase=new Vect(mTailleCase/3f,mHeight+7,mTailleCase/3f);
        }

        public float getCenterX(int numeroCaseBalle,float xDebutCase) {
            int xCase = mGridX - (numeroCaseBalle % mNombreCases);
            if (xCase>mNombreCases/2) {
                xCase-=mNombreCases;
            } else if (xCase<-mNombreCases/2) {
                xCase+=mNombreCases;
            }
            return xDebutCase + xCase*mTailleCase + mInCase.at(0);
        }

        public float getCenterY(long time) {
            return mInCase.at(1)+2*(float)Math.sin((double)time/500);
        }

        public float getCenterZ(int numeroCaseBalle,float zDebutCase) {
            int yCase = mGridZ - numeroCaseBalle/mNombreCases;
            if (yCase>mNombreCases/2) {
                yCase-=mNombreCases;
            } else if (yCase<-mNombreCases/2) {
                yCase+=mNombreCases;
            }
            return zDebutCase + (float)yCase*mTailleCase + mInCase.at(2);
        }

        int getGridX() { return mGridX;}
        int getGridZ() { return mGridZ;}

        public boolean isInside(Vect posBall,float rayon) {
            Vect diff=mInCase.plus(posBall.by(-1));
            return (diff.norme()-rayon < mRayon);
        }

        private float mRayon;
        private Vect mInCase;
        private int mGridX,mGridZ;
    }

    private int[][] grilleObstacles;

    Vector<Integer> mObstaclesAffichables;

    private Vector<Obstacle> mObstacles;
    private Vector<Objectif> mObjectifs;
    private Vector<Bonus> mBonus;

    private final float mHeight=-1f;
    private final int mTailleGrille;//mTailleCase*mNombreCase
    private final int mTailleCase;
    private final float mCoteObstacle;
    private final int mNombreCases;

    private Random mRand;

    /*****
     * Getters
     *****/
    public float getHeight(){ return mHeight; }
    public int getTailleGrille() { return mTailleGrille; }
    public float getCoteObstacle() { return mCoteObstacle; }
    public float getTailleCase() {return mTailleCase;}
}
