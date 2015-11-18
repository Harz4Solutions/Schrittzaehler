package android.hartz4solutions.schrittzaehler;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 11.11.15.
 */
public class WalkActivity extends AppCompatActivity implements StepListener{

    private final int SCAN_QR_CODE_ONE =1;

    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private StepCounter stepCounter;
    private ArrayList<String> directions;
    private Button startAndPause;
    private TextView textView;
    private Speaker speaker;
    private int directionCounter=0;
    private int schritte;
    private boolean canWalk = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        stepCounter = new StepCounter(this);
        sensorManager.registerListener(stepCounter, sensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);

        textView = (TextView) findViewById(R.id.textView);

        speaker = new Speaker(getApplicationContext());

        Intent intent = getIntent();
        directions = intent.getStringArrayListExtra("DIRECTIONS");
        startAndPause = (Button) findViewById(R.id.startAndPause);
        startAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(directionCounter<directions.size()){
                    startAndPause.setText("Bereit?");
                    startAndPause.setEnabled(false);
                    String aktion = directions.get(directionCounter);
                    if(aktion.matches("\\d+")){
                        schritte = Integer.parseInt(aktion);
                        textView.setText(schritte + " Schritte nach vorne machen");
                        speaker.speak(schritte + " Schritte nach vorne machen");
                        canWalk = true;
                    }
                    else{
                        textView.setText("Nach " + aktion + " drehen");
                        speaker.speak("Nach " + aktion + " drehen");
                        startAndPause.setEnabled(true);

                    }
                    directionCounter++;
                } else {
                    textView.setText("Endstation, QR-Code einscannen");
                    speaker.speak("Endstation, QR-Code einscannen");
                    finish();

                }



            }
        });
        Button lazywalk = (Button) findViewById(R.id.walk);
        lazywalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStep();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(stepCounter, sensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(stepCounter);
    }

    @Override
    public void onStep() {
        if(canWalk){
            if(schritte!= 1){
                textView.setText(--schritte + " Schritte nach vorne machen");
            }else {
                canWalk=false;
                speaker.speak("Halt Stop!");
                textView.setText("Halt Stop!");
                startAndPause.setEnabled(true);
            }
        }
    }
}
