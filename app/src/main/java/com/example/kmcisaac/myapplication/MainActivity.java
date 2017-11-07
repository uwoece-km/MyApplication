package com.example.kmcisaac.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {
    private boolean redIsOn;
    private boolean redStarted;
    private boolean blueIsOn;
    private boolean blueStarted;
    private boolean greenStarted;
    private boolean greenIsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redIsOn = false;
        redStarted = false;
        blueIsOn = false;
        blueStarted = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class BlueFlash extends Thread {
        private Handler uiThread;

        public BlueFlash(Handler theHandler){
            uiThread = theHandler;
        }
        public void run(){
            while(blueStarted) {
                if (blueIsOn) {
                    // Don't do it like this:
                    /*Button blueButton = (Button) findViewById(R.id.blueButton);
                    blueButton.setBackgroundColor(Color.BLUE);*/

                    // Do it like this instead
                    uiThread.post(new Runnable() {
                        public void run() {
                            Button blueButton = (Button) findViewById(R.id.blueButton);
                            blueButton.setBackgroundColor(Color.BLUE);
                        }
                    });
                    blueIsOn = false;
                } else {
                    // Don't do it like this
                    /*Button blueButton = (Button) findViewById(R.id.blueButton);
                    blueButton.setBackgroundColor(Color.LTGRAY);*/

                    // Do it like this instead
                    uiThread.post(new Runnable() {
                        public void run() {
                            Button blueButton = (Button) findViewById(R.id.blueButton);
                            blueButton.setBackgroundColor(Color.LTGRAY);
                        }
                    });

                    blueIsOn = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }

    }

    private BlueFlash blueFlasher;
    public void onBlueClick(View view) {
        if (!blueStarted) {
            blueStarted = true;
            Handler uiThread = new Handler();
            blueFlasher = new BlueFlash(uiThread);
            blueFlasher.start();
        }
        else
        {
            blueStarted = false;
        }
    }

    public class RedFlash implements Runnable

    {
        public void run ()
        {
            Button redButton = (Button) findViewById(R.id.redButton);
            redButton.setBackgroundColor(Color.RED);
            if (redStarted)
            {
                Handler me = new Handler();
                me.postDelayed(new RedOff(),1000);
            }
        }
    }

    public class RedOff implements Runnable {
        public void run(){
            Button redButton = (Button) findViewById(R.id.redButton);
            redButton.setBackgroundColor(Color.LTGRAY);
            if (redStarted)
            {
                Handler me = new Handler();
                me.postDelayed(new RedFlash(),1000);
            }
        }
    }
    public void onRedClick(View view) {
        if (!redStarted){
            // Start a red timer
            Handler me = new Handler();
            me.postDelayed(new RedFlash(),1000);
            redStarted = true;
        }
        else
        {
            // Stop a red timer
            redStarted = false;
        }
    }

    public class GreenWorker extends AsyncTask<Void,Integer,Integer> {
        protected Integer doInBackground(Void... params){
            while(greenStarted)
            {
                try {
                    Thread.sleep(1000);
                    if (greenIsOn) {
                        publishProgress(Color.LTGRAY);
                        greenIsOn = false;
                    }
                    else
                    {
                        publishProgress(Color.GREEN);
                        greenIsOn=true;
                    }
                }
                catch(Exception e)
                {}
            }
            return 0;
        }
        protected class ColorChanger implements Runnable{
            private int newColor;
            ColorChanger(int c){newColor=c;}
            public void run(){
                Button greenButton = (Button)findViewById(R.id.greenButton);
                greenButton.setBackgroundColor(newColor);
            }
        }

        protected void onProgressUpdate(Integer... state) {
            // The boolean passed tells us if the button should be made green or not
            Handler me = new Handler();
            me.post(new ColorChanger(state[0]));
        }
        protected void onPostExecute(){
            Handler me = new Handler();
            me.post(new ColorChanger(Color.LTGRAY));
        }
    }

    public void onGreenClick(View view) {
        if (!greenStarted){
            // Begin a background worker task
            greenStarted = true;
            GreenWorker theWorker = new GreenWorker();
            theWorker.execute();
        }
        else
        {
            greenStarted = false;
        }
    }
}
