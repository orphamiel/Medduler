package com.orphamiel.medduler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class createactivity extends FragmentActivity
{
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.createfrag);
		Date current = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy", Locale.US);
        //change this back to mm instead of 00 maybe?
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:00 aa", Locale.US);
        String datecurr = dateFormat.format(current);
        String timecurr = timeFormat.format(current);
        Button mEdit = (Button)findViewById(R.id.datebutt);
        Button mEdit2 = (Button)findViewById(R.id.timebutt);
        mEdit.setText(datecurr);
        mEdit2.setText(timecurr);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String fcname = prefs.getString("fcname", "");
        String lcname = prefs.getString("lcname", "");
        ((EditText)findViewById(R.id.nameedit)).setText(fcname);
        ((EditText)findViewById(R.id.nameedit2)).setText(lcname);        
	}
	
	/**
	 * Called when cancel is pressed
	 */
	public void quit(View view)
	{
		finish();
	}
	
	public void selectDate(View view) 
    {
    	//so much code to prevent double clicking from opening two dialogs
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("DatePicker");
        if (prev != null) 
        {
            ft.remove(prev);
            
        }
        DialogFragment newFragment = new SelectDateFragment();
    	ft.add(newFragment, "DatePicker");
        ft.addToBackStack(null);
    	ft.commit();
    }
    
    public void selectTime(View view)
    {
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("TimePicker");
        if (prev != null) 
        {
            ft.remove(prev);            
        }
        DialogFragment newFragment = new TimePickerFragment();
    	ft.add(newFragment, "TimePicker");
        ft.addToBackStack(null);
    	ft.commit();
    }
    
    public void onActivityResult(int reqCode, int resultCode, Intent data) 
    {
    	if (reqCode == 2) 
	    {
    		if (resultCode == Activity.RESULT_OK) 
    		{
    			EditText docna = (EditText)findViewById(R.id.nameedit3);
    			EditText isedit = (EditText)findViewById(R.id.issuedit);
    			String name = "";
    			Cursor cursor =  getContentResolver().query(data.getData(), null, null, null, null); 
    			while (cursor.moveToNext()) 
    			{         
    				name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
    			}
    			cursor.close();
    			docna.setText(name);
    			isedit.requestFocus();
    			isedit.setSelection(isedit.length()); 
    		}
	    }
    }
    
    public void makemedduler(View view)
    {
    	Boolean errortest = false;
    	//make sure the handling doesnt get errors later
    	Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
        {
        	DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
        //declare views
    	String first = ((EditText)findViewById(R.id.nameedit)).getText().toString().trim();
    	String lastn = ((EditText)findViewById(R.id.nameedit2)).getText().toString().trim();
    	String docna = ((EditText)findViewById(R.id.nameedit3)).getText().toString().trim();
    	String depar = ((EditText)findViewById(R.id.depedit)).getText().toString().trim();
    	String issur = ((EditText)findViewById(R.id.issuedit)).getText().toString().trim();
    	String locar = ((EditText)findViewById(R.id.locedit)).getText().toString().trim();
    	String remar = ((EditText)findViewById(R.id.reedit)).getText().toString().trim();
    	String datestring = ((Button)findViewById(R.id.datebutt)).getText().toString();
    	String timestring = ((Button)findViewById(R.id.timebutt)).getText().toString();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy", Locale.US);
    	SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa", Locale.US);
		Date convertedDate = new Date();
		String datecurr = "";
    	try 
		{
    		convertedDate = dateFormat.parse(datestring);
		} 
    	catch (ParseException e) 
    	{
    		errortest=true;
    	}
    	Date convertedDate2 = new Date();
    	String timecurr = "";
    	try
    	{
    		convertedDate2 = timeFormat.parse(timestring);
    	}
    	catch(ParseException e)
    	{
    		errortest=true;
    	}    	
    	int errorcheck = 0;
    	errorcheck += checkforinvalid(first);
    	errorcheck += checkforinvalid(lastn);
    	errorcheck += checkforinvalid(docna);
    	errorcheck += checkforinvalid(depar);
    	errorcheck += checkforinvalid(issur);
    	errorcheck += checkforinvalid(locar);
    	errorcheck += checkforinvalid(remar);
    	if(errorcheck==7)
    	{
    		//no errors
        	if (errortest==false)
        	{
        		//add to shared pref the names
        		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        		prefs.edit().putString("fcname", first).commit();
        		prefs.edit().putString("lcname", lastn).commit();
        		//other stuff here
            	SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                datecurr = dateFormat2.format(convertedDate);
                SimpleDateFormat timeFormat2 = new SimpleDateFormat("h:mm aa", Locale.US);
                timecurr = timeFormat2.format(convertedDate2);
                timecurr = timecurr.toUpperCase(Locale.ENGLISH);
                Date current = new Date();
                SimpleDateFormat currentFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.US);
                String timestamp = currentFormat.format(current);
                timestamp = timestamp.toUpperCase(Locale.ENGLISH);
                SimpleDateFormat currentFormat2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String filename = currentFormat2.format(current);
            	String combined = "#&%" + first + "#&%" + lastn + "#&%" + depar + "#&%" + locar +
            					  "#&%" + remar + "#&%" + datecurr + "#&%" + timecurr
            			          + "#&%" + timestamp + "#&%" + issur + "#&%" + docna + "#&%";
            	String [] arr;
            	arr = combined.toString().trim().split("#&%");
            	//start create file
            	Boolean test1 = com.orphamiel.medduler.MainActivity.isExternalStorageWritable();
            	if (test1 == true)
    			{
            		writeoffqr2 task = new writeoffqr2();
                	task.execute(new String[] {filename, combined});
    			}
            	else
    			{
    				DialogFragment newFragment = new singledialog();
    		    	Bundle args = new Bundle();
    		    	args.putString("title", "Unable to save Appointment");
    		    	args.putString("text", "Could not save Appointment to SdCard");
    		    	newFragment.setArguments(args);
    		        newFragment.show(getSupportFragmentManager(), "dialog");
    			}
            	//end of writing file
            	if (arr.length == 11)
            	{
            		//Splitting into related sections
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
	            	boolean caloption = prefs.getBoolean("calimport", true);
	            	if (caloption==true)
	            	{
	            		String caltitle = date1 + " appointment";
	            		String caldesc = issue1 + " appointment with " + docname + " at " + locatio + " on " + date1 + " at " + time1;
	            		addtocalendar(caltitle, caldesc, locatio, calDate.getTimeInMillis()); 	            		
	            	}
	            	Toast.makeText(this, "Appointment saved", Toast.LENGTH_SHORT).show();
	            	finish();
            	}
        	}
        	else
        	{
        		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Invalid date or time");
    	    	args.putString("text", "The date or time is invalid.");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
        	} 
    	}
    	else
    	{
    		if(errorcheck<7)
    		{
    			DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Error");
    	    	args.putString("text", "Empty fields are not allowed");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
    		}
    		else
    		{
    			DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Error");
    	    	args.putString("text", "The string \"#&%\" is not allowed.");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
    		}
    	}
    }
    
    public int checkforinvalid(String a)
    {
    	int f=0;
    	if (a!= null&&!a.equals("")&&!a.contains("#&%"))
    	{
    		f=1;
    		return f;
    	}
    	else
    	{
    		if (a==null||a.equals(""))
    		{
    			return f;
    		}
    		else
    		{
    			f=8;
    			return f;
    		}	
    	}
    }
   	
   	public class writeoffqr2 extends AsyncTask<String, Void, Integer>
    {
    	
    	protected void onPreExecute() 
		{

		}
    	
    	@Override
    	@SuppressWarnings("unchecked")
	    protected Integer doInBackground(String... writeinfo) 
	    {
    		try 
			{
    			File meddulerdir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");			
	     	    meddulerdir.mkdirs();
    			File file1 = new File(Environment.getExternalStorageDirectory() + "/Medduler/", writeinfo[0]+".medduler");
				FileWriter fw1 = new FileWriter(file1.getAbsoluteFile());
				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write(writeinfo[1]);
				bw1.close();
				File file = getBaseContext().getFileStreamPath("Medduler.bin");
	  	       	if(file.exists())
	  	       	{
	  	       		try
	  	       		{
		     	    	HashMap<String, String> filemap;
			     	    FileInputStream fos = getApplicationContext().openFileInput("Medduler.bin");
			     	    ObjectInputStream inputstream = new ObjectInputStream(fos);
			     	    filemap = (HashMap<String, String>) inputstream.readObject();
			     	    inputstream.close();
			     	    filemap.put(writeinfo[0]+".medduler", null);
			     	    FileOutputStream fos2 = getApplicationContext().openFileOutput("Medduler.bin", Context.MODE_PRIVATE);   
			     	    ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
			     	    outputStream.writeObject(filemap);
			     	    outputStream.flush();
			     	    outputStream.close();
		          	}
		     	    catch (Exception e)
		          	{
		     	    	Log.v("IO Exception3", e.getMessage());
		          	}
	  	       	}
	  	       	else
	  	       	{
	  	    	  try
		          {
	  	    		  HashMap<String, String> filemap = new HashMap<String, String>();
			     	  filemap.put(writeinfo[0]+".medduler", null);
			     	  FileOutputStream fos2 = getApplicationContext().openFileOutput("Medduler.bin", Context.MODE_PRIVATE);   
			     	  ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
			     	  outputStream.writeObject(filemap);
			     	  outputStream.flush();
			     	  outputStream.close();
		          }
		     	  catch (Exception e)
		          {
		     		  Log.v("IO Exception3", e.getMessage());
		          }
	  	       	}
	  	       	return 0;
			}
			catch (FileNotFoundException e) 
			{
				Log.e("createcaughtwrite", "File not found when writing " + writeinfo[0], e);
				return 1;
			} 
			catch (IOException e) 
			{
				Log.e("createcaughtwrite", "Can't write to file " + writeinfo[0], e);
				return 2;
			}    		
	    }
    	
    	 protected void onPostExecute(final Integer result) 
 	     {
 	    	if (result==0)
 	    	{

 	    	}
 	    	else if (result==1)
 	    	{
 	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "File not found");
    	    	args.putString("text", "File was deleted halfway through writing");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
 	    	}
 	    	else if (result==2)
 	    	{
 	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to write");
    	    	args.putString("text", "Could not write to the file");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
 	    	}
 	    	else
 	    	{
 	    		Log.e("createcaughtwrite", "Error at writing file in async");
 	    	}
 	    }
    }
   	
   	/**
     * Adds an event to the calendar with autodetection of API level to choose correct contentresolver
     * @see http://developer.android.com/reference/android/provider/CalendarContract.EventsColumns.html
     * @param eventtitle String to set as event title
     * @param eventdesc String to set as event description
     * @param eventloc String to set as event location
     * @param startTime Long to set as event time, will also be used to calculate end time
     */
    public void addtocalendar(String eventtitle, String eventdesc, String eventloc, long startTime)
    {
    	Uri uri = CalendarContract.Calendars.CONTENT_URI;
    	long calid = 0, medcalcount = 0;
    	Cursor result = getContentResolver().query(uri, new String[] {CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
    	while (result.moveToNext()) 
    	{
    		if(result.getString(2).equals("Medduler"))
    		{
    			calid = result.getLong(0);
    			medcalcount++;
    		}
    	}
    	result.close();    
    	if(medcalcount>=1)
    	{
    		final ContentValues event = new ContentValues();
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       	 	event.put("calendar_id", calid);
       	 	event.put("title", eventtitle);
       	 	event.put("description", eventdesc);
       	 	event.put("eventLocation", eventloc);
       	 	event.put("dtstart", startTime);
       	 	event.put("eventColor", 0xffff4444);
    		//get duration of events
    		String durac = prefs.getString("dura", "");
    		int durat = 0;
    		try
    		{
    			durat = Integer.parseInt(durac);
    		}
    		catch(NumberFormatException nfe)
    		{
    			Log.e("maincaughtresult", "could not parse duration from shared " + durac);
    			durat = 60;
    			prefs.edit().putString("dura", "60").commit();
    		}
        	durat *= 60*1000;
        	//end calculation of duration and set end time of event
       	 	event.put("dtend", startTime+durat);
       	 	event.put("allDay", 0);   // 0 for false, 1 for true
       	 	event.put("hasAlarm", 1); // 0 for false, 1 for true
       	 	//get privacy
    		String privac = prefs.getString("priva", "");
    		int priv = 0;
    		try
    		{
    			priv = Integer.parseInt(privac);
    		}
    		catch(NumberFormatException nfe)
    		{
    			Log.e("maincaughtresult", "could not parse privacy from shared" + privac);
    			priv = 2;
    			prefs.edit().putString("priva", "2").commit();
    		}
       	 	event.put("accessLevel", priv);
       	 	//end of setting privacy, now get recurring
       	 	boolean calrecurr = prefs.getBoolean("calrecur", false);
    		if (calrecurr == true)
    		{
    			String frequency1 = prefs.getString("calrepeat", "");
    			if (frequency1 == null||frequency1.equals("")) 
    			{
    				frequency1 = "DAILY";
    				prefs.edit().putString("calrepeat", "DAILY").commit();
    			}
    			String frequencystring = "FREQ="+frequency1+";WKST=SU;";
    			event.put("rrule", frequencystring);
    		}
    		//end of setting recurring now set timezone
       	 	String timeZone = TimeZone.getDefault().getID();
       	 	event.put("eventTimezone", timeZone);
       	 	Uri baseUri;
       	 	if (android.os.Build.VERSION.SDK_INT >= 8) 
       	 	{
       	 		baseUri = Uri.parse("content://com.android.calendar/events");
       	 	}	
       	 	else 
       	 	{
       	 		baseUri = Uri.parse("content://calendar/events");
       	 	}
       	 	Uri eventUri =getApplicationContext().getContentResolver().insert(baseUri, event);
       	 	if(eventUri!=null)
       	 	{
       	 		long id = Long.parseLong(eventUri.getLastPathSegment());
       	 		System.out.println(id);
       	   	 	//now get emails
       			String caregiv1 = prefs.getString("caregiv", "");
       			if (caregiv1 != null&&!caregiv1.equals("")) 
       			{
       				String[] emailarr = new String[]{};
       				try
       				{
       					emailarr = caregiv1.trim().split(",");
       				}
       				catch(Exception e)
       				{
       					
       				}
       				for(String email : emailarr)
       				{
       					ContentValues attendees = new ContentValues();
       	   	            attendees.put("attendeeEmail", email.trim());  
       	   	            attendees.put("attendeeRelationship", 1);
       	   	            attendees.put("attendeeType", 1);
       	   	            attendees.put("event_id", id);
       	   	            attendees.put("attendeeStatus", 3);
       	   	   			Uri attendeesUri = null;
       	   	            if (android.os.Build.VERSION.SDK_INT >= 8)
       	   	            {
       	   	                attendeesUri = Uri.parse("content://com.android.calendar/attendees");
       	   	            }
       	   	            else
       	   	            {
       	   	            	attendeesUri = Uri.parse("content://calendar/attendees");
       	   	            }
       	   	            getApplicationContext().getContentResolver().insert(attendeesUri, attendees);
       				}
       			}
       			//set notification times
       	    	Set<String> timeset = prefs.getStringSet("remind", new HashSet<String>()
       	    	{{
       	    	    add("60");
       	    	    add("1440");
       	    	}});
       	    	Uri reminder = Uri.parse("content://com.android.calendar/reminders");
       	    	if(!timeset.isEmpty())
       	    	{
       	        	for (String time : timeset)
       	        	{
       	        		int timeint = 0;
       	        		try
       	        		{
       	        			timeint = Integer.parseInt(time);
       	        		}
       	        		catch(Exception e)
       	        		{
       	        			
       	        		}
       	        		ContentValues values = new ContentValues();
       	    	 		values.put("event_id", id);
       	    	 		//method : 1 - noti 2 - email 3 - SMS 4 - alarm
       	    	 		values.put("method", 1);
       	    	 		values.put("minutes", timeint); 
       	    	 		getApplicationContext().getContentResolver().insert(reminder, values);
       	        	}
       	    	}
       	 	}
       	 	else
       	 	{
       	 		Toast.makeText(this, "Could not create the event!", Toast.LENGTH_LONG).show();
       	 	}
    	}
    	else
    	{
    		Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        	ContentValues cv = new ContentValues();
        	cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "medduler");
        	cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        	cv.put(CalendarContract.Calendars.NAME, "Medduler");
        	cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Medduler");
        	cv.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xffff4444);
        	cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        	cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
        	cv.put(CalendarContract.Calendars.VISIBLE, 1);
        	cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        	calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "medduler")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();
        	getContentResolver().insert(calUri, cv);
        	addtocalendar(eventtitle, eventdesc, eventloc, startTime);
    	}	
    }
   		 
   	/**
     * Converts a String to an integer and catches for error
     * @param inpstring The string to convert to an integer
     * @param debugcode If failed to convert, this debug code is printed in a dialog
     * @return Returns the integer from conversion or 0 if it failed to convert
     */
	public int stringparse(String inpstring, String debugcode)
	{
		int convertedint = 0;
		try 
		{
			convertedint = Integer.parseInt(inpstring);
		} 
		catch(NumberFormatException nfe) 
		{
			Log.e("createcaught", "Error in parsing " + debugcode + ". Value was " + inpstring);
		}
		return convertedint;
	}

	/**
	 * Starts a contact picker for the user to pick from
	 * onActivityResult then gets the returned name and then sets it to
	 * the EditText called Doctor's name
	 * @param Launched when the button beside Doctor's name is clicked
	 */
    public void contactpick(View view)
    {
    	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 2);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
	    {
	    case android.R.id.home:
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
}