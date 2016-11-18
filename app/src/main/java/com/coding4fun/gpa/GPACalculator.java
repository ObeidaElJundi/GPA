package com.coding4fun.gpa;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coding4fun.adapters.GpaRVadapter;
import com.coding4fun.models.Course;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class GPACalculator extends Fragment implements View.OnClickListener {

    RecyclerView rv;
    GpaRVadapter adapter;
    List<Course> courses;
    FloatingActionButton done,add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gpa_calculator, container, false);
        //define views here
        rv = (RecyclerView) v.findViewById(R.id.GPA_RV);
        done = (FloatingActionButton) v.findViewById(R.id.GPA_done_FAB);
        add = (FloatingActionButton) v.findViewById(R.id.GPA_add_FAB);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        initRV();
        courses = new ArrayList<>();
        /*for(int i=0;i<=10;i++)*/ courses.add(new Course());
        adapter = new GpaRVadapter(getContext(),courses);
        rv.setAdapter(adapter);
        done.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    private void initRV(){
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setHasFixedSize(true);
        DefaultItemAnimator anim = new DefaultItemAnimator();
        anim.setAddDuration(500);
        anim.setRemoveDuration(500);
        rv.setItemAnimator(anim);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.GPA_add_FAB:
                courses.add(new Course());
                if(courses.size() == 1){
                    adapter.notifyItemChanged(0);
                } else {
                    adapter.notifyItemInserted(courses.size()-1);
                    rv.scrollToPosition(courses.size()-1);
                }
                break;
            case R.id.GPA_done_FAB:
                if(courses.size() > 0){
                    alertGPA();
                } else{
                    Toast.makeText(getContext(), "No Courses!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private double calcGPA(){
        double n = 0, d = 0;
        for(Course c : courses){
            n += Course.gradesByNumber[c.getGradeIndex()] * Course.credits[c.getCreditIndex()];
            d += Course.credits[c.getCreditIndex()];
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(n/d));
    }

    void alertGPA(){
        new AlertDialog.Builder(getContext())
                .setTitle(calcGPA()+"")
                .setMessage(getAlertMsg())
                .setIcon(R.drawable.grad_cap)
                .setPositiveButton("Show Calculations", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        alertCalculations();
                    }})
                .setNegativeButton("OK", null)
                .show();
    }

    String getAlertMsg(){
        double gpa = calcGPA();
        String msg = "Your GPA is "+gpa+"\n";
        if(gpa == 4.0)
            return msg+"Get a life, NERD!";
        else if(gpa >= 3.5)
            return msg+"Awesome! Keep it up.";
        else if(gpa >= 3.0)
            return msg+"Nice job";
        else if(gpa >= 2.5)
            return msg+"Not bad!";
        else if(gpa >= 2.0)
            return msg+"You can do better ;)";
        else if(gpa > 1.0)
            return msg+"Ops! You have to do better!";
        else
            return msg+"I suggest you drop out uni! :(";
    }

    void alertCalculations(){
        AlertDialog.Builder d = new AlertDialog.Builder(getContext());
        d.setTitle("GPA Calculations");
        d.setMessage(getGPACalculations());
        d.setIcon(R.drawable.grad_cap);
        d.setNegativeButton("OK", null);
        AlertDialog a = d.show();
        TextView dTV = (TextView) a.findViewById(android.R.id.message);
        dTV.setGravity(Gravity.CENTER);
        dTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        a.show();
    }

    String getGPACalculations(){
        String msg = "sum(grade*credits)\n____________________\nsum(credits)\n\n";
        String numMSG="",dMSG="";
        double num=0,d=0;
        for(Course c : courses){
            num += Course.gradesByNumber[c.getGradeIndex()] * Course.credits[c.getCreditIndex()];
            d += Course.credits[c.getCreditIndex()];
            numMSG += Course.gradesByNumber[c.getGradeIndex()]+" * "+Course.credits[c.getCreditIndex()]+" + ";
            dMSG += Course.credits[c.getCreditIndex()] + " + ";
        }
        msg += numMSG.substring(0, numMSG.length()-3);
        msg += "\n";
        for(int i=0;i<numMSG.length() && i<38;i++)
            msg += "_";
        msg += "\n";
        msg += dMSG.substring(0, dMSG.length()-3);
        msg += "\n\n\n" + (num/d);
        return msg;
    }
}