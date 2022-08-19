package com.blblbl.torus.menu;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.blblbl.torus.game.BalleManager;
import com.blblbl.torus.game.Environnement;

/**
 * Puisque on est dans une vue, c'est ici qu'on va gérer les évènements (onTouchEvent)!
 */
public class MenuFondView extends GLSurfaceView {
    private static final String TAG="GLSurfaceView";
    public MenuFondView(Context context) {
        super(context);
        init(context);
    }
    public MenuFondView(Context context,AttributeSet useless) {
        super(context,useless);
        init(context);
    }

    public void init(Context context) {
        setEGLContextClientVersion(2);
        mRenderer = new FondMenuRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRenderer.linkWithWorld(new BalleManager(),new Environnement(10,45));
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.linkWithWorld(new BalleManager(),new Environnement(10,45));
        requestRender();
    }

    private FondMenuRenderer mRenderer;//note: ne JAMAIS accéder directement au renderer, utiliser queueEvent
}