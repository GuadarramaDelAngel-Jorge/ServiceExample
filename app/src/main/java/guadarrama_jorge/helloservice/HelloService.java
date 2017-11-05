package guadarrama_jorge.helloservice;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class HelloService extends Service {

    private static final String TAG = "HelloService";
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this);
    private boolean isRunning  = false;


    int id = 1;
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i(TAG, "Service onStartCommand");
        mBuilder.setSmallIcon(R.drawable.ic_stat_airline_seat_flat);
        mBuilder.setContentTitle("Notification");
        mBuilder.setContentText("Downloading...");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                int incr;
                for(incr=0;incr<=100;incr+=5)
                {
                    mBuilder.setProgress(100,incr,false);
                    mNotifyManager.notify(id, mBuilder.build());
                    // Sleeps the thread, simulating an operation
                    // that takes time

                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "sleep failure");
                    }


                }
                mBuilder.setContentText("Download complete")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(id, mBuilder.build());

                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.

                DownloaderTask downloaderTask = new DownloaderTask();
                downloaderTask.execute();
                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();


    }
    private class DownloaderTask extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            Log.i("DEBUG", "Executing pre execute");
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Log.i("DEBUG", "Doing background work");
            String imageURL = "http://i2.wp.com/shoryuken.com/wp-content/uploads/2015/06/ssb4-wiiu-ryu-srk-cast.png";

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            Log.i("DEBUG", "Executing post execute");
        }

    }

}