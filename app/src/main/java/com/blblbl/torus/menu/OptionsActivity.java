package com.blblbl.torus.menu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.blblbl.torus.R;

public class OptionsActivity extends Activity {
    protected SharedPreferences mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_options);
        mSettings = getSharedPreferences(getString(R.string.stringSharedPreferencesOptions),MODE_PRIVATE);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupTouchChoice);
        if (mSettings.getInt(getString(R.string.stringPrefsModeTouch),0) == 0)
            radioGroup.check(R.id.radioTouchFalse);
        else
            radioGroup.check(R.id.radioTouchTrue);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = mSettings.edit();
                if (checkedId == R.id.radioTouchTrue)
                    editor.putInt(getString(R.string.stringPrefsModeTouch),1);
                if (checkedId == R.id.radioTouchFalse)
                    editor.putInt(getString(R.string.stringPrefsModeTouch),0);
                editor.apply();
            }
        });

        SeekBar sensibiliteBar= (SeekBar) findViewById(R.id.BarSensibilite);
        sensibiliteBar.setProgress(mSettings.getInt(getString(R.string.stringPrefsSensibilite),50));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SeekBar sensibiliteBar= (SeekBar) findViewById(R.id.BarSensibilite);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(getString(R.string.stringPrefsSensibilite),sensibiliteBar.getProgress());
        editor.apply();
    }
}
