package com.cheeseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cheeseapp.navigation.TabInfo;
import com.cheeseapp.R;

import java.util.ArrayList;

/**
 * User: Bryan King
 * Date: 7/10/12
 */
public class MyCheeseActivity extends SherlockActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, CheeseList.class);

                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        return super.onMenuItemSelected(featureId, item);
    }
}
