package com.coding4fun.gpa;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.coding4fun.adapters.ViewPagerAdapter;

/**
 * Created by coding4fun & alRawas on 09-Oct-16.
 */

public class MainActivity extends AppCompatActivity {

    Toolbar tb;
    ViewPager vp;
    ViewPagerAdapter vAdapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initToolbar();
        setupViewPager();
        initTabLayout();

    }

    private void initToolbar(){
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setElevation(5);
    }


    private void setupViewPager() {
        vp = (ViewPager) findViewById(R.id.viewPager);
        vAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vAdapter.addFrag(new GPACalculator(), "GPA CALC");
        vAdapter.addFrag(new Timer(), "TIMER");
        vAdapter.addFrag(new Notebook(), "NOTEBOOK");
        vAdapter.addFrag(new Map(), "MAP");
        vp.setOffscreenPageLimit(3);
        vp.setAdapter(vAdapter);
        vp.setCurrentItem(2);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(vp);
        //setupTabIcons();
        vp.setCurrentItem(2);
		/*tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
			@Override
			public void onTabUnselected(Tab arg0) {

			}
			@Override
			public void onTabSelected(Tab arg0) {

			}
			@Override
			public void onTabReselected(Tab arg0) {

			}
		});*/
    }

}