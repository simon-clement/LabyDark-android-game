package com.blblbl.torus.interaction_game;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blblbl.torus.R;
import com.blblbl.torus.game.Physics;
import com.blblbl.torus.utils.TimeHandler;

public class MainActivity extends Activity {
    /**
     * l'activity principale, on met la vue dedans
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_game);
        RelativeLayout relativeLayout = (RelativeLayout) frameLayout.findViewById(R.id.layout_text_game);
        mTextScoreView=(TextView) relativeLayout.findViewById(R.id.scoreTextView);
        mTextDiedView= (TextView) relativeLayout.findViewById(R.id.diedTextView);
        mRetryButton= (Button) relativeLayout.findViewById(R.id.retryButton);
        mNombreSablierView = (TextView) relativeLayout.findViewById(R.id.nombreSablierTextView);
        mButtonSlowTime = (ImageButton) relativeLayout.findViewById(R.id.slowButton);

        mGameView = new GameView(this);

        mButtonSlowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameView.slowTime();
                mNombreSabliers--;//let's remove a hourglass
                if (mNombreSabliers <1) {//if there's no more hourglass we hide the button
                    mButtonSlowTime.setVisibility(View.GONE);
                    mNombreSablierView.setText("");
                } else {
                    mNombreSablierView.setText(getString(R.string.stringNumber,mNombreSabliers));//print the new number of hourglass
                }
            }
        });
        mNombreSabliers=0;
        mButtonSlowTime.setVisibility(View.GONE);//at the beginning there's no hourglass
        Physics.resetScore();
        setScore(0);//neither we have a score

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//we don't want the device to shutdown the screen
        TimeHandler.resetOffset();//we want the time to begin at 0!


        frameLayout.addView(mGameView,0);//we're ready to add the game view now
    }

    public void setScore(final int score) { //just prints the new score (doesn't change Physics)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextScoreView.setText(getString(R.string.stringScoreInGame,score));
            }
        });
    }

    public void newSablier() {
        mNombreSabliers++;//we add a hourglass and print the new number of them
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mButtonSlowTime.setVisibility(View.VISIBLE);
                mNombreSablierView.setText(getString(R.string.stringNumber,mNombreSabliers));
            }
        });
    }

    public void resetDied() {
        //this fuction must be called if the game begin or if someone has hit "try again"
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRetryButton.setVisibility(View.GONE);
                mTextDiedView.setVisibility(View.GONE);
                Physics.resetScore();
                mTextScoreView.setText(getString(R.string.stringScoreInGame,0));
                mNombreSabliers=0;
                mNombreSablierView.setText("");
            }
        });
    }
    public void setDied(final int score) {
        //this function must be called when... THE BALL DIED! YOU DON'T SAY?!
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextDiedView.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.VISIBLE);
                mButtonSlowTime.setVisibility(View.GONE);
                mNombreSabliers=0;
                mNombreSablierView.setText("");
                mRetryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetDied();
                        mGameView.letTheGameBegins();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();//when the activity pauses, we stop the time (literally in timeHandler)
        TimeHandler.onPause();
        mGameView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();//when the activity resume we can take the time we have now =)// TODO faire un 3...2...1...??
        TimeHandler.onResume();
        mGameView.onResume();
    }
    private ImageButton mButtonSlowTime;
    private Button mRetryButton;
    private TextView mNombreSablierView;
    private TextView mTextScoreView;
    private TextView mTextDiedView;
    private GameView mGameView;
    private int mNombreSabliers;
}
