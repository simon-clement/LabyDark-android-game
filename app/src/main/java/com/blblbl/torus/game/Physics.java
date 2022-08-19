package com.blblbl.torus.game;

import android.util.Log;

import com.blblbl.torus.interaction_game.MainActivity;
import com.blblbl.torus.utils.TimeHandler;
import com.blblbl.torus.utils.Vect;

public abstract class Physics {
    private static final String TAG="Physics";

    public static void update(Environnement env, BalleManager balleManager, MainActivity activity) {
        /**
         * check death and objectives: this function is supposed to be called one to 10 times per seconds,
         * so the calculs here can be longer than the function "avancerRapidement".
         */
        Vect retTheorie=balleManager.getPos();//in retTheorie there is the position of the ball: it may be already inside a wall!
        long timeActuel=balleManager.getNewTime();

        if (mSlowed) {//we do NOT really slow the time to let the ball turn as fast as the user may want.
            int timeDiff=(int)timeActuel-(int)mTimeSlow;//may be negative (if the user use 2 hourglass at a time)
            if (timeDiff > DUREE_RALENTI) {
                mSlowed=false;
                balleManager.setSpeed(balleManager.VITESSE_ROTATION_BALLE);
            } else {
                balleManager.setSpeed(Math.min(balleManager.VITESSE_ROTATION_BALLE/4+timeDiff/700,balleManager.VITESSE_ROTATION_BALLE));
            }//the ball slowly gain speed
        }


        if (env.collisionPossibleWall(retTheorie,BalleManager.getRayon()+5)) {//5 is an arbitrary margin to increase permittivity of this test
            int mIndexObstacle = env.isInWalls(retTheorie, BalleManager.getRayon(), timeActuel);//first we look at the pure position of the ball
            if (mIndexObstacle == -1) {
                mIndexObstacle = env.collisionViolente(mFormerPos, retTheorie, timeActuel);//then we draw its trajectory and see if there's a wall on the way
            }
            if (mIndexObstacle != -1) {
                Log.v(TAG, "DEAAAAAAAAD");//Well, it's seems the user just lost his game
                activity.setDied(score);//this function prints at the screen "you died, etc..."
                mDead = true;
                balleManager.getPos().setX(mFormerPos.at(0));//we take the former position to avoid if possible being in a wall
                balleManager.getPos().setY(mFormerPos.at(1));
                balleManager.getPos().setZ(mFormerPos.at(2));
                mTimeDeath = TimeHandler.getTime();
            }
        }



        if (env.onBonus(retTheorie,BalleManager.getRayon())) {
            activity.newSablier();//we add a hourglass if we deserve it
        }

        if (env.onObjectif(retTheorie,BalleManager.getRayon())) {
            score++;//if we are on an objective, the score is incremented
            env.addRandomBonus(retTheorie);
            activity.setScore(score);
            for (int i=0; i<10; ++i) {
                env.addRandomObstacle(retTheorie);//let's add several abstacles
            }
            env.addRandomObjectif(retTheorie);//...and a new objective
        }
        mFormerPos=retTheorie;//we save it in case of the ball die next time!
    }

    public static void avancerRapidement(Environnement env,BalleManager balleManager,Ciel ciel) {
        /**
         *WARNING this is done on rendering Thread, please do nothing that can wait a little bit, use update() instead
         */
        if (!mDead) {
            Vect retTheorie=balleManager.getPos().plus(balleManager.avancement());
            if (retTheorie.at(1)-BalleManager.getRayon() < env.getHeight()) {
                balleManager.getPos().setY(env.getHeight()+BalleManager.getRayon());
                balleManager.resetVitesseChute();
                if (mJumping) {
                    balleManager.jump();
                }
            }

            ciel.turn(balleManager.avancement());//.cross(new Vect(0,1,0)),retTheorie.norme()/300000f);


            balleManager.updatePos(env);
            balleManager.changeAngle();
            mJumping=false;
        }
    }

    public static void slowTime(BalleManager balleManager) {
        balleManager.setSpeed(10);
        if (!mSlowed) {
            mSlowed = true;
            mTimeSlow = TimeHandler.getTime();
        } else {
            mTimeSlow += DUREE_RALENTI;
        }
    }

    public static void resetScore() {
        score=0;
    }

    private static Vect mFormerPos = new Vect(0,0,0);
    private static int score;
    public static long mTimeSlow;
    public static boolean mSlowed=false;
    private static final long DUREE_RALENTI=4000;
    public static long mTimeDeath;
    public static boolean mDead=false;
    public static boolean mJumping=false;
}
