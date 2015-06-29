package com.rocdev.android.piet.fooicalculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by Piet on 10-1-2015.
 */
public class InstellingenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //toon fragment
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new InstellingenFragment()).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}

