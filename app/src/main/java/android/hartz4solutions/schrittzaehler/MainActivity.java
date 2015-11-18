package android.hartz4solutions.schrittzaehler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private final int SCAN_QR_CODE_ONE =1;

    private Button scan;
    private ArrayList<String> directions;
    private int startStation=-1;
    private int endStation=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = (Button) findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qrIntent = new Intent("com.google.zxing.client.android.SCAN");
                qrIntent.putExtra("SCAN_MODE","QR_CODE_MODE");
                startActivityForResult(qrIntent, SCAN_QR_CODE_ONE);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode== SCAN_QR_CODE_ONE){
            String input = intent.getStringExtra("SCAN_RESULT");
            JSONObject json;
            try {
                directions = new ArrayList<String>();
                json = new JSONObject(input);
                JSONArray jsonArray;
                try{
                    jsonArray = json.getJSONArray("input");
                } catch (Exception e){
                    jsonArray = null;
                }
                if(jsonArray!=null){
                    for(int i=0; i< jsonArray.length();i++){
                        directions.add(jsonArray.getString(i));
                    }
                    startStation = json.getInt("startStation");
                    Intent walkIntent = new Intent(this,WalkActivity.class);
                    walkIntent.putExtra("DIRECTIONS",directions);
                    startActivity(walkIntent);
                }else {
                    endStation = json.getInt("endStation");
                    if(startStation!=-1 && endStation!=-1){
                        logResult();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void logResult(){
        Intent logIntent = new Intent("ch.appquest.intent.LOG");

        if(getPackageManager().queryIntentActivities(logIntent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()){
            Toast.makeText(this,"Logbuch App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        try{
            json.put("task", "Schrittzaehler");
            json.put("startStation",startStation);
            json.put("endStation",endStation);
        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Values could not be saved", Toast.LENGTH_LONG).show();
        }
        String logMessage = json.toString();
        logIntent.putExtra("ch.appquest.logmessage",logMessage);
        startActivity(logIntent);

    }
}
