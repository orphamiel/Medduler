package com.orphamiel.medduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class listfragment extends ListFragment 
{
	 //this boolean is just to see if the secret code is entered
	 public int secretactivated = 0;
	 public boolean createactivated = false;
	 static int qrclickcount = 1;
	
	 @Override
	 public void onResume() 
	 {
		 super.onResume();
		 createactivated = false;
	     refresh();
	 }
	 
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 setHasOptionsMenu(true);
	 }
	 
	 @Override
	 public void onActivityCreated(Bundle savedInstanceState) 
	 {
	        super.onActivityCreated(savedInstanceState);	
	        /*TODO
	        MultiChoiceModeListener listener = new MultiChoiceModeListener() 
	        {

	            @Override
	            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) 
	            {
	            	//add to arraylist then check in menu click what is in arraylist and do whatever i want
	            	if(checked==true)
	            	{
	            		MedduFiles itemselected = (MedduFiles) getListView().getItemAtPosition(position);
		           	 	final String filename = itemselected.filename;
		           	 	if (filename.contains(".medduler"))
		           	 	{
		           	 		Toast.makeText( getActivity().getBaseContext()  , "Selected " + filename , Toast.LENGTH_SHORT).show();	 
		           	 	}
		           	 	else
		           	 	{
		           	 		
		           	 	}	   
	            	}
	            	else
	            	{
	            		MedduFiles itemselected = (MedduFiles) getListView().getItemAtPosition(position);
		           	 	final String filename = itemselected.filename;
		           	 	if (filename.contains(".medduler"))
		           	 	{
		           	 		Toast.makeText( getActivity().getBaseContext()  , "Unselected " + filename , Toast.LENGTH_SHORT).show();	 
		           	 	}
		           	 	else
		           	 	{
		           	 		
		           	 	}
	            	}   	
	            }

	            @Override
	            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	                // Respond to clicks on the actions in the CAB
	                switch (item.getItemId()) 
	                {
	                    default:
	                        return true;
	                }
	            }

	            @Override
	            public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	            {
	                // Inflate the menu for the CAB
	                MenuInflater inflater = mode.getMenuInflater();
	                return true;
	            }

	            @Override
	            public void onDestroyActionMode(ActionMode mode) {
	                // Here you can make any necessary updates to the activity when
	                // the CAB is removed. By default, selected items are deselected/unchecked.
	            }

	            @Override
	            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	                // Here you can perform updates to the CAB due to
	                // an invalidate() request
	                return false;
	            }
	        };
	        getListView().setMultiChoiceModeListener(listener);
	        */
	        //end todo
	 }
	
     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
     {
    	 View rootView = inflater.inflate(R.layout.infoview, container, false);
         return rootView;        
     }     
     
     /**
      * Creates the option menu. Ran on creation of the fragment. Inflates secretmenu
      * if the secret is activated.
      */
     @Override
     public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
     {
    	 if(secretactivated==1)
    	 {
    		 menu.clear(); 	
    		 getActivity().getMenuInflater().inflate(R.menu.secret, menu);   		 	 
        	 secretactivated=0;
    	 }
    	 else if(secretactivated==2)
    	 {
    		 menu.clear(); 	
    		 getActivity().getMenuInflater().inflate(R.menu.secret2, menu);   		 	 
        	 secretactivated=0;
    	 }        
    	 else
    	 {
    		 inflater.inflate(R.menu.refresh, menu);
    		 inflater.inflate(R.menu.updateall, menu);
    		 final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    		 boolean checked = prefs.getBoolean("showexpire", false);
    		 menu.findItem(R.id.item12).setChecked(checked);
    	 }
     }
     
     /**
      * Handles clicks in menu and does what the button is meant to do
      */
     @Override
     public boolean onOptionsItemSelected(MenuItem item) 
     {
    	Context ctx = (Context)this.getActivity();
        switch (item.getItemId()) 
        {
           case R.id.item9:
        	   refresh();        	  
        	   return true;
           case R.id.item8:
        	   search();
        	   return true;
           case R.id.itemupdate:
        	   updateap();
        	   return true;
           case R.id.item11:
        	   //start create new appointment activity and page
        	   Intent startNewActivityOpen = new Intent(getActivity(), createactivity.class);
        	   if (createactivated==false)
        	   {
            	   startActivity(startNewActivityOpen);
            	   createactivated=true;
        	   }
        	   return true;
           case R.id.item12:
        	   final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	   if (item.isChecked()) 
        	   {
                   item.setChecked(false);
                   prefs.edit().putBoolean("showexpire", false).commit();
               }
               else 
               {
            	   item.setChecked(true);
            	   prefs.edit().putBoolean("showexpire", true).commit();
               }
        	   refresh();
        	   return true;
           //secret commands are below here
           case R.id.startscanner:
        	   Intent intentScan = new Intent("com.orphamiel.zxing.client.android.SCAN2");
        	   intentScan.putExtra("SCAN_MODE", "QR_CODE_MODE");
        	   startActivity(intentScan);
        	   return true;
           case R.id.secretitem1:
           	   dumphash();
           	   return true;
           case R.id.secretitem2:
        	   rootrestart();
        	   return true;
           case R.id.flymoon:
        	   String youtubelink = "rtsp://r7---sn-a5m7zu76.c.youtube.com/CiILENy73wIaGQmWyivhCdXiMRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp";
        	   Intent youtubeintent = new Intent(getActivity(), YoutubeService.class);
        	   youtubeintent.putExtra("extraData", youtubelink);
        	   ctx.startService(youtubeintent);
        	   return true;
           case R.id.getlucky:
        	   youtubelink = "rtsp://r7---sn-a5m7zu7r.c.youtube.com/CiILENy73wIaGQnv7zc8L1dU4BMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp";
        	   youtubeintent = new Intent(getActivity(), YoutubeService.class);
        	   youtubeintent.putExtra("extraData", youtubelink);
        	   ctx.startService(youtubeintent);
        	   return true;
           case R.id.tiggerapp:
				List<MedduFiles> rowItems;
				rowItems = new ArrayList<MedduFiles>();
				Date current = new Date();
				String tigstring = new String("This application was made by Tigger!\n\nAnd Jing Hao is dumb.\nAnd so is Kennedy.\nAnd his stupid master.");
				MedduFiles item2 = new MedduFiles(current, tigstring, "", "", "", 2);
 				rowItems.add(item2);
				FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
				setListAdapter(adapter); 
				setHasOptionsMenu(false);
				getActivity().getPackageManager().setComponentEnabledSetting(getActivity().getComponentName(), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED, android.content.pm.PackageManager.DONT_KILL_APP);
				getActivity().getPackageManager().setComponentEnabledSetting(new android.content.ComponentName("com.orphamiel.medduler", "com.orphamiel.medduler.MainActivity2"), android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED, android.content.pm.PackageManager.DONT_KILL_APP);
				return true;
            case R.id.aestest:
            	aesdiag();
            	return true;
            case R.id.aestest2:
            	aesdiag2();
            	return true;
            case R.id.genqr:
            	startActivity(new Intent(getActivity(), BarcodeGen.class));
            	return true;
            case R.id.makeappt:
            	makefakeappt();
            	refresh();
            	return true;
            case R.id.deletecal:
            	deletecalen();
            	return true;
           default:
              return super.onOptionsItemSelected(item);
        }
     }
     
     /**
      * Checks if update service is already running to avoid duplicate service
      */
     public void updateap()
     {
    	 Context ctx = listfragment.this.getActivity();
    	 Boolean testifrun = false;
    	 ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
         for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
         {
              if (service.getClass().getName().equals("com.orphamiel.medduler.updateservice")) 
              {
                  testifrun = true;
              }
         }
         if (testifrun==false)
         {
        	 Intent updateintent = new Intent(getActivity(), updateservice.class);
       	  	 updateintent.putExtra("extraData", "");
       	  	 ctx.startService(updateintent);  
         }
     }
         
     /**
      * Real search that also loads the related appointments into list
      */
     public class searchasync extends AsyncTask<String, ProgressStore, Integer>
     {
    	List<MedduFiles> rowItems;
    	int filefail = 0;
    	int filetot = 0;
    	int count = 0;
    	HashSet<String> yearset = new HashSet<String>();
    	private ProgressDialog dialog = new ProgressDialog(getActivity());
     	
    	@Override
     	protected void onPreExecute() 
 		{
 		     this.dialog.setMessage("Searching...");
 		     this.dialog.setCancelable(false);
 		     this.dialog.setIndeterminate(true);
 		     this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 		     this.dialog.show();
 		}
     	
     	@Override
	    protected Integer doInBackground(String... writeinfo) 
	    {
     		String[] fileArray;
     		File dir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");
     		if (dir.exists()&&dir.isDirectory()==true) 
         	{
     			File[] files = dir.listFiles(new FilenameFilter() 
         		{
         		    public boolean accept(File dir, String name) 
         		    {
         		        return name.endsWith(".medduler");
         		    }
         		});
         		fileArray = new String[files.length];
         		int numoffiles = 0;
         		for (int i = 0; i < files.length; ++i)
         		{
         			String filenam = files[i].getName();					
    				if (filenam.endsWith(".medduler"))
    				{
    					fileArray[i] = files[i].getName();
    					numoffiles++;
    				}     			
         		}
         		filetot = numoffiles;
         		publishProgress(new ProgressStore(filetot, 0, "files", writeinfo[0]));
         		int filesearched = 0;
         		if (numoffiles!=0)
         		{
         			rowItems = new ArrayList<MedduFiles>();
         			int relatedfiles = 0;
         			while (numoffiles!=0)
         			{
         				final StringBuilder text = new StringBuilder();
         				final File searchfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + fileArray[numoffiles-1]);
         				try 
         		    	{
         		    		 File file = new File(Environment.getExternalStorageDirectory() + "/Medduler/", fileArray[numoffiles-1]);
         		    	     BufferedReader br = new BufferedReader(new FileReader(file));
         		    	     String line;

         		    	     while ((line = br.readLine()) != null) 
         		    	     {
         		    	         text.append(line);
         		    	     }
         		    	     br.close();
         		    	}
         		    	catch (IOException e) 
         		    	{
         		    	    Log.e("listcaughtsearchasync", "Could not read from file " + fileArray[numoffiles-1] , e);
         		    	}
         				/*
         				 * Case insensitive search
         				 */
         				String [] testerr;         				
         				String teststring = new String();
         				Boolean earlyerr = false;
         				try
          	            {     					
          	            	testerr = text.toString().trim().split("#&%");
          	            	teststring = testerr[3] + " dept @ " + testerr[4];
          	            	teststring = "Failed to parse remarks";
          	            	teststring = testerr[5];
          	            	teststring = "Failed to parse date and time";
          	            	teststring = testerr[6] + " " + testerr[7];
          	            	teststring = "Failed to parse issue";
          	            	teststring = testerr[9];
          	            }
          				catch(Exception e)
          	            {
          					if(teststring.equals("")||teststring==null)
          					{
          						Log.e("listcaughtsearch", "Failed to parse at department and location", e);
          					}
          					else
          					{
          						Log.e("listcaughtsearch", teststring, e);
          					}
          					filefail++;
          					earlyerr = true;
          	            }
         				if (earlyerr==false)
         				{
         					int len = writeinfo[0].length();
         			        int max = text.length() - len;
         			        int nodupl = 0;			        
         			        for (int i = 0; i <= max; i++) 
         			        {
         			            if (text.toString().regionMatches(true, i, writeinfo[0], 0, len)) 
         			            {
         			            	if (nodupl==0)
         			            	{		            	
         	 			            	String [] textarr;
         	 	          				String issue = "", location = "", remarks = "", apptdate = "";
         	 	          				int fail = 0;
         	 	          				try
         	 	          	            {     					
         	 	          	            	textarr = text.toString().trim().split("#&%");
         	 	          	            	location = textarr[3] + " dept @ " + textarr[4];
         	 	          	            	remarks = textarr[5];
         	 	          	            	apptdate = textarr[6] + " " + textarr[7];
         	 	          	            	issue = textarr[9];
         	 	          	            }
         	 	          				catch(Exception e)
         	 	          	            {
         	 	          					fail++;
         	 	          	            } 	 	          				
         	 	          				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
         	 	          				Date convertedDate = new Date();
         	 	          				try 
         	 	          				{
         	 	          					convertedDate = dateFormat.parse(apptdate);
         	 	          				} 
         	 	          				catch (ParseException e) 
         	 	          				{
         	 	          					Log.e("listcaughtsearch", "Failed while parsing timestamp " + apptdate , e);
         	 	          					fail++;
         	 	          				}
         	 	          				String yeartest = "";
         	 	          				SimpleDateFormat itemformat = new SimpleDateFormat("yyyy", Locale.US);
         	 	          				if (fail==0)
         	 	          				{
         	 	          					MedduFiles item = new MedduFiles(convertedDate, searchfile.getName(), issue, location, remarks, 0);
         	 	          					rowItems.add(item);
         	 	          					Date itemdate = item.aptdate;
         	 	          					yeartest = itemformat.format(itemdate);
         	 	          					yearset.add(yeartest);
         	 	          					relatedfiles++;
         	 	          					count++;
         	 	          				}          			    	
         			            	}			            	 
         			            	nodupl++;
         			            }
         			        }
         			        //end of case insensitive search    				
             				numoffiles--;
         				}
         				else
         				{
         					numoffiles--;
         				}
         				publishProgress(new ProgressStore(filetot, filesearched, fileArray[numoffiles], writeinfo[0]));
         				filesearched++;
         			}
         			publishProgress(new ProgressStore(filetot, filesearched, fileArray[numoffiles], writeinfo[0]));
         			if (filefail==filetot)
      				{
         				return 2;
      				}
         			else
         			{        				
         				if (relatedfiles>0)
             			{
         					Iterator<String> it = yearset.iterator();
          			    	while(it.hasNext())
          			    	{
          			    		String headyear = it.next();
          			    		SimpleDateFormat headformat = new SimpleDateFormat("yyyy", Locale.US);
          			    		Date headdate = new Date();
              				    try 
              				    {
              				        headdate = headformat.parse(headyear);
              				    } 
              				    catch (ParseException e) 
              				    {
              				    	Log.e("listcaughtsearchasync", "Failed parsing date " + headyear, e);
              				    }
              				    MedduFiles item = new MedduFiles(headdate, headyear, "", "", "", 3);
            			    	rowItems.add(item);
          			    	}
          			    	Collections.reverse(rowItems);
             				Collections.sort(rowItems);
         					getActivity().runOnUiThread(new Runnable() 
         					{
         					    public void run() 
         					    {
         					    	FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
                    		        setListAdapter(adapter); 
         					    }
         					});     
             				return 0;
             			}
             			else
             			{
             				Date current = new Date();
             				MedduFiles item = new MedduFiles(current, "\nNo files with the term found.\n\nSearch again or refresh the list.\n\nSearch syntax :\nDate : 01-01-2013\nTime : 01:01 am \n", "", "", "", 2);
	          				rowItems.add(item);
             				getActivity().runOnUiThread(new Runnable() 
         					{
         					    public void run() 
         					    {
         					    	FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
                    		        setListAdapter(adapter); 
         					    }
         					}); 
             				return 1;
             			}
         			}         			
         		}
         		else
         		{
         			return 2;
         		}
         	}
     		else
     		{
     			return 2;
     		}    		
	    }
     	
     	@Override
     	protected void onProgressUpdate(ProgressStore... progress) 
     	{
     		super.onProgressUpdate(progress);
     		int maxint = progress[0].max;
     		int progressint = progress[0].progress;
            this.dialog.setIndeterminate(false);
            this.dialog.setMax(maxint);
            this.dialog.setProgress(progressint);
            if(maxint!=progressint)
            {
            	this.dialog.setMessage("Searching for \"" + progress[0].searchterm + "\" in "+ progress[0].filename);
            }
            else
            {
            	this.dialog.setMessage("Search completed");
            }
        }
     	
     	@Override
     	protected void onPostExecute(final Integer result) 
     	{
     		if (this.dialog.isShowing()) 
    		{
    	        this.dialog.dismiss();
    		}
     		if (result==0)
	    	{
	    		Toast.makeText(getActivity(), "Search completed with " + Integer.toString(count) + " results", Toast.LENGTH_SHORT).show();
	    	}
     		else if (result==1)
     		{
	    		Toast.makeText(getActivity(), "No files with the term were found", Toast.LENGTH_SHORT).show();
     		}
     		else
     		{
	    		Toast.makeText(getActivity(), "You have no appointments", Toast.LENGTH_SHORT).show();
     		}
     		getListView().animate()
			 			 .alpha(0f)
			 			 .setDuration(75)
			 			 .setListener(new AnimatorListenerAdapter() 
			 			 {
			 				 @Override
			 				 public void onAnimationEnd(Animator animation) 
			 				 {
			 					 getListView().animate()
			 					 			  .alpha(1f)
			 					 			  .setDuration(75);
			 				 }
			 			 });
	    }
     	
     }
         
     /**
      * Launches search dialog and checks for invalid search values before
      * prompting the real async search
      */
     public void search()
     {  	 
    	 final Dialog singledia = new Dialog(getActivity());
    	 singledia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
    	 singledia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
    	 singledia.setContentView(R.layout.custsearch);
    	 final AutoCompleteTextView input = (AutoCompleteTextView)singledia.findViewById(R.id.searchedit);
    	 singledia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    	 singledia.findViewById(R.id.spos_butt).setOnClickListener(new OnClickListener() 
    	 {
    		 @Override
    		 public void onClick(View v) 
    		 {
    			 	String seresult = input.getText().toString().trim();
	     			if (seresult != null&&!seresult.equals("")) 
	     			{
	     				if (!seresult.contains("#&%"))
	     				{
	     					searchasync task = new searchasync();
	     					task.execute(new String[] {seresult});				
	     				}
	     				else
	     				{
	     					String messagestring = "";
	     					Random rand = new Random();
	     					int randomNum = rand.nextInt((3- 1) + 1) + 1;
	     					if (randomNum <= 2)
	     					{
	     						messagestring = "DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS";
	     					}
	     					else if (randomNum ==3)
	     					{
	     						messagestring = "ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL ORPHAMIEL";
	     					}
	     					else
	     					{
	     							
	     					}
	     					final Dialog secretdia = new Dialog(getActivity());
	     					secretdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
	     					secretdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
	     					secretdia.setContentView(R.layout.customdiag);
	     					((TextView)secretdia.findViewById(R.id.diagtitle)).setText("DEVELOPERS DEVELOPERS DEVELOPERS DEVELOPERS");    		
	     					((TextView)secretdia.findViewById(R.id.diagmessage)).setText(messagestring);	     								
	     					secretdia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	     					Button secretbutt = (Button)secretdia.findViewById(R.id.positive_button);
	     					secretbutt.setText("DEVELOPERS");
	     					secretbutt.setOnClickListener(new OnClickListener() 
	     					{
	     			            @Override
	     			            public void onClick(View v) 
	     			            {
	     			                secretdia.dismiss();
	     			                MediaPlayer mPlayer2;
									mPlayer2 = MediaPlayer.create(getActivity(), R.raw.maintheme);
									mPlayer2.setLooping(true);
									mPlayer2.start();
	     			            }
	     					});
	     					secretdia.setCancelable(false);
	     					secretdia.show();   					   		     					
	     				}   		     				
	     			}
	     			else
	     			{
	     				DialogFragment newFragment = new singledialog();
	        	    	Bundle args = new Bundle();
	        	    	args.putString("title", "Error");
	        	    	args.putString("text", "Please enter a term to search for");
	        	    	newFragment.setArguments(args);
	        	        newFragment.show(getActivity().getSupportFragmentManager(), "dialog"); 
	     			}
	     			singledia.dismiss();
    		 }
    	 });
    	 singledia.findViewById(R.id.snega_butt).setOnClickListener(new OnClickListener() 
    	 {
    		 @Override
             public void onClick(View v) 
             {
    			 singledia.dismiss();
    			 InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    			 imm.hideSoftInputFromWindow(input.getWindowToken(), 0);   			 
             }
    	 });
    	 singledia.show();
    	 input.setFocusableInTouchMode(true);
    	 input.requestFocus();
    	 InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	 inputMethodManager.toggleSoftInputFromWindow(input.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
     }
         
     /**
      * Calls loadasync().execute to refresh the list. Acts as a jumper wire really...
     */
     public void refresh()
     {
    	 new loadasync().execute();	
     }
     
     /**
      * Handles clicking of appointments in listview
      */
     @Override
     @SuppressWarnings("unchecked")
     public void onListItemClick(ListView l, View v, int position, long id) 
     {    
    	 final StringBuilder text = new StringBuilder();
    	 MedduFiles itemselected = (MedduFiles) getListView().getItemAtPosition(position);
    	 final String filename = itemselected.filename;
    	 if (filename.contains(".medduler"))
    	 {
    		 int catcherror = 0;
        	 final File sendfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + filename);
        	 try 
        	 {
        	     BufferedReader br = new BufferedReader(new FileReader(sendfile));
        	     String line;

        	     while ((line = br.readLine()) != null) 
        	     {
        	         text.append(line);
        	     }
        	     br.close();
        	 }
        	 catch (IOException e) 
        	 {
        	     Log.e("listcaughtonlistclick", "Could not read from file " + filename, e);
        	     catcherror =1;
        	 }
        	 if (catcherror == 0)
        	 {
        		 int catcherror2 = 0;
        		 String [] textarr;
            	 String apptdate = "";
                 String fullmessagestring = "File is corrupted or invalid. Please make sure it's a valid .medduler file.";
             	 try
             	 {
             		 textarr = text.toString().trim().split("#&%");
             		 String fullname = textarr[1] + " " + textarr[2];
             	 	 fullmessagestring = "Patient Name : " + fullname + "\nDoctor : " + textarr[10] + 
             			 				 "\nDepartment : " + textarr[3] + "\nIssue : " + textarr[9] +
             			 				 "\nDate : " + textarr[6] + "\nTime : " + textarr[7] + 
             			 				 "\nLocation : " + textarr[4] + "\nRemarks : " + textarr[5] ;
             	 	 apptdate = textarr[6];
             	 }
             	 catch(Exception e)
             	 {
             		Log.e("listcaughtonlistclick", "Invalid text from "+ filename, e);
                	catcherror2 = 1;
             	 }
             	 final String apptdate2 = apptdate; 
             	 if (catcherror2 == 0)
             	 {
             		 //lol integrated an entire dialogfragment here
            		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            		final Context mContext = getActivity();
            		
            		final Dialog singledia = new Dialog(getActivity());
            		singledia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
            		singledia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
            		singledia.setContentView(R.layout.custtridi);
            		((TextView)singledia.findViewById(R.id.diagtitle)).setText(filename);
            		((TextView)singledia.findViewById(R.id.diagmessage)).setText(fullmessagestring);
            		singledia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            		singledia.findViewById(R.id.positive_button).setOnClickListener(new OnClickListener() 
            		{
                        @Override
                        public void onClick(View v) 
                        {
                        	singledia.dismiss();
                        	final Dialog singledia2 = new Dialog(getActivity());
                    		singledia2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                    		singledia2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                    		singledia2.setContentView(R.layout.custduodiag);
                    		((TextView)singledia2.findViewById(R.id.diagtitle2)).setText(filename);
                    		((TextView)singledia2.findViewById(R.id.diagmessage2)).setText("How should " + filename +" be shared?");
                    		((Button)singledia2.findViewById(R.id.pos_butt)).setText("Other");
                    		((Button)singledia2.findViewById(R.id.nega_butt)).setText("QR");
                    		singledia2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    		singledia2.findViewById(R.id.pos_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	singledia2.dismiss();
                                	//original share button
                                	Intent intent = new Intent(Intent.ACTION_SEND);     	  
                                	String caregiv1 = prefs.getString("caregiv", "");
                                	String[] emailarr = caregiv1.split(",");
                                	intent.putExtra(Intent.EXTRA_EMAIL, emailarr);
                                	intent.putExtra(Intent.EXTRA_SUBJECT, apptdate2 + " Appointment");
                                	intent.putExtra(Intent.EXTRA_TITLE, "Jing Hao testing");
                                	intent.putExtra(Intent.EXTRA_TEXT, "Hello, this is my appointment for " + apptdate2 + ". Thank you!");
                                	intent.setType("image/medduler");
                                	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sendfile));
                                	startActivity(Intent.createChooser(intent, "Share By : "));
                                	
                                }
                    		});
                    		singledia2.findViewById(R.id.nega_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	//closebutton
                                	singledia2.dismiss();
                                	//new share button
                                	if(text.toString().getBytes().length<=1000)
                                	{
                                		String encr = new String();
										try 
										{
											encr = AES.encrypt(text.toString(), AES.aeskey);
										} 
										catch (Exception e) 
										{
											Log.e("listcaughtonlistclick", "Failed to encrypt " + text.toString(), e);
										} 
										final String qrtext = encr;
                                		Bitmap gend = BarcodeGen.datatoqr(qrtext, 400, 400);
                                    	final Dialog picdia = new Dialog(getActivity());
                                		picdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                                		picdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                                		picdia.setContentView(R.layout.qrsharediag);
                                		((TextView)picdia.findViewById(R.id.qrdiagtitle)).setText(filename);
                                		final ImageView QRimage = (ImageView)picdia.findViewById(R.id.qrview);
                                		QRimage.setImageBitmap(gend);
                                		QRimage.setOnClickListener(new OnClickListener()
                                		{

											@Override
											public void onClick(View v)
											{
												switch(qrclickcount)
												{
													case 1:
														Bitmap gend2 = BarcodeGen.datatoqr(qrtext, 500, 500);
														QRimage.setImageBitmap(gend2);
														qrclickcount++;
														break;
													case 2:
														Bitmap gend3 = BarcodeGen.datatoqr(qrtext, 600, 600);
														QRimage.setImageBitmap(gend3);
														qrclickcount++;
														break;
													case 3:
														Bitmap gend4 = BarcodeGen.datatoqr(qrtext, 400, 400);
														QRimage.setImageBitmap(gend4);
														qrclickcount=1;
														break;
												}
											}
                                			
                                		});
                                		picdia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                		picdia.findViewById(R.id.qrbutton).setOnClickListener(new OnClickListener() 
                                		{
                                            @Override
                                            public void onClick(View v) 
                                            {
                                                picdia.dismiss();
                                            }
                                		});
                                		picdia.show();
                                	}
                                	else
                                	{
                                		DialogFragment newFragment = new singledialog();
                            	    	Bundle args = new Bundle();
                            	    	args.putString("title", "QR cannot be generated");
                            	    	args.putString("text", "This file is bigger than the maximum of 1000 bytes");
                            	    	newFragment.setArguments(args);
                            	        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");  
                                	}
                                	//end new share button
                                }
                    		});
                    		singledia2.show();
                        }
            		});
            		singledia.findViewById(R.id.neutral_button).setOnClickListener(new OnClickListener() 
            		{
                        @Override
                        public void onClick(View v) 
                        { 
                        	singledia.dismiss();
                        	final Dialog singledia2 = new Dialog(getActivity());
                    		singledia2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                    		singledia2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                    		singledia2.setContentView(R.layout.custduodiag);
                    		((TextView)singledia2.findViewById(R.id.diagtitle2)).setText(filename);
                    		((TextView)singledia2.findViewById(R.id.diagmessage2)).setText("Are you sure you want to delete " +filename + " ?\n" + "\nWarning : \nThis action cannot be undone!");
                    		((Button)singledia2.findViewById(R.id.pos_butt)).setText("Yes");
                    		((Button)singledia2.findViewById(R.id.nega_butt)).setText("No");
                    		singledia2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    		singledia2.findViewById(R.id.pos_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	singledia2.dismiss();
                                	try
                        			{
                        				sendfile.delete();
                        			}
                        			catch(Exception e)
                        			{
                        				Log.e("listcaughtonlistclick", "Failed to delete invalid file " + filename, e);
                        			}
                        			File file = mContext.getFileStreamPath("Medduler.bin");
                        			if(file.exists())
                        			{
                        				try
                        				{
                        					HashMap<String, String> filemap;
                        					FileInputStream fos = mContext.openFileInput("Medduler.bin");
                        					ObjectInputStream inputstream = new ObjectInputStream(fos);
                        					filemap = (HashMap<String, String>) inputstream.readObject();
                        					inputstream.close();
                        					filemap.remove(filename);
                        					FileOutputStream fos2 = mContext.openFileOutput("Medduler.bin", Context.MODE_PRIVATE);   
                        					ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
                        					outputStream.writeObject(filemap);
                        					outputStream.flush();
                        					outputStream.close();
                        				}    	
                        				catch (Exception e)
                        				{
                        					Log.e("listcaughtonlistclick", "Failed while deleting " + filename, e);
                        				}
                        			}
                        			File file2 = mContext.getFileStreamPath("calmap.bin");
                        			if(file2.exists())
                        			{
                        				try
                        				{
                        					HashMap<String, Long> calmap;
                        					FileInputStream fos = mContext.openFileInput("calmap.bin");
                        					ObjectInputStream inputstream = new ObjectInputStream(fos);
                        					calmap = (HashMap<String, Long>) inputstream.readObject();
                        					inputstream.close();
                        					calmap.remove(filename);
                        					FileOutputStream fos2 = mContext.openFileOutput("calmap.bin", Context.MODE_PRIVATE);   
                        					ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
                        					outputStream.writeObject(calmap);
                        					outputStream.flush();
                        					outputStream.close();
                        				}    	
                        				catch (Exception e)
                        				{
                        					Log.e("listcaughtonlistclick", "Failed while deleting " + filename, e);
                        				}
                        			}
                        			refresh();
                                }
                    		});
                    		singledia2.findViewById(R.id.nega_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	//closebutton
                                	singledia2.dismiss();
                                }
                    		});
                    		singledia2.setCancelable(false);
                    		singledia2.show();
                        }
            		});
            		singledia.findViewById(R.id.negative_button).setOnClickListener(new OnClickListener() 
            		{
                        @Override
                        public void onClick(View v) 
                        {
                        	//closebutton
                        	singledia.dismiss();
                        }
            		});
            		singledia.show();
            		//end of integration this is madness
             	 }
             	 else
             	 {
                	final Dialog singledia = new Dialog(getActivity());
            		singledia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
            		singledia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
            		singledia.setContentView(R.layout.custduodiag);
            		((TextView)singledia.findViewById(R.id.diagtitle2)).setText(filename);
            		((TextView)singledia.findViewById(R.id.diagmessage2)).setText(fullmessagestring);
            		((Button)singledia.findViewById(R.id.pos_butt)).setText("Delete");
            		((Button)singledia.findViewById(R.id.nega_butt)).setText("Close");
            		singledia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            		singledia.findViewById(R.id.pos_butt).setOnClickListener(new OnClickListener() 
            		{
                        @Override
                        public void onClick(View v) 
                        {
                        	singledia.dismiss();
                        	final Dialog singledia2 = new Dialog(getActivity());
                    		singledia2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                    		singledia2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                    		singledia2.setContentView(R.layout.custduodiag);
                    		((TextView)singledia2.findViewById(R.id.diagtitle2)).setText(filename);
                    		((TextView)singledia2.findViewById(R.id.diagmessage2)).setText("Are you sure you want to delete " +filename + " ?\n" + "\nWarning : \nThis action cannot be undone!");
                    		((Button)singledia2.findViewById(R.id.pos_butt)).setText("Yes");
                    		((Button)singledia2.findViewById(R.id.nega_butt)).setText("No");
                    		singledia2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    		singledia2.findViewById(R.id.pos_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	singledia2.dismiss();
                                	try
                                	{
                                		sendfile.delete();
                                	}
                                	catch(Exception e)
                                	{
                                		Log.e("listcaughtonlistclickinvalid", "Failed to delete invalid file " + filename, e);
                                	}
                                	refresh();
                                }
                    		});
                    		singledia2.findViewById(R.id.nega_butt).setOnClickListener(new OnClickListener() 
                    		{
                                @Override
                                public void onClick(View v) 
                                {
                                	//closebutton
                                	singledia2.dismiss();
                                }
                    		});
                    		singledia2.setCancelable(false);
                    		singledia2.show();
                        }
            		});
            		singledia.findViewById(R.id.nega_butt).setOnClickListener(new OnClickListener() 
            		{
                        @Override
                        public void onClick(View v) 
                        {
                        	//closebutton
                        	singledia.dismiss();
                        }
            		});
            		singledia.setCancelable(false);
            		singledia.show();
             	 }
        	 }
        	 else
        	 {
        		 DialogFragment newFragment = new singledialog();
        		 Bundle args = new Bundle();
     	    	 args.putString("title", filename);
     	    	 args.putString("text", "The file does not exist");
     	    	 newFragment.setArguments(args);
     	    	 newFragment.show(getActivity().getSupportFragmentManager(), "passeddialog");
        	 }
    	 }
    	 else
    	 {
    		 if(filename.equals("1994"))
    		 {   			 
    			 Toast.makeText(getActivity(), "Developer's Secret Menu Activated", Toast.LENGTH_LONG).show();
    			 secretactivated=1;
    			 getActivity().invalidateOptionsMenu();
    		 }
    		 else if(filename.equals("1936"))
    		 {
    			 Toast.makeText(getActivity(), "Orphamiel's Secret Menu Activated", Toast.LENGTH_LONG).show();
    			 secretactivated=2;
    			 getActivity().invalidateOptionsMenu();
    		 }
    		 else
    		 {

    		 }
    	 }
     }
     
     /**
      * Loads the listview with appointments. Can also be used as a refresh
      */
     public class loadasync extends AsyncTask<String, Void, Integer>
     {
    	List<MedduFiles> rowItems;	
    	ListView listv = getListView();
 		HashSet<String> yearset = new HashSet<String>();
    	int success = 0;
    	int filefail = 0;
    	int filetot = 0;
         
     	protected void onPreExecute() 
  		{
     		rowItems = new ArrayList<MedduFiles>();
     		Date current = new Date();
     		MedduFiles item = new MedduFiles(current, "Loading...", "", "", "", 2);
		    rowItems.add(item);
  			getActivity().runOnUiThread(new Runnable() 
     		{
     		     public void run() 
     		     {
     		         FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
     		         setListAdapter(adapter);        		        
     		     }
     		});			
  			listv.animate()
  						 .alpha(0f)
  						 .setDuration(150)
  						 .setListener(new AnimatorListenerAdapter() 
  						 {
  							 @Override
  							 public void onAnimationEnd(Animator animation) 
  							 {
  								 listv.animate()
					 			  			  .alpha(1f)
					 			  			  .setDuration(0);
  							 }
  						 });
  		}
     	
     	@Override
 	    protected Integer doInBackground(String... writeinfo) 
 	    {
     		rowItems = new ArrayList<MedduFiles>();
     		String[] fileArray;
      		File dir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");
      		if (dir.exists()&&dir.isDirectory()==true) 
         	{
          		File[] files = dir.listFiles(new FilenameFilter() 
          		{
          		    public boolean accept(File dir, String name) 
          		    {
          		        return name.endsWith(".medduler");
          		    }
          		});
          		fileArray = new String[files.length];
          		int numoffiles = 0;
          		for (int i = 0; i < files.length; ++i)
          		{
          			String filenam = files[i].getName();					
     				if (filenam.endsWith(".medduler"))
     				{
     					fileArray[i] = files[i].getName();
     					numoffiles++;
     				}     			
          		}
          		filetot = numoffiles;
          		//If there are files    		
          		if (numoffiles!=0)
          		{      		    	
          			String apptdate = "";
          			while (numoffiles!=0)
          			{
          				final StringBuilder text = new StringBuilder();
          				final File searchfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + fileArray[numoffiles-1]);
          				try 
          		    	{
          		    		 File file = new File(Environment.getExternalStorageDirectory() + "/Medduler/", fileArray[numoffiles-1]);
          		    	     BufferedReader br = new BufferedReader(new FileReader(file));
          		    	     String line;

          		    	     while ((line = br.readLine()) != null) 
          		    	     {
          		    	         text.append(line);
          		    	     }
          		    	     br.close();
          		    	}
          		    	catch (IOException e) 
          		    	{
          		    	    Log.e("listcaughtrefresh", "Could not read from file " +fileArray[numoffiles-1], e);
          		    	}
          				/*
          				 * Important stuff from here on.
          				 */
          				String [] textarr;
          				String issue = "";
          				String location = "";
          				String remarks = "";
          				int fail = 0;
          				try
          	            {     					
          	            	textarr = text.toString().trim().split("#&%");
          	            	location = textarr[3] + " dept @ " + textarr[4];
          	            	remarks = textarr[5];
          	            	apptdate = textarr[6] + " " + textarr[7];
          	            	issue = textarr[9];
          	            }
          				catch(Exception e)
          	            {
          					fail++;
          					filefail++;
          	            }
          				if (fail==0)
          				{
          					final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
          					boolean checked = prefs.getBoolean("showexpire", false);
          					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
          				    Date convertedDate = new Date();
          				    Date currentDate = convertedDate;
          				    try 
          				    {
          				        convertedDate = dateFormat.parse(apptdate);
          				    } 
          				    catch (ParseException e) 
          				    {
          				    	Log.e("listcaughtrefresh", "Error while parsing timestamp " + apptdate, e);
          				    }
          			    	String yeartest = "";
          			    	SimpleDateFormat itemformat = new SimpleDateFormat("yyyy", Locale.US);
          					if (checked==true)
          					{
              			    	MedduFiles item = new MedduFiles(convertedDate, searchfile.getName(), issue, location, remarks, 0);
              			    	rowItems.add(item);
              			    	Date itemdate = item.aptdate;
              			    	yeartest = itemformat.format(itemdate);
              			    	yearset.add(yeartest);
              					success++;             			    	
          					}
          					else
          					{
          						if (convertedDate.after(currentDate))
             				    {
          							MedduFiles item = new MedduFiles(convertedDate, searchfile.getName(), issue, location, remarks, 0);
                  			    	rowItems.add(item);
                  			    	Date itemdate = item.aptdate;
                  			    	yeartest = itemformat.format(itemdate);
                  			    	yearset.add(yeartest);
                 					success++;                  			    	
             				    }
          					}
          				}
          				else
          				{
          					//if it failed we reset it
          					fail=0;
          				}     				
          				numoffiles--;     				
          			}
  			    	Iterator<String> it = yearset.iterator();
  			    	while(it.hasNext())
  			    	{
  			    		String headyear = it.next();
  			    		SimpleDateFormat headformat = new SimpleDateFormat("yyyy", Locale.US);
  			    		Date headdate = new Date();
      				    try 
      				    {
      				        headdate = headformat.parse(headyear);
      				    } 
      				    catch (ParseException e) 
      				    {
      				    	Log.e("listcaughtrefresh", "Error while parsing year " + headyear, e);
      				    }
      				    MedduFiles item = new MedduFiles(headdate, headyear, "", "", "", 3);
    			    	rowItems.add(item);
  			    	}
  			    	Collections.reverse(rowItems);
      				Collections.sort(rowItems);
      				if (filefail==filetot)
      				{
      					Date current = new Date();
      	  				MedduFiles item = new MedduFiles(current, "No appointments", "", "", "", 1);
      				    rowItems.add(item);
      	      			getActivity().runOnUiThread(new Runnable() 
      	         		{
      	         		     public void run() 
      	         		     {
      	         		         FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
      	         		         setListAdapter(adapter);        		        
      	         		     }
      	         		});
      	      			
      	      			return 1;
      				}
      				else
      				{
          				Date currentDate2 = new Date();
    					if(success==0)
    					{
    						MedduFiles item = new MedduFiles(currentDate2, "No upcoming appointments", "", "", "", 1);
          			    	rowItems.add(item);
    					}
      					getActivity().runOnUiThread(new Runnable() 
                 		{
                 		     public void run() 
                 		     {
                 		         FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
                 		         setListAdapter(adapter);  
                 		     }
                 		});
                 		return 0;
      				}             		
          		}
          		else
          		{
          			Date current = new Date();
      				MedduFiles item = new MedduFiles(current, "No appointments", "", "", "", 1);
    			    rowItems.add(item);
          			getActivity().runOnUiThread(new Runnable() 
             		{
             		     public void run() 
             		     {
             		         FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
             		         setListAdapter(adapter);  
             		     }
             		});
          			return 1;
          		}
         	}
      		else
      		{
      			Date current = new Date();
  				MedduFiles item = new MedduFiles(current, "No appointments", "", "", "", 1);
			    rowItems.add(item);
      			getActivity().runOnUiThread(new Runnable() 
         		{
         		     public void run() 
         		     {
         		         FileAdapter adapter = new FileAdapter(getActivity().getApplicationContext(), R.layout.mainlistrow, rowItems, getListView());        
         		         setListAdapter(adapter);        		        
         		     }
         		});
      			return 1;
      		}
 	    }
     	
     	protected void onPostExecute(final Integer result) 
      	{
     		listv.animate()
			 			 .alpha(0f)
			 			 .setDuration(0)
			 			 .setListener(new AnimatorListenerAdapter() 
			 			 {
			                 @Override
			                 public void onAnimationEnd(Animator animation) 
			                 {
			                	 listv.animate()
		     					 .alpha(1f)
		     					 .setDuration(150);
			                 }
			             });
     		if (result==0)
     		{
     			//Everything is normal
     		}
     		else if (result==1)
     		{
     			//No appointments... do something here
     		}
     		else
     		{
     			Log.e("listcaughtrefresh", "Weird out of bounds error");
     		}
      	}
     }
     
     /*
      * Irrelevant or debug code below to avoid taking up space above
      */
     
     /**
      * Dumps hashmap of ftp links of appointment
      */
     @SuppressWarnings("unchecked")
     public void dumphash()
     {
    	 String a = new String();
    	 try 
    	 {	
    		 HashMap<String, String> filemap;
    		 FileInputStream fos = getActivity().getApplicationContext().openFileInput("Medduler.bin");
    		 ObjectInputStream inputstream = new ObjectInputStream(fos);
    		 filemap = (HashMap<String, String>) inputstream.readObject();
    		 inputstream.close();
    		 for (Map.Entry<String, String > entry : filemap.entrySet()) 
    		 {
    			 if (entry.getValue()!=null)
    			 {
    				 a += (entry.getKey() + "," + entry.getValue());  
    			 }
    			 else
    			 {
    				 a += (entry.getKey() + "," + "null");  
    			 }
    			 a += "\n";
    		 }
    	 }
    	 catch(Exception e)
    	 {
    		 a =  e.getMessage();
    	 }
    	 DialogFragment newFragment = new singledialog();
    	 Bundle args = new Bundle();
    	 args.putString("title", "Hashmap Info");
    	 args.putString("text", a);
    	 newFragment.setArguments(args);
    	 newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
     }
     
     /**
      * Restarts the device if there's root access
      */
     public void rootrestart()
     {
    	 try
    	 {
    		 Process root = Runtime.getRuntime().exec(new String[]{ "su", "-c", "reboot" });
    		 root.waitFor();
    	 }
    	 catch(Exception e)
    	 {
    		 Toast.makeText(getActivity(), "This device does not have root access", Toast.LENGTH_SHORT).show();
    		 Log.e("listcaughtrestart", "Request for restart failed", e);
    	 }
     }
     
     /**
      * Creates a dialog for encryption of a string to AES256 with my key and IV
      */
     public void aesdiag()
     {
    	 final Dialog aesdia = new Dialog(getActivity());
    	 aesdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
     	 aesdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
     	 aesdia.setContentView(R.layout.aestest);
     	 aesdia.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
     	 final EditText aesinput = (EditText)aesdia.findViewById(R.id.aesedit);
     	 ((TextView)aesdia.findViewById(R.id.aestitle)).setText("AES256 Encryption");
     	 aesinput.addTextChangedListener(new TextWatcher()
     	 {    		 
     		 public void afterTextChanged(Editable s) 
     		 {
     		 }
     		 
     		 public void beforeTextChanged(CharSequence s, int start, int count, int after) 
     		 {
     		 }
     		 
     		 public void onTextChanged(CharSequence s, int start, int before, int count) 
     		 {
     			 try 
     			 {
     				 ((TextView)aesdia.findViewById(R.id.aesmessage)).setText(AES.encrypt(aesinput.getText().toString(), AES.aeskey));
     			 } 
     			 catch (Exception e) 
     			 {
     				 Log.e("listcaughtaesdiag" , "Failed to encrypt " + aesinput.getText().toString(), e);
     			 }
     		 }
     	 });
     	 aesdia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     	 aesdia.findViewById(R.id.aesbutt).setOnClickListener(new OnClickListener() 
     	 {
     		 @Override
     		 public void onClick(View v) 
     		 {
     			 aesdia.dismiss();
     		 }
     	 });
     	 aesdia.findViewById(R.id.aesbutt2).setOnClickListener(new OnClickListener() 
     	 {
     		 @Override
     		 public void onClick(View v) 
     		 {
     			 String cliptext = ((TextView)aesdia.findViewById(R.id.aesmessage)).getText().toString();
     			 if (cliptext.equals("Type the string to encrypt below :"))
     			 {
     				Toast.makeText(getActivity().getBaseContext(), "Why don't you type something in the textbox first?", Toast.LENGTH_SHORT).show();
     			 }
     			 else
     			 {
     				Toast.makeText(getActivity().getBaseContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
     				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(android.app.Activity.CLIPBOARD_SERVICE); 
     				ClipData clip = ClipData.newPlainText("Encrypted Text", cliptext);
     				clipboard.setPrimaryClip(clip);
     			 }	 
     		 }
     	 });
     	 aesdia.setCancelable(false);
     	 aesdia.show();
     	 aesinput.setFocusableInTouchMode(true);
     	 aesinput.requestFocus();
     	 InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
     	 inputMethodManager.toggleSoftInputFromWindow(aesinput.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
     }
     
     /**
      * Creates a dialog for decryption of a string to AES256 with my key and IV
      */
     public void aesdiag2()
     {
    	 final Dialog aesdia = new Dialog(getActivity());
    	 aesdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
     	 aesdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
     	 aesdia.setContentView(R.layout.aestest);
     	 final EditText aesinput = (EditText)aesdia.findViewById(R.id.aesedit);
     	 ((TextView)aesdia.findViewById(R.id.aestitle)).setText("AES256 Decryption");
     	 ((TextView)aesdia.findViewById(R.id.aesmessage)).setText("Type the string to decrypt below :");
     	 ((Button)aesdia.findViewById(R.id.aesbutt2)).setText("Decrypt");
     	 aesdia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     	 aesdia.findViewById(R.id.aesbutt).setOnClickListener(new OnClickListener() 
     	 {
     		 @Override
     		 public void onClick(View v) 
     		 {
     			 aesdia.dismiss();
     		 }
     	 });
     	 aesdia.findViewById(R.id.aesbutt2).setOnClickListener(new OnClickListener() 
     	 {
     		 @Override
     		 public void onClick(View v) 
     		 {
     			 aesdia.dismiss();
     			 try 
     			 {
					String result = AES.decrypt(aesinput.getText().toString(), AES.aeskey);
					DialogFragment newFragment = new singledialog();
        	    	Bundle args = new Bundle();
        	    	args.putString("title", "Decrypted Text");
        	    	args.putString("text", "Decrypted text is :\n" + result);
        	    	newFragment.setArguments(args);
        	        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
     			 } 
     			 catch (Exception e) 
     			 {
     				Log.e("listcaughtaesdiag2", "Error while decrypting " + aesinput.getText().toString(), e);
     				DialogFragment newFragment = new singledialog();
        	    	Bundle args = new Bundle();
        	    	args.putString("title", "Decryption Error");
        	    	args.putString("text", "Could not decrypt " + aesinput.getText() + " :\n" + e.getMessage());
        	    	newFragment.setArguments(args);
        	        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
     			 }
     		 }
     	 });
     	 aesdia.setCancelable(false);
     	 aesdia.show();
     	 aesinput.setFocusableInTouchMode(true);
     	 aesinput.requestFocus();
     	 InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
     	 inputMethodManager.toggleSoftInputFromWindow(aesinput.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
     }
     
     public void makefakeappt()
     {
     	try 
		{
     		File mmeddulerdir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");			
     		mmeddulerdir.mkdirs();
     		Date mcurrent = new Date();
     		Date mappttime = new Date();
     		mappttime = new Date(mappttime.getTime()+3600000);
     		SimpleDateFormat mFormat2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
     		String mfilename = mFormat2.format(mcurrent);
     		SimpleDateFormat mFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.US);
  	    	String mtimestamp = mFormat.format(mcurrent);
  	    	mtimestamp = mtimestamp.toUpperCase(Locale.ENGLISH);
  	    	SimpleDateFormat mdateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            String mdatecurr = mdateFormat2.format(mappttime);
            SimpleDateFormat mtimeFormat2 = new SimpleDateFormat("h:mm aa", Locale.US);
            String mtimecurr = mtimeFormat2.format(mappttime);
            mtimecurr = mtimecurr.toUpperCase(Locale.ENGLISH);
  	    	File mfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/", mfilename+".medduler");
  	    	FileWriter mfw1 = new FileWriter(mfile.getAbsoluteFile());
  	    	BufferedWriter mbw1 = new BufferedWriter(mfw1);
  	    	mbw1.write("#&%Test#&%Test#&%Test#&%Test#&%Test#&%" + mdatecurr + "#&%" + mtimecurr + "#&%" + mtimestamp + "#&%Test#&%Test#&%");
  	    	mbw1.close();
		}
     	catch(Exception e)
     	{
     		
     	}
     }
     
     //clear all data
     public void deletecalen()
     {
    	 Uri evuri = CalendarContract.Calendars.CONTENT_URI;
     	 Cursor result = getActivity().getContentResolver().query(evuri, new String[] {CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
     	 while (result.moveToNext()) 
     	 {
     		 if(result.getString(2).equals("Medduler"))
     		 {
     			 long calid = result.getLong(0);
     			 Uri deleteUri = ContentUris.withAppendedId(evuri, calid);
        		 getActivity().getContentResolver().delete(deleteUri, null, null);
     		 }
     	 }            
     	 File filedelete = new File(Environment.getExternalStorageDirectory() + "/Medduler/");
     	 DeleteRecursive(filedelete);
     	 refresh();
     }
     
     private void DeleteRecursive(File fileOrDirectory) 
     {
    	 if (fileOrDirectory.isDirectory())
    	 for (File child : fileOrDirectory.listFiles())
    	 {
    		 child.delete();
    	     DeleteRecursive(child);
    	 }
    	 fileOrDirectory.delete();
     }
       
}