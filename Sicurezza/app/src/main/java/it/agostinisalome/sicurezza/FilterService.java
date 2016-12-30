package it.agostinisalome.sicurezza;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by stefano on 27/12/16.
 */

/** This class reads a selected file and filters all packets by keyword,
 *  then creates a list of filtered packets
 */

public class FilterService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */

    String filter;
    String filePath;
    ArrayList<String> pack;

    public FilterService() {
        super("FilterService");
    }

    @Override
    public void onHandleIntent(Intent intent) {


    this.filter = intent.getStringExtra("filter");
    this.filePath = intent.getStringExtra("filePath"); // Absolute
    File input = new File(this.filePath);

    try {
        //Read the sniffed packets file
        BufferedReader br = new BufferedReader(new FileReader(input));
        String line;
        this.pack = new ArrayList();

        String ip = "IP";
        String arp = "ARP";

        String header = "";
        String body = "";

        while ((line = br.readLine()) != null) {

            //Current Header
            if(line.contains(ip) || line.contains(arp)){

                if(!header.isEmpty() && (header.contains(filter) || body.contains(filter))) {
                    Log.v("HEADER:\n\n", header);
                    Log.v("BODY:\n\n", body);
                    this.pack.add("HEADER:\n\n" + header + "\n\n");
                    this.pack.add("BODY:\n\n" + body + "\n\n");
                    sendBroadcast(body,header);

                }

                body = "";
                header = line;
            }

            //Current Body
            else {
                body = body.concat(line);
            }

        }

        if(header.contains(filter) || body.contains(filter)){
            this.pack.add("HEADER:\n\n" + header + "\n\n");
            this.pack.add("BODY:\n\n" + body + "\n\n");
            sendBroadcast(body,header);
        }

      /*  for(int i=0 ; i< pack.size(); i+=2){
            sendBroadcast(pack.get(i),pack.get(i+1));
        }*/
        br.close();
    }

    catch (IOException e) {
        //To handle...
    }

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "close filter service!", Toast.LENGTH_SHORT).show();
        this.stopSelf();
    }

    private void sendBroadcast (String pkt,String head){
        Intent intent = new Intent ("filterMessage"); //put the same message as in the filter you used in the activity when registering the receiver
        intent.putExtra("pkt", pkt);
        intent.putExtra("head", head);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}