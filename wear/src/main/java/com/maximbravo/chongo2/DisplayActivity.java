package com.maximbravo.chongo2;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.Constants;

/**
 * Created by Maxim Bravo on 7/3/2017.
 */

public class DisplayActivity extends Activity {
    private TextView character;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private TextView pinyin;
    private TextView definition;
    private TextView level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        defineVariables();
        updateNotification();
    }

    private void defineVariables() {
        level = (TextView) findViewById(R.id.level);
        character = (TextView) findViewById(R.id.character);
        linearLayout = (LinearLayout) findViewById(R.id.layout);
        relativeLayout = (RelativeLayout) findViewById(R.id.container);
        pinyin = (TextView) findViewById(R.id.pinyin);
        definition = (TextView) findViewById(R.id.definition);
        LayoutTransition transition = linearLayout.getLayoutTransition();
        if(transition != null) {
            transition.enableTransitionType(LayoutTransition.CHANGING);
        }
    }


    public void updateNotification() {
        final Intent contentIntent = getIntent();
        if(contentIntent != null) {
            character.setText(contentIntent.getStringExtra(Constants.CHARACTER_KEY));
            level.setText("" + contentIntent.getIntExtra(Constants.LEVEL_KEY, 0));
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pinyin.setVisibility(View.VISIBLE);
                    pinyin.setText(contentIntent.getStringExtra(Constants.PIYIN_KEY));
                    definition.setVisibility(View.VISIBLE);
                    definition.setText(contentIntent.getStringExtra(Constants.DEFINITION_KEY));
                }
            });
        }


    }
}
