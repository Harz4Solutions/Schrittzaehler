package android.hartz4solutions.schrittzaehler;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Button startAndPaus;
    private Button confirm;
    private TextView textView;
    private Speaker speaker;
    private boolean isRunning = false;
    private int position = 0;
    private int stepCount = 0;
    private int expectedSteps;


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
        startAndPaus = (Button) findViewById(R.id.startAndPause);
        startAndPaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning){
                    startAndPaus.setText("Pause");
                    confirm.setActivated(true);
                    if(position%2==0){
                        textView.setText("Drehe dich nach "+directions.get(position));
                        speaker.speak("Drehe dich nach "+directions.get(position));

                    }
                    else {
                        textView.setText("Drehe "+directions.get(position));
                        speaker.speak("Drehe "+directions.get(position));
                        confirm.setActivated(false);
                        startAndPaus.setText("Start");
                    }

                }else{
                    startAndPaus.setText("Start");
                    confirm.setActivated(false);
                }
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
        stepCount++;
    }
}
