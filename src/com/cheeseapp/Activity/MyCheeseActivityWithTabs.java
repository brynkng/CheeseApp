package com.cheeseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.cheeseapp.R;
import com.cheeseapp.navigation.TabInfo;

import java.util.ArrayList;

/**
 * User: Bryan King
 * Date: 7/10/12
 */
public class MyCheeseActivityWithTabs extends MyCheeseActivity implements ActionBar.TabListener {

    public static final String TARGET_TAB_POSITION_KEY = "targetTabPosition";
    public static final String LAST_TAB_POSITION_KEY = "lastTabPosition";

    public static final int TAB_POSITION_CHEESES = 0;
    public static final int TAB_POSITION_JOURNAL = 1;
    public static final int TAB_POSITION_GLOSSARY = 2;

    private int _currentTabPosition;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _setupTabs();
    }

    private void _setupTabs() {
        _currentTabPosition = getIntent().getIntExtra(TARGET_TAB_POSITION_KEY, 0);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        ArrayList<TabInfo> _tabInfoList = new ArrayList<TabInfo>();

        TabInfo cheeses = new TabInfo("CHEESES", CheeseList.class);
        _tabInfoList.add(cheeses);

        TabInfo journal = new TabInfo("JOURNAL", JournalHome.class);
        _tabInfoList.add(journal);

        for (TabInfo tabInfo : _tabInfoList) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText(tabInfo.getTitle());
            tab.setTabListener(this);
            tab.setTag(tabInfo.getTargetClass());
            getSupportActionBar().addTab(tab, false);
        }

        getSupportActionBar().setSelectedNavigationItem(_currentTabPosition);
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (_shouldChangeTabActivity(tab)) {
            _changeTabActivity(tab);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Class targetClass = (Class) tab.getTag();
        if (this.getClass() != targetClass) {
            _changeTabActivity(tab);
        }
    }

    private boolean _shouldChangeTabActivity(ActionBar.Tab tab) {
        Class targetClass = (Class) tab.getTag();
        return (this.getClass() != targetClass) && (tab.getPosition() != _currentTabPosition);
    }

    private void _changeTabActivity(ActionBar.Tab tab) {
        Class targetClass = (Class) tab.getTag();
        Intent intent = new Intent(this, targetClass);

        intent.putExtra(TARGET_TAB_POSITION_KEY, tab.getPosition());
        intent.putExtra(LAST_TAB_POSITION_KEY, _currentTabPosition);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
