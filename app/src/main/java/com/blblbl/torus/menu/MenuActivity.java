package com.blblbl.torus.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.blblbl.torus.R;
import com.blblbl.torus.interaction_game.MainActivity;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout_menu);
        RelativeLayout relativeLayout = (RelativeLayout) frameLayout.findViewById(R.id.layout_relative_menu);

        Button playButton= (Button) relativeLayout.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MenuActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Button optionsButton= (Button) relativeLayout.findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MenuActivity.this,OptionsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MenuFondView test = (MenuFondView) findViewById(R.id.MenuFondViewMenu);
        test.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        MenuFondView test = (MenuFondView) findViewById(R.id.MenuFondViewMenu);
        test.onPause();
    }
}
