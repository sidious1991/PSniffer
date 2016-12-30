package it.agostinisalome.sicurezza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private long initial_X_coord=0;
    private Switch startSwitch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> currentList;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);
        startSwitch = (Switch) findViewById(R.id.filterFile);
        startSwitch.setChecked(false);
        startSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    list = (ListView) findViewById(R.id.listView);
                    currentList = new ArrayList<String>();
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, currentList){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            /*YOUR CHOICE OF COLOR*/

                            textView.setTextColor(Color.BLUE);

                            return view;
                        }
                    };

                    /*SET THE ADAPTER TO LISTVIEW*/
                    //list.setListAdapter(adapter);
                    list.setAdapter(adapter);

                    viewFile();
                } else {
                    stopServ();
                }

            }
        });//end switch

        File dirFileObj = new File("mnt/sdcard/sicurezzaReg");
        File[] files = dirFileObj.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for(int i =0 ; i< files.length; i++) {
            String[] temp = files[i].toString().split("/");
            fileNames.add(temp[3]);
            Log.w("file i", temp[3]);
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, fileNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);
    }
    public void viewFile(){
        //LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("filterMessage"));
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String txt = spinner.getSelectedItem().toString();
        Log.w("txt Spinner",txt);
        Intent i=new Intent(this,FilterService.class);
        EditText filter = (EditText) findViewById(R.id.FilterValue);
        i.putExtra("filter",filter.getText().toString());
        i.putExtra("filePath","mnt/sdcard/sicurezzaReg/"+txt);
        startService(i);
    }
    public void stopServ(){
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        Toast.makeText(this, "close view service!", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,FilterService.class);
        stopService(i);

    }
    private BroadcastReceiver bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whaterver you want your activity to do with the intent received
            Log.w("receive","rec");
            if(intent.getStringExtra("head")!= null) {
                Log.w("head", intent.getStringExtra("head"));
                Log.w("pkt", intent.getStringExtra("pkt"));
                currentList.add("HEADER: "+intent.getStringExtra("head"));
                currentList.add("BODY:   "+intent.getStringExtra("pkt"));
                adapter.notifyDataSetChanged();
                // next thing you have to do is check if your adapter has changed
            }
        }
    };

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("filterMessage"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                initial_X_coord = Math.round(eventX);
                break;
            case MotionEvent.ACTION_UP:
                long positionDelta = Math.round(eventX) - initial_X_coord;

                if (positionDelta > 400){
                    Intent i=new Intent(this,MainActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(this, "Slide left for change view ! >>>>", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }
}
