package com.project.hackathon.motorola.bluetoothexample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity {


    LineGraphSeries<DataPoint> mSeries ;
    GraphView mBeeGraph;
    private Handler  mGraphHandler;
    private Runnable mGraphTimer;
    private Viewport mVp;
    private int xpos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeeGraph = (GraphView)findViewById(R.id.graph);
        mGraphHandler = new Handler();

        mSeries =  new LineGraphSeries<>();
        mSeries.setTitle("Bee Hissing Level");
        mSeries.setBackgroundColor(Color.GRAY);

        mBeeGraph.addSeries(mSeries);

        mVp= mBeeGraph.getViewport();
        mVp.setXAxisBoundsManual(true);
        mVp.setMinX(0);
        mVp.setMaxX(1000);
        mVp.setMinY(-2);
        mVp.setMaxY(2);


        xpos = 0;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {
        super.onResume();

        mGraphTimer = new Runnable() {
            @Override
            public void run() {

                xpos++;
        //        mSeries.appendData(new DataPoint(xpos, bleActivity.aggroLevel), true, 1000);
                mGraphHandler.postDelayed(this, 100);
            }
        };

        mGraphHandler.postDelayed(mGraphTimer, 100);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}