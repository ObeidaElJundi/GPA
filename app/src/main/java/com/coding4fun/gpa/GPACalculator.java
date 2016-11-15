package com.coding4fun.gpa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coding4fun.adapters.GpaRVadapter;
import com.coding4fun.models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class GPACalculator extends Fragment {

    RecyclerView rv;
    GpaRVadapter adapter;
    List<Course> courses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gpa_calculator, container, false);
        //define views here
        rv = (RecyclerView) v.findViewById(R.id.GPA_RV);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        initRV();
        courses = new ArrayList<>();
        for(int i=0;i<=10;i++) courses.add(new Course());
        adapter = new GpaRVadapter(getContext(),courses);
        rv.setAdapter(adapter);
    }

    private void initRV(){
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setAddDuration(500);
        anim.setRemoveDuration(500);
        rv.setItemAnimator(anim);
    }
}