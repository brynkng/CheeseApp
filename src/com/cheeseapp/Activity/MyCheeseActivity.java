package com.cheeseapp.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cheeseapp.DbAdapter.DbAdapter;
import com.cheeseapp.navigation.TabInfo;
import com.cheeseapp.R;

import java.util.ArrayList;

/**
 * User: Bryan King
 * Date: 7/10/12
 */
public class MyCheeseActivity extends SherlockActivity{

    protected SQLiteDatabase mDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = DbAdapter.getDbInstance(this);
    }

    @Override
    protected void onDestroy() {
        if (mDb.isOpen()) {
            mDb.close();
        }
        super.onDestroy();
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

        public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuItem help = menu.add("Help | Glossary");
        help.setIcon(R.drawable.action_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(MyCheeseActivity.this, Glossary.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    return true;
                }
            });

        return true;
    }
}
