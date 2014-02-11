package com.orphamiel.medduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class updateservice extends Service 
{
	public void onCreate()
   	{
		super.onCreate();
		(((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "orphupdate")).acquire();	
   	}
	
	@Override
   	public IBinder onBind(Intent arg0) 
   	{
		return null;
   	}
   	    
   	public void onDestroyed()
   	{
   	    super.onDestroy();
   	    (((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "orphupdate")).release();
   	    stopForeground(true);
   	}
   	
   	@Override 
	public int onStartCommand(Intent intent, int flags, int startId)
	{ 
		  updateasync task = new updateasync();
		  task.execute();
		  Intent i=new Intent(this, MainActivity.class);
		  i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);	    
	      PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
	      NotificationCompat.Builder builder = 
	    		  	new NotificationCompat.Builder(updateservice.this)  
		            .setSmallIcon(R.drawable.download2)  
		            .setContentTitle("Updating appointments")  
		            .setContentText("Please wait...")
		            .setWhen(0)
		            .setContentIntent(pi)
		            .setTicker("Updating appointments");
		  startForeground(103, builder.build());
		  return Service.START_STICKY;
	}
   	
   	public class updateasync extends AsyncTask<String, Void, Integer>
    {
   		final ArrayList<String> filearr = new ArrayList<String>();
   		int ftpfiles = 0;
   		int updatedfiles = 0;
   		int failedfiles = 0;
   		Boolean hasbin = false;
   		int numoffiles = 0;
   		int errorcatch = 0;
   		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
   		Intent i=new Intent(updateservice.this, MainActivity.class);
	    PendingIntent pi=PendingIntent.getActivity(updateservice.this, 0, i, 0);
   		String updatedfilestring = "";
   		String failedfilestring = "";
   		String updatedstring = "";
   		String updatedstring2 = "";
	    
   		protected void onPreExecute() 
   		{

   		}	
   			
   		@Override
   		@SuppressWarnings("unchecked")
   		protected Integer doInBackground(String... writeinfo) 
   		{
   			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(updateservice.this);
   			String usernam = prefs.getString("usern", "medduler");
   			String passwo = prefs.getString("passw", "medpass");
   			String port = prefs.getString("portn", "");
   			try
   			{
   				HashMap<String, String> filemap;
        		FileInputStream fos = updateservice.this.openFileInput("Medduler.bin");
        		ObjectInputStream inputstream = new ObjectInputStream(fos);
        		filemap = (HashMap<String, String>) inputstream.readObject();
        		inputstream.close();
            	for (Map.Entry<String, String > entry : filemap.entrySet()) 
            	{
            		if (entry.getValue()!=null)
            		{
            			filearr.add(entry.getValue());      		
                		ftpfiles++;
            		}
            	}
            	hasbin = true;
   			}    	
   			catch (Exception e)
   			{
   				Log.v("IO Exception2", e.getMessage());
        		hasbin = false;
   			}
   			if(hasbin== true)
   			{
   				if (ftpfiles>0)
   				{
   					int portnum = 0;
   					try
   					{
   						portnum = Integer.parseInt(port);
   					}
   					catch(NumberFormatException nfe)
   					{
   						Log.e("updateservicecaught", "could not parse portnum from shared");
   						portnum = 21;
   					}
   					if (portnum>65535)
   					{
   						return 3;
   					}
   					else
   					{
   						for (int i=0; i<ftpfiles; i++)
   						{
   							FTPClient ftpClient = new FTPClient();
   							String filelink = filearr.get(i);
   							String [] linkarr, linkarr2, linkarr3;
   							linkarr = filelink.toString().trim().split("//");
   							linkarr2 = linkarr[1].toString().trim().split("/", 2);
   							String hostname = linkarr2[0];
   							linkarr3 = linkarr2[1].toString().trim().split(".medduler", 2);
   							linkarr3 = linkarr3[0].toString().trim().split("/");
   							try
   							{
   								ftpClient.connect(InetAddress.getByName(hostname), portnum);
   								Boolean logina = ftpClient.login(usernam, passwo);
   								if (logina==true)
   								{
   									File meddulerdir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");			
   									meddulerdir.mkdirs();
   									ftpClient.enterLocalPassiveMode();
   									ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 
   									Boolean checkexist = false;
   									FTPFile [] a = ftpClient.listFiles("/"+linkarr2[1]); 	     	       					 
   									for (int z=0; z<a.length; z++)
   									{
   										if((linkarr3[linkarr3.length-1]+".medduler").equals(a[z].getName()))
   										{
   											checkexist=true; 
   										}
   									}
   									if(checkexist==true)
   									{
   										FileOutputStream desFileStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Medduler/" + linkarr3[linkarr3.length-1] +".medduler");
   										ftpClient.retrieveFile("/"+linkarr2[1], desFileStream);	     	       	     	   			     	       
   										ftpClient.logout();
   										ftpClient.disconnect();
   										//Update calendar by getting the hashmap from storage and then searching
   										//for a matched id
   										long calid = 0;
   										try
   							  			{
   							  				HashMap<String, Long> calmap;
   							  				FileInputStream fos = openFileInput("calmap.bin");
   							  				ObjectInputStream inputstream = new ObjectInputStream(fos);
   							  				calmap = (HashMap<String, Long>) inputstream.readObject();
   							  				inputstream.close();
   							  				calid = calmap.get(linkarr3[linkarr3.length-1] +".medduler");
   							  				
   							  			}    	
   							  			catch (Exception e)
   							  			{
   							  				
   							  			}
   										if(calid!=0)
   										{
   											Boolean filecheck = true;
   											final StringBuilder text = new StringBuilder();
   								        	final File ftpfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + linkarr3[linkarr3.length-1] +".medduler");
   								        	try 
   								        	{
   								        		BufferedReader br = new BufferedReader(new FileReader(ftpfile));
   								        		String line;

   								        		while ((line = br.readLine()) != null) 
   								        		{
   								        			text.append(line);
   								        		}
   								        		br.close();
   								        	}
   								        	catch (IOException e) 
   								        	{
   								        		filecheck=false;
   								        	}
   								        	if(filecheck==true)
   								        	{
   								        		String [] arr;
   								            	arr = text.toString().trim().split("#&%");
   								            	if (arr.length == 11)
   								            	{
   								            		String locatio = arr[4];
   									            	String date1 = arr[6];
   									            	String time1 = arr[7];
   									            	String issue1 = arr[9];
   									            	String docname = arr[10];
   									            	//This new array is to split the date then convert to int
   									            	String [] arr3;
   									            	arr3 = date1.toString().trim().split("-");
   									            	int year1 = stringparse(arr3[2], "year");
   									            	int month1 = stringparse(arr3[1], "month");
   									            	int day1 = stringparse(arr3[0], "day");
   									            	//convert time
   									            	SimpleDateFormat fulltimeFormat = new SimpleDateFormat("h:mm aa", Locale.US);
   									            	Date appttime = new Date();
   									            	try
   													{
   														appttime = fulltimeFormat.parse(time1);
   													} 
   									            	catch (ParseException e)
   													{
   														Log.e("createcaughtmake", "could not parse time "+ time1, e);
   													}
   									            	Calendar timetotal = new GregorianCalendar();
   									            	timetotal.setTime(appttime);
   									            	int hour1 = timetotal.get(Calendar.HOUR_OF_DAY);
   									            	int minute1 = timetotal.get(Calendar.MINUTE);
   									            	//End of time conversion - note : month-1 because 0 = jan and 11=dec
   									            	GregorianCalendar calDate = new GregorianCalendar(year1, month1-1, day1, hour1, minute1);
   								            		String caltitle = date1 + " appointment";
   								            		String caldesc = issue1 + " appointment with " + docname + " at " + locatio + " on " + date1 + " at " + time1;
   								            		final ContentValues updevent = new ContentValues();
   	   	   	   								    	updevent.put("title", caltitle);
   	   	   	   								    	updevent.put("description", caldesc);
   	   	   	   								    	updevent.put("eventlocation", locatio);
   	   	   	   								    	updevent.put("dtstart", calDate.getTimeInMillis());
   	   	   	   								    	String durac = prefs.getString("dura", "");
   	   	   	   								    	int durat = 0;
   	   	   	   								    	try
   	   	   	   								    	{
   	   	   	   								    		durat = Integer.parseInt(durac);
   	   	   	   								    	}
   	   	   	   								    	catch(NumberFormatException nfe)
   	   	   	   								    	{
   	   	   	   								    		durat = 60;
   	   	   	   								    	}
   	   	   	   								    	durat *= 60*1000;
   	   	   	   								    	updevent.put("dtend", calDate.getTimeInMillis()+durat);
   	   	   	   								    	Uri updateuri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calid);
   	   	   	   								    	getContentResolver().update(updateuri, updevent, null, null);
   								            	}
   								        	}
   										}
   										//end of updating calendar
   										updatedfilestring += "\n" + linkarr3[linkarr3.length-1] +".medduler";
   										updatedfiles++;
   									}
   									else
   									{
   										errorcatch=7;
   										failedfilestring += "\n" + linkarr3[linkarr3.length-1] +".medduler";
   										failedfiles++;
   									}	 
   								}
   								else
   								{
   									Log.e("updateservicecaught", "couldn't login due to wrong pw or id");
   									errorcatch=1;
   								}
   							}
   							catch (SocketException e) 
   							{
   								Log.e("updateservicecaught", "Socket Exception", e);
   								errorcatch = 3;
   								failedfiles++;
   							} 
   							catch (UnknownHostException e) 
   							{
   								Log.e("updateservicecaught", "Host Unknown", e);
   								errorcatch = 4;
   								failedfiles++;
   							} 
   							catch (IOException e) 
   							{
   								Log.e("updateservicecaght", "Failed to retrieve a file" + (linkarr3[linkarr3.length-1]+".medduler"), e);
   								errorcatch = 5;
   								failedfiles++;
   							}
   						}
   					}
   					if (updatedfiles>0)
   					{
   						return 0;
   					}
   					else
   					{
   						if (errorcatch == 0)
   						{
   							return 0;
   						}
   						else if (errorcatch ==1)
   						{
   							return 2;
   						}
   						else if (errorcatch ==3)
   						{
   							return 4;
   						}
   						else if (errorcatch ==4)
   						{
   							return 5;
   						}
   						else if (errorcatch ==5)
   						{
   							return 6;
   						}
   						else if (errorcatch == 7)
   						{
   							return 7;
   						}
   						else
   						{
   							return 99;
   						}
   					}
   				}
   				else
   				{
   					return 1;
   				}  
   			}
   			else
   			{
   				return 1;
   			}
   		}
   		
   		public int stringparse(String inpstring, String debugcode)
   	    {
   	    	int convertedint = 0;
   	    	try 
   	    	{
   	    	    convertedint = Integer.parseInt(inpstring);
   	    	} 
   	    	catch(NumberFormatException nfe) 
   	    	{
   	    		
   	    	}
   	    	return convertedint;
   	    }
   	 
   		@TargetApi(16)
   		protected void onPostExecute(final Integer result) 
   		{
   			if (result==0)
   			{
   				if (ftpfiles==updatedfiles)
   				{  					
   					if(updatedfiles==1)
   					{
   						updatedstring = "1 file was updated! Please refresh the list.";
   						updatedstring2 = updatedstring + "\nUpdated File :" + updatedfilestring;
   					}
   					else
   					{
   						updatedstring = Integer.toString(updatedfiles) + " files were updated! Please refresh the list.";
   						updatedstring2 = updatedstring + "\nUpdated Files :" + updatedfilestring;
   					}
   					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
   					{
   						Notification noti = new Notification.BigTextStyle(
   								new Notification.Builder(updateservice.this)
   								.setContentTitle("Update successful")
   								.setContentText(updatedstring)
   								.setSmallIcon(R.drawable.download2)
   								.setWhen(0)
   								.setContentIntent(pi)
   								.setTicker("Update successful"))
   							.bigText(updatedstring2)
   							.build();
   						notificationManager.notify(102, noti);
   					}
   					else
   					{
   						NotificationCompat.Builder builder =  
   								new NotificationCompat.Builder(updateservice.this)
   								.setSmallIcon(R.drawable.download2)  
   								.setContentTitle("Update successful")  
   								.setContentText(updatedstring)
   								.setContentIntent(pi)
   								.setTicker("Update successful")
   								.setWhen(0);
   						notificationManager.notify(102, builder.build());
   					}
   				}
   				else
   				{
   					if(updatedfiles==1)
   					{
   						updatedstring = Integer.toString(updatedfiles) + " file was updated! Failed to update " + Integer.toString(failedfiles) + " file(s). Please refresh the list.";
   						updatedstring2 = updatedstring + "\nUpdated File :" + updatedfilestring + "\nFailed to update :" + failedfilestring;
   					}
   					else
   					{
   						updatedstring = Integer.toString(updatedfiles) + " files were updated! Failed to update " + Integer.toString(failedfiles) + " file(s). Please refresh the list.";
   						updatedstring2 = updatedstring + "\nUpdated Files :" + updatedfilestring + "\nFailed to update :" + failedfilestring;
   					}
   					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
   					{
   						Notification noti = new Notification.BigTextStyle(
   								new Notification.Builder(updateservice.this)
   								.setContentTitle("Update partially successful")
   								.setContentText(updatedstring)
   								.setSmallIcon(R.drawable.download2)
   								.setContentIntent(pi)
   								.setWhen(0)
   								.setTicker("Update partially successful"))
   							.bigText(updatedstring2)
   							.build();
   						notificationManager.notify(102, noti);
   					}
   					else
   					{
   						NotificationCompat.Builder builder =  
   								new NotificationCompat.Builder(updateservice.this)
   								.setSmallIcon(R.drawable.download2)  
   								.setContentTitle("Update partially successful")  
   								.setContentText(updatedstring)
   								.setContentIntent(pi)
   								.setTicker("Update partially successful")
   								.setWhen(0);
   						notificationManager.notify(102, builder.build());
   					}
   				}
   			}
   			else if(result==1)
   			{
   				NotificationCompat.Builder builder =  
				            new NotificationCompat.Builder(updateservice.this)  
				            .setSmallIcon(R.drawable.download2)  
				            .setContentTitle("Update successful")  
				            .setContentText("There were no files to update!")
				            .setContentIntent(pi)
				            .setTicker("Update successful")
				            .setWhen(0);
   				notificationManager.notify(102, builder.build());
   			}
   			else if (result==2)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("Could not connect to server because of wrong password or id")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else if (result==3)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("Could not connect to server because of a bad port number.")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else if (result==4)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("Could not connect to server because the port is blocked or invalid.")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else if (result==5)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("The server could not be found.")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else if (result==6)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("The connection was terminated while downloading.")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else if (result==7)
   			{
   				NotificationCompat.Builder builder =  
			            new NotificationCompat.Builder(updateservice.this)  
			            .setSmallIcon(R.drawable.download2)  
			            .setContentTitle("Update unsuccessful")  
			            .setContentText("The file could not be found on the server.")
			            .setContentIntent(pi)
			            .setTicker("Update unsuccessful")
			            .setWhen(0);
				notificationManager.notify(102, builder.build());
   			}
   			else
   			{
   				Log.e("updateservicecaught", "no idea where this error is from");
   			}
			stopSelf();
   		}
    }  	
}