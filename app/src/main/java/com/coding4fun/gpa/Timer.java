package com.coding4fun.gpa;

import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coding4fun.others.TextProgressBar;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class Timer extends Fragment implements View.OnClickListener, View.OnTouchListener,
        GestureDetector.OnGestureListener, SeekBar.OnSeekBarChangeListener {

    TextProgressBar pb;
    TextView studyTimeTV,breakTimeTV;
    Button startTimer;
    SeekBar studySeekBar,breakSeekBar;
    boolean timerON = false, breakTime = false, flingUP=true, shown = true;
    Thread t;
    int progressStatus = 0, totalStudyingSeconds, totalBreakSeconds, secondsLeft;
    private Handler handler = new Handler();
    View timesLL;
    float dx,dy;
    GestureDetectorCompat gd;
    Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timer, container, false);
        //define views here
        pb = (TextProgressBar) v.findViewById(R.id.timer_progressbar);
        startTimer = (Button) v.findViewById(R.id.startTimerBTN);
        studyTimeTV = (TextView) v.findViewById(R.id.study_time_tv);
        breakTimeTV = (TextView) v.findViewById(R.id.break_time_tv);
        studySeekBar = (SeekBar) v.findViewById(R.id.study_seekbar);
        breakSeekBar = (SeekBar) v.findViewById(R.id.break_seekbar);
        timesLL = v.findViewById(R.id.timesLL);
        studySeekBar.setOnSeekBarChangeListener(this);
        breakSeekBar.setOnSeekBarChangeListener(this);
        startTimer.setOnClickListener(this);
        timesLL.setOnTouchListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        setHasOptionsMenu(true);	//assign menu only for this fragment
        gd = new GestureDetectorCompat(getContext(),this);
        //show(0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.timer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configure:
                if(shown) hide(333);
                else show(333);
                return true;
            default:
                return false;
        }
    }

    // Update the progress bar
    void updateProgressBar(){
        handler.post(new Runnable() {
            public void run() {
                pb.setProgress(progressStatus);
                String progressBarTitle = ((breakTime) ? "Break\n" : "Studying\n") + "\n"  + getTimeLeft();
                pb.setText(progressBarTitle);
            }
        });
    }

    String getTimeLeft(){
        String mins = (secondsLeft/60) + "";
        if(mins.length() == 1) mins = "0" + mins;
        String secs = (secondsLeft%60) + "";
        if(secs.length() == 1) secs = "0" + secs;
        return mins+":"+secs;
    }

    public void startTimer(){
        if(!timerON){
            totalStudyingSeconds = studySeekBar.getProgress() * 60;
            totalBreakSeconds = breakSeekBar.getProgress() * 60;
            secondsLeft = totalStudyingSeconds;
            progressStatus = 0;
            timerON = !timerON;
            breakTime = false;
            startThread(); //t.start();
            startTimer.setText("STOP");
        } else {
            timerON = !timerON;
            startTimer.setText("START");
        }
    }

    void startThread() {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (timerON) {
                    //while (progressStatus <= 100) {
                    while (secondsLeft >= 0) {
                        if (!timerON) break; //to exit when stop pressed
                        progressStatus = 100 - secondsLeft * 100 / ((breakTime) ? totalBreakSeconds : totalStudyingSeconds);
                        updateProgressBar();
                        try {Thread.sleep(1000);}
                        catch (InterruptedException e) {}
                        secondsLeft--;
                    }
                    breakTime = !breakTime;
                    secondsLeft = (breakTime) ? totalBreakSeconds : totalStudyingSeconds;
                }
            }
        });
        t.start();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.startTimerBTN){
            //startTimer();
            if(!timerON) new TimerTask().execute();
            else timerON = false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gd.onTouchEvent(motionEvent);

        /*switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                //dx =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    dy = view.getY() - motionEvent.getRawY();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    //view.setX(motionEvent.getRawX());
                    //view.setY(motionEvent.getRawY());
                    view.setY(motionEvent.getRawY() + dy);
                }
                return true;
            case MotionEvent.ACTION_UP:

                return true;
            default:
                return false;
        }*/
    }

    void hide(int duration){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            float h = timesLL.getHeight();
            float y = timesLL.getY();
            ObjectAnimator oa = ObjectAnimator.ofFloat(timesLL, "y", y + (h-30));
            oa.setDuration(duration);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.start();
            flingUP = false;
            shown = false;
        }

        /*TranslateAnimation ta = new TranslateAnimation(0,0,0,h-20);
        ta.setDuration(1000);
        ta.setInterpolator(new DecelerateInterpolator());
        ta.setFillAfter(true);
        timesLL.startAnimation(ta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) timesLL.setY(timesLL.getY()+(h-20));
        //timesLL.setVisibility(View.INVISIBLE);*/
    }

    void show(int duration){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            float h = timesLL.getHeight();
            float y = timesLL.getY();
            ObjectAnimator oa = ObjectAnimator.ofFloat(timesLL, "y", y - (h-30));
            oa.setDuration(duration);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.start();
            flingUP = true;
            shown = true;
        }

        /*float h = timesLL.getHeight();
        TranslateAnimation ta = new TranslateAnimation(0,0,0,-(h-20));
        ta.setDuration(1000);
        ta.setInterpolator(new DecelerateInterpolator());
        ta.setFillAfter(true);
        timesLL.startAnimation(ta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) timesLL.setY(timesLL.getY()-(h-20));
        //timesLL.setVisibility(View.VISIBLE);*/
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.e("onDown", motionEvent.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Log.e("onScroll", motionEvent.toString()+"\n"+motionEvent1.toString());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Log.e("onFling", motionEvent.toString()+"\n"+motionEvent1.toString());
        if (motionEvent1.getY() > motionEvent.getY() && flingUP) hide(333);
        else if(motionEvent1.getY() < motionEvent.getY() && !flingUP) show(333);
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if(seekBar.getId() == R.id.study_seekbar) {
            studyTimeTV.setText(progress + " min");
        } else if(seekBar.getId() == R.id.break_seekbar) {
            breakTimeTV.setText(progress + " min");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}



    class TimerTask extends AsyncTask<Void,Boolean,Void>{

        int totalStudyingSeconds, totalBreakSeconds, secondsLeft;
        boolean isBreak;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timerON = true;
            isBreak = false;
            totalStudyingSeconds = studySeekBar.getProgress() * 60;
            totalBreakSeconds = breakSeekBar.getProgress() * 60;
            secondsLeft = totalStudyingSeconds;
            pb.setMax(totalStudyingSeconds);
            //updateProgressBar();
            startTimer.setBackgroundResource(R.drawable.stop_bg);
            startTimer.setText("STOP");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (timerON) {
                if(secondsLeft >= 0) {
                    publishProgress();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                    secondsLeft--;
                } else if(!isBreak) {
                    isBreak = true;
                    secondsLeft = totalBreakSeconds;
                    publishProgress(true);
                } else {
                    isBreak = false;
                    secondsLeft = totalStudyingSeconds;
                    publishProgress(false);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... setStudy) {
            super.onProgressUpdate(setStudy);
            if (setStudy.length == 0) {
                updateProgressBar();
            } else if (setStudy[0]) {
                pb.setMax(totalStudyingSeconds);
            } else {
                pb.setMax(totalBreakSeconds);
            }
        }

        void updateProgressBar(){
            pb.setProgress(secondsLeft);
            String progressBarTitle = ((isBreak) ? "Break" : "Studying") + " "  + getTimeLeft();
            pb.setText(progressBarTitle);
        }

        String getTimeLeft(){
            String mins = (secondsLeft/60) + "";
            if(mins.length() == 1) mins = "0" + mins;
            String secs = (secondsLeft%60) + "";
            if(secs.length() == 1) secs = "0" + secs;
            return mins+":"+secs;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startTimer.setBackgroundResource(R.drawable.start_bg);
            startTimer.setText("START");
        }
    }

}