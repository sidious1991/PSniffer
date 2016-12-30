package it.agostinisalome.sicurezza;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static android.R.attr.id;
import static it.agostinisalome.sicurezza.R.id.editText;

public class MainActivity extends AppCompatActivity {

    private long initial_X_coord=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void start_click(View v){
        Toast.makeText(this, "Ciao a tutti!", Toast.LENGTH_SHORT).show();

        EditText path = (EditText) findViewById(R.id.editText);
        CheckBox checkBox = (CheckBox) findViewById(R.id.hexChk);
        if(!path.getText().toString().isEmpty()) {
            Intent i=new Intent(this,RegisterService.class);
            i.putExtra("path", path.getText() + ".txt");
            if (checkBox.isChecked())
                i.putExtra("flag", "hex");
            startService(i);
        }
    }
    public void stop_click(View v){
        Toast.makeText(this, "Addio!", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,RegisterService.class);
        stopService(i);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();

        EditText path = (EditText) findViewById(R.id.editText);
        Log.d("text",path.getText().toString());
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                initial_X_coord = Math.round(eventX);
                break;
            case MotionEvent.ACTION_UP:
                long positionDelta = Math.round(eventX) - initial_X_coord;

                if (positionDelta < -400){
                    Intent i=new Intent(this,SecondActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(this, "<<<<  Slide left for change view !", Toast.LENGTH_SHORT).show();
                }

                return true;
        }
        return false;
    }
}
