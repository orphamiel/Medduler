package com.orphamiel.medduler;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class YoutubeService extends Service 
{
	
	@Override
	public void onCreate()
   	{
   		super.onCreate();
   	}
	
	
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId)
	{ 
		  final String extras; 
		  extras = intent.getExtras().getString("extraData"); 
		  youtubeasync task = new youtubeasync();
		  task.execute(new String[] {extras});
		  Intent i = new Intent(this, MainActivity.class);
		  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		  PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
		  NotificationCompat.Builder builder = 
	    		  	new NotificationCompat.Builder(YoutubeService.this)  
		            .setSmallIcon(R.drawable.search)  
		            .setContentTitle("Developer's Easter Egg")  
		            .setContentText("Congratulations on finding it")
		            .setWhen(0)
		            .setContentIntent(pi)
		            .setTicker("Easter Egg Found!");
		  Notification note = builder.build();
	      note.flags|=Notification.FLAG_NO_CLEAR;
		  startForeground(113, note);
		  return Service.START_NOT_STICKY;
	}

	public class youtubeasync extends AsyncTask<String, Void, Integer>
    {   	
     	protected void onPreExecute() 
     	{

     	}
 		    
 		@Override
 		protected Integer doInBackground(String... youtubelink) 
 		{  			
			MediaPlayer mp = new MediaPlayer();
			try
			{
					mp.setDataSource(youtubelink[0]);
					mp.prepare();
					mp.setOnCompletionListener(new OnCompletionListener() 
					{

			            @Override
			            public void onCompletion(MediaPlayer mp) 
			            {
			                onDestroyed();
			                mp.stop();	
			                mp.release();
			            }
					});

					mp.start();
					return 0;
			}
			catch(Exception e)
			{
				return 1;
			}
 		}
 		     
 		protected void onPostExecute(final Integer result) 
 	    {
 			if (result!=0)
 			{
 				Toast.makeText(YoutubeService.this, "Could not load music", Toast.LENGTH_SHORT).show();
 				Log.e("youtubeservicecaught", "could not load youtube vid");
 				onDestroyed();
 				stopSelf();
 			}
 	    }  		
    }
    	
    	
	@Override
   	public IBinder onBind(Intent arg0) 
   	{
		return null;
   	}
   	    
   	
   	    
   	public void onDestroyed()
   	{
   	    super.onDestroy();
   	    stopForeground(true);
   	}
   	
}