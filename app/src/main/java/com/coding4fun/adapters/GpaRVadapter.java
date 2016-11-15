package com.coding4fun.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.coding4fun.gpa.R;
import com.coding4fun.models.Course;

import java.util.List;

/**
 * Created by coding4fun on 01-Oct-16.
 */

public class GpaRVadapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private List<Course> courses;
    Context context;
    private static final int EMPTY_VIEW = 10;
    private static final int COURSE_VIEW = 11;

    public GpaRVadapter(Context context, List<Course> modelData) {
        this.context = context;
        courses = modelData;
    }

    //describes an item view, and
    //Contains references for all views that are filled by the data of the entry
    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        EditText courseName;
        WheelPicker creditWheel,gradeWheel;
        ImageView delete;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseName = (EditText) itemView.findViewById(R.id.courseName);
            creditWheel = (WheelPicker) itemView.findViewById(R.id.creditWheel);
            gradeWheel = (WheelPicker) itemView.findViewById(R.id.gradeWheel);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            initWheel(creditWheel);
            initWheel(gradeWheel);
            listeners();
        }

        public void initWheel(WheelPicker wp) {
            wp.setVisibleItemCount(3);
            wp.setSelectedItemPosition(2);
            wp.setIndicator(true);
            //wp.setIndicatorColor(context.getResources().getColor(R.color.textColorPrimary));
            wp.setIndicatorColor(context.getResources().getColor(R.color.colorAccent));
            wp.setItemTextColor(context.getResources().getColor(R.color.colorPrimary));
            //wp.setCurtain(true);
            //wp.setCurtainColor(Color.parseColor("#"));
            wp.setCurved(true);
            wp.setAtmospheric(true);
            wp.setMaximumWidthText("333");
            //wp.setBackgroundColor(context.getResources().getColor(R.color.accent));
            //wp.setBackgroundResource(R.drawable.wheel_bg);
            wp.setBackgroundColor(Color.parseColor("#ACBCCC"));
        }

        @Override
        public void onClick(View v) {
            courses.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        void listeners(){
            gradeWheel.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    courses.get(getAdapterPosition()).setGrade(position);
                }
            });
            creditWheel.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    courses.get(getAdapterPosition()).setCredit(position);
                }
            });
            courseName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    courses.get(getAdapterPosition()).setName(charSequence.toString());
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Return the size of the items list
    @Override
    public int getItemCount() {
        return courses.size()>0 ? courses.size() : 1;	//otherwise, even empty layout won't appear
    }

    @Override
    public int getItemViewType(int position) {
        if (courses.size() == 0)
            return EMPTY_VIEW;
        else if (courses.get(position) instanceof Course)
            return COURSE_VIEW;
        return super.getItemViewType(position);
    }

    // inflate the item (row) layout and create the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == EMPTY_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty, viewGroup, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_gpa_calculator, viewGroup, false);
        CourseViewHolder evh = new CourseViewHolder(v);
        return evh;
    }

    //display (update) the data at the specified position
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CourseViewHolder) {
            CourseViewHolder vh = (CourseViewHolder) viewHolder;
            Course course = courses.get(position);
            vh.courseName.setText(course.getName());
            vh.gradeWheel.setSelectedItemPosition(course.getGradeIndex());
            vh.creditWheel.setSelectedItemPosition(course.getCreditIndex());
        }
    }

}