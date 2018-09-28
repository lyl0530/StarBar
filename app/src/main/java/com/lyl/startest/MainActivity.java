package com.lyl.startest;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private StarBar star, star1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        star = (StarBar) findViewById(R.id.star);
        star1 = (StarBar) findViewById(R.id.star1);

        star.setIntegerMark(false);
        star.setStarCount(5);
        star.setStarMark(2.8f);

        star1.setStarCount(7);
        star1.setStarMark(6);
    }
}
