package it.agostinisalome.sicurezza;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegisterService extends IntentService {

    public RegisterService() {
        super("RegisterService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        DataOutputStream dos = null;
        DataInputStream dis = null;
        DataInputStream des = null;
        Process process = null;

        boolean errorStream=false;
        File folder = new File("mnt/sdcard/sicurezzaReg");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String[] commands = new String[2];
        commands[0] = "su";
        Log.w("flag",intent.getStringExtra("flag")+"");

        if(intent.getStringExtra("flag")!= null && intent.getStringExtra("flag").equals("hex"))
            commands[1] = "tcpdump -i wlan0 -XX -tttt > mnt/sdcard/sicurezzaReg/"+intent.getStringExtra("path");
        else
            commands[1] = "tcpdump -i wlan0 -A -tttt > mnt/sdcard/sicurezzaReg/"+intent.getStringExtra("path");

        try {
            process = Runtime.getRuntime().exec("su -c sh");
            // process = Runtime.getRuntime().exec(commands[0]);
            //process = Runtime.getRuntime().exec(commands[1]);
        } catch (IOException io) {
            Log.e("TestApp:Exception:", io.toString());
        }
        dos = new DataOutputStream(process.getOutputStream());
        dis = new DataInputStream(process.getInputStream());
        if(errorStream)
            des = new DataInputStream(process.getErrorStream());
        try {
            String result = "";
            for (String single : commands) {
                dos.writeBytes(single + "\n");
                dos.flush();
            }
            dos.writeBytes("exit\n");
            dos.flush();
//process.waitFor();
            Log.d("done","Commands");
            while(dis.available() > 0){
                result += dis.readLine() + "\n";
                Log.d("output",dis.readUTF());
            }
            if(errorStream)
            {
                while(des.available() > 0)
                    result += des.readLine() + "\n";
            }
            dis.close();
            dos.close();
            if(errorStream)
                des.close();
            // return result;
        } catch (Exception e) {
            Log.e ("Error",e.getMessage());
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        DataOutputStream dos = null;
        Process process = null;

        boolean errorStream=false;

        String[] commands = new String[2];
        commands[0] = "su";
        commands[1] = "pkill tcpdump";

        try {
            process = Runtime.getRuntime().exec("su -c sh");
        } catch (IOException io) {
            Log.e("TestApp:Exception:", io.toString());
        }
        dos = new DataOutputStream(process.getOutputStream());

        try {
            String result = "";
            for (String single : commands) {
                dos.writeBytes(single + "\n");
                dos.flush();
            }
            dos.writeBytes("exit\n");
            dos.flush();
//process.waitFor();
            Log.d("done","Commands");

            dos.close();

        } catch (Exception e) {
            Log.e ("Error",e.getMessage());
        }
        this.stopSelf();
    }
}
