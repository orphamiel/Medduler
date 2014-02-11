package com.orphamiel.medduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    PagerTitleStrip viewpagetitl;
    private String intenturi ="";
    static int qrclickcount = 1;

    public void onResume()
    {
    	super.onResume();
    	refreshactivity();
    }
    
    //called at end of writeoffqr and ftpdownload
    public void refreshactivity()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String latestfile = prefs.getString("latestfile", "");
		if (latestfile!= null&&!latestfile.equals(""))
		{
			loadasync task = new loadasync();
			task.execute(new String[] {"nothing"}); 
		}
		else
		{
			//this means the appointment hasnt finish loading/reloading
		}
    }   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("latestfile").commit();
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setPageMargin(2);
        mViewPager.setPageMarginDrawable(R.color.border);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //set immediate fragment to number 2
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() 
        {
            @Override
            public void onPageSelected(int position) 
            {
            	
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) 
            {
            	
            }

            @Override
            public void onPageScrollStateChanged(int state) 
            {
            	//hide keyboard if not dragging and if the state is idle
            	if (state == ViewPager.SCROLL_STATE_IDLE && state!= ViewPager.SCROLL_STATE_DRAGGING)
            	{
            		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
            	}
            }
        });        
        
        /*
         * This section is to hook the intent when called upon by a file manager
         */
        final Intent intent = getIntent();  
        final String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action))
        {
        	intenturi = intent.getData().toString();
        	//TODO for web  : uri = intent.getData().getQueryParameter("var");
        	String[] filestring = new String[]{};
        	try
        	{
        		filestring = intenturi.split("file:///");
        	}
        	catch(Exception e)
        	{
        		Log.e("uri", "uri was" + intenturi);
        	}           
            File tempintent = new File("nothing");
            try
            {
            	tempintent = new File(filestring[1]);
            }
            catch (Exception e)
            {
            	tempintent = new File(intenturi);
            }
            final File fileintent = tempintent;
            Boolean intentfail= false;
            String filename = "";
            try
            {
            	filename = filestring[1].substring(filestring[1].length()-23, filestring[1].length());
            }
            catch (Exception e)
            {
            	Log.e("maincaughtfileman", "filename was " + filename + " uri was" + intenturi, e);
            	intentfail = true;
            }
            final StringBuilder text = new StringBuilder();
            try 
       	 	{	
       	     	BufferedReader br = new BufferedReader(new FileReader(fileintent));
       	     	String line;
       	     	
       	     	while ((line = br.readLine()) != null) 
       	     	{
       	     		text.append(line);
       	     	}
       	     	br.close();
       	 	}
       	 	catch (IOException e) 
       	 	{
       	 		Log.e("maincaughtfileman", "could not read from file" + filename, e);
       	 	}
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
            	Log.e("maincaughtfileman", "Invalid text from" + filename, e);
            	intentfail = true;
            }
            final String apptdate2 = apptdate;
            final String filename2 = filename;
            if (intentfail != true)
            {
            	final Dialog singledia = new Dialog(this);
        		singledia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
        		singledia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
        		singledia.setContentView(R.layout.custduodiag);
        		((TextView)singledia.findViewById(R.id.diagtitle2)).setText(filename);
        		((TextView)singledia.findViewById(R.id.diagmessage2)).setText(fullmessagestring);
        		singledia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        		singledia.findViewById(R.id.pos_butt).setOnClickListener(new OnClickListener() 
        		{
                    @Override
                    public void onClick(View v) 
                    {
                    	singledia.dismiss();
                    	final Dialog singledia2 = new Dialog(MainActivity.this);
                		singledia2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                		singledia2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                		singledia2.setContentView(R.layout.custduodiag);
                		((TextView)singledia2.findViewById(R.id.diagtitle2)).setText(filename2);
                		((TextView)singledia2.findViewById(R.id.diagmessage2)).setText("How should " + filename2 +" be shared?");
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
                            	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileintent));
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
                            	if(text.toString().getBytes().length<=1000)
                            	{                     		
                            		String encr = new String();
									try 
									{
										encr = AES.encrypt(text.toString(), AES.aeskey);
									}
									catch (Exception e) 
									{
										Log.e("maincaughtfileman", "Failed to encrypt " + text.toString(), e);
									}
									final String qrtext = encr;
                            		Bitmap gend = BarcodeGen.datatoqr(encr, 400, 400);
                                	final Dialog picdia = new Dialog(MainActivity.this);
                            		picdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
                            		picdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
                            		picdia.setContentView(R.layout.qrsharediag);
                            		((TextView)picdia.findViewById(R.id.qrdiagtitle)).setText(filename2);
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
                        	        newFragment.show(getSupportFragmentManager(), "dialog");  
                            	}
                            }
                		});
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
        		//end integration2.0
            }
            else
            {
            	DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Invalid file");
    	    	args.putString("text", fullmessagestring);
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");           	
            }
        }
        /*
         * End hook
         */       
    }
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter 
    {

        public SectionsPagerAdapter(FragmentManager fm) 
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) 
        {
        	switch (position) 
        	{
            case 0:
            	Fragment fragment = new qrfragment();
            	return fragment;
            case 1:
            	ListFragment fragment2 = new listfragment();
            	return fragment2;
            default:
                return null;
        	}
        }

        @Override
        public int getCount() 
        {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) 
        {
            switch (position) 
            {
                case 0:
                    return "SCAN";
                case 1:
                    return "APPOINTMENTS";
            }
            return null;
        }
    }
    
    /**
     * Writes Offline QR to storage
     */
    public class writeoffqr extends AsyncTask<String, Void, Integer>
    {
    	private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    	
    	protected void onPreExecute() 
		{
		     this.dialog.setMessage("Writing file...");
		     this.dialog.show();
		     this.dialog.setCancelable(false);
		}
    	
    	@Override
    	@SuppressWarnings("unchecked")
	    protected Integer doInBackground(String... writeinfo) 
	    {
    		try 
			{
    			File file1 = new File(Environment.getExternalStorageDirectory() + "/Medduler/", writeinfo[0]+".medduler");
				FileWriter fw1 = new FileWriter(file1.getAbsoluteFile());
				BufferedWriter bw1 = new BufferedWriter(fw1);
				bw1.write(writeinfo[1]);
				bw1.close();
				File file = getBaseContext().getFileStreamPath("Medduler.bin");
				//check if medduler.bin exists and create or read from existing file and save
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
				Log.e("maincaughtwrite", "File not found when writing " + writeinfo[0], e);
				return 1;
			} 
			catch (IOException e) 
			{
				Log.e("maincaughtwrite", "Can't write to file " + writeinfo[0], e);
				return 2;
			}    	
	    }
    	
    	 protected void onPostExecute(final Integer result) 
 	     {
    		if (this.dialog.isShowing()) 
	    	{
	    	      this.dialog.dismiss();
	    	}
 	    	if (result==0)
 	    	{
 	    		Toast.makeText(getApplicationContext(), "Appointment saved!", Toast.LENGTH_LONG).show();
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
    	    	args.putString("text", "Unable to write to the file");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
 	    	}
 	    	else
 	    	{
 	    		Log.e("maincaughtwrite", "Error at writing file in async");
 	    	}
 	    	refreshactivity();
 	    }
    }
    
    public class ftpdownload extends AsyncTask<String, Void, Integer> 
	{
		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
		
		protected void onPreExecute() 
		{
		     this.dialog.setMessage("Downloading appointment... Please do not close the app");
		     this.dialog.show();
		     this.dialog.setCancelable(false);
		}
		
	    @Override
	    @SuppressWarnings("unchecked")
	    protected Integer doInBackground(String... ftpinfo) 
	    {
  		   	int portnum = 0;
  		   	try
  		   	{
  		   		portnum = Integer.parseInt(ftpinfo[5]);
  		   	}
  		   	catch(NumberFormatException nfe)
  		   	{
 	    	 	 Log.e("maincaughtftpdl", "could not parse portnum from shared" + ftpinfo[5]);
 	    	 	 portnum = 21;
  		   	}
  		   	if (portnum>65535)
  		   	{
  		   		return 5;
  		   	}
  		   	else
  		   	{
  		   		FTPClient ftpClient = new FTPClient();
  		   		try
  		   		{
	     	       ftpClient.connect(InetAddress.getByName(ftpinfo[0]), portnum);
		     	   Boolean logina = ftpClient.login(ftpinfo[3], ftpinfo[4]);
		     	   if (logina==true)
		     	   {
		     	    	ftpClient.enterLocalPassiveMode();
			     	    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			     	    //make dir in case it doesnt exist
			     	    File meddulerdir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");			
			     	    meddulerdir.mkdirs();
			     	    Boolean checkexist = false;
			     	    FTPFile [] a = ftpClient.listFiles(ftpinfo[1]);
			     	    for (int i=0; i<a.length; i++)
			     	    {
			     	    	if((ftpinfo[2]+".medduler").equals(a[i].getName()))
			     	    	{
			     	    		checkexist=true;
			     	    	}
			     	    }
			     	    if(checkexist==true)
			     	    {
			     	    	FileOutputStream desFileStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Medduler/" + ftpinfo[2] +".medduler");
			     	    	ftpClient.retrieveFile(ftpinfo[1], desFileStream);
			     	    	ftpClient.logout();
				     	    ftpClient.disconnect();
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
			     	    			filemap.put(ftpinfo[2]+".medduler", ftpinfo[6]);
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
			     	    			filemap.put(ftpinfo[2]+".medduler", ftpinfo[6]);
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
			     	    else
			     	    {
			     	    	ftpClient.logout();
			     	    	ftpClient.disconnect();
			     	    	return 6;
			     	    }
		     	   }
		     	   else
		     	   {
		     		   Log.e("maincaughtftpdl", "couldn't login due to wrong pw or id");
		     		   return 1;
		     	   }       
  		   		} 
  		   		catch (SocketException e) 
  		   		{
  		   			Log.e("maincaughtftpdl", "Socket Exception");
  		   			return 2;
  		   		} 
  		   		catch (UnknownHostException e) 
  		   		{
  		   			Log.e("maincaughtftpdl", "Host Unknown");
  		   			return 3;
  		   		} 
  		   		catch (IOException e) 
  		   		{
  		   			Log.e("maincaughtftpdl", "Failed to retrieve a file");
  		   			return 4;
  		   		}
  		   	}
	    }
	    
	    protected void onPostExecute(final Integer result) 
	    {
	    	if (this.dialog.isShowing()) 
    		{
    	        this.dialog.dismiss();
    		}
	    	if (result==0)
	    	{
    	        Toast.makeText(getApplicationContext(), "Appointment downloaded into /sdcard/Medduler/", Toast.LENGTH_LONG).show();
	    	}
	    	else if(result==1)
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Wrong ID or Password");
    	    	args.putString("text", "Please check if you've entered the ID or password correctly");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	else if(result==2)
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to download");
    	    	args.putString("text", "Please check if the port has been blocked or entered incorrectly");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	else if(result==3)
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to download");
    	    	args.putString("text", "Server could not be found");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	else if(result==4)
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to download");
    	    	args.putString("text", "The connection was terminated midway");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	else if(result==6)
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to download");
    	    	args.putString("text", "The file does not exist");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	else
	    	{
	    		DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "Failed to download");
    	    	args.putString("text", "Port number cannot be bigger than 65535");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    }

	}
    
    public class ftpcaladd extends AsyncTask<String, Void, Integer> 
	{
    	@Override
		protected void onPreExecute() 
		{

		}
		
	    @Override
	    protected Integer doInBackground(String... ftpinfo) 
	    {
	    	String [] linkarr, linkarr2, linkarr3;	            		
        	linkarr = ftpinfo[0].toString().trim().split("//");
        	linkarr2 = linkarr[1].trim().split("/", 2);
    		linkarr3 = linkarr2[1].trim().split(".medduler", 2);
        	linkarr3 = linkarr3[0].trim().split("/");
        	String filename = linkarr3[linkarr3.length-1] + ".medduler";
        	final StringBuilder text = new StringBuilder();
        	Boolean filecheck = true;
        	final File ftpfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + filename);
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
        		Log.e("maincaughtftp", "Could not read from file " + filename, e);
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
            		addtocalendar(caltitle, caldesc, locatio, calDate.getTimeInMillis(), filename); 
            		return 0;
            	}
            	else
            	{
            		Log.e("maincaughtftp", "Invalid file's text is " + text.toString());
            		return 1;
            	}		            	
        	}
        	else
        	{
        		Log.e("maincaughtftp", "Downloaded file disappeared");
        		return 2;
        	}
	    }
	    
	    @Override
	    protected void onPostExecute(final Integer result) 
	    {
	    	if(result==1)
	    	{
	    		DialogFragment newFragment = new singledialog();
		    	Bundle args = new Bundle();
		    	args.putString("title", "Invalid File");
		    	args.putString("text", "Could not add invalid appointment to calendar");
		    	newFragment.setArguments(args);
		        newFragment.show(getSupportFragmentManager(), "dialog");
	    	}
	    	refreshactivity();
	    }

	}
    
    /**
     * Activated when the big red button is clicked. Disables itself
     * and waits for return in onActivityResult to enable itself
     */
    public void scanNow(View view)
	{
    	((Button)findViewById(R.id.button1)).setEnabled(false);
    	Intent intent = new Intent("com.orphamiel.zxing.client.android.SCAN");
    	intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
    	startActivityForResult(intent, 0);
	}
           
    @Override 
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
    	super.onActivityResult(requestCode, resultCode, intent);
    	((Button)findViewById(R.id.button1)).setEnabled(true);
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if (requestCode == 0) 
	    {
	        if (resultCode == RESULT_OK)
	        {
	            String content = new String();
				try 
				{
					content = AES.decrypt(intent.getStringExtra("SCAN_RESULT"), AES.aeskey);
				} 
				catch (Exception e) 
				{
					Log.e("maincaughtresult", "Failed to decrypt scanned " + intent.getStringExtra("SCAN_RESULT"), e);
				}
				final String contents = content;
	            if (contents.contains("ftp://"))
	            {
	            	boolean wifioption = prefs.getBoolean("wifidown", false);
	            	if (wifioption==true)
	            	{
	            		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	            		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

	            		if (mWifi.isConnected()) 
	            		{
	            			launchftp(contents);
	            		}
	            		else
	            		{
	            	    	DialogFragment wifidia = new WifiDialog();
	            	    	Bundle wifiargs = new Bundle();
	            	    	wifiargs.putString("title", "No Wifi Connection Found");
	            	    	wifiargs.putString("text", "Please connect to a Wifi network");
	            	    	wifidia.setArguments(wifiargs);
	            	        wifidia.show(this.getSupportFragmentManager(), "wifidialog");	            			
	            		}
	            	}
	            	else
	            	{
	            		launchftp(contents);
	            	}
	            	boolean caloption = prefs.getBoolean("calimport", true);
	            	if (caloption==true)
	            	{	
	            		ftpcaladd task = new ftpcaladd();
	            	    task.execute(new String[] {contents});
	            	}
	            	
	            }
	            else if (contents.contains("#&%"))
	            {
	            	String [] arr;
	            	arr = contents.trim().split("#&%");
	            	if (arr.length == 11)
	            	{
	            		//Splitting into related sections
		            	String locatio = arr[4];
		            	String date1 = arr[6];
		            	String time1 = arr[7];
		            	String timestamp1 = arr[8];
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
		            	GregorianCalendar currentdate = new GregorianCalendar();         	
		            	//check if date is after today
		            	if (calDate.after(currentdate)) 
		            	{
		            		boolean saveof = prefs.getBoolean("saveoff", true);
		            		if (saveof==true)
			            	{
		            			//test if there's read and write permission
		            			Boolean test1 = isExternalStorageWritable();
		            			if (test1 == true)
		            			{
		            				File meddulerdir = new File(Environment.getExternalStorageDirectory() + "/Medduler/");			
				        			meddulerdir.mkdirs();				        			
				        			SimpleDateFormat stampFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.US);
				        			Date timestamp = new Date();
					            	try
									{
										timestamp = stampFormat.parse(timestamp1);
									} 
					            	catch (ParseException e)
									{
										Log.e("maincaughtresult", "could not parse date "+ timestamp1, e);
									}
					            	//get year
					            	SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.US);
					            	String yearfin = yearFormat.format(timestamp);
					            	//get month
					            	SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US);
					            	String monthfin = monthFormat.format(timestamp);
					            	//get day
					            	SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
					            	String dayfin = dayFormat.format(timestamp);
					            	//get hour
					            	SimpleDateFormat hourFormat = new SimpleDateFormat("kk", Locale.US);
					            	String hourfinal = hourFormat.format(timestamp);
					            	//get minute
					            	SimpleDateFormat minuteFormat = new SimpleDateFormat("mm", Locale.US);
					            	String minutefin = minuteFormat.format(timestamp);
					            	//get seconds
					            	SimpleDateFormat secondFormat = new SimpleDateFormat("ss", Locale.US);
					            	String secondfin = secondFormat.format(timestamp);
					            	//end of conversion - combine strings for filename
					            	final String namestring = yearfin + monthfin + dayfin + hourfinal + minutefin + secondfin;
					            	File file1 = new File(Environment.getExternalStorageDirectory() + "/Medduler/", namestring+".medduler");
					            	if (file1.exists())
				        			{
					            		//first one to save, second one to know which was clicked
					            		Boolean existfilesave = prefs.getBoolean("writeexist", false);
					            		Boolean existfilesaves = prefs.getBoolean("writeexists", false);
					            		if (existfilesave==true)
					            		{
					            			if (existfilesaves==true)
					            			{
					            				writeoffqr task = new writeoffqr();
								            	task.execute(new String[] {namestring, contents});
					            			}
					            		}
					            		else
					            		{
					            			final Dialog check2dia = new Dialog(this);
					            			check2dia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
					            			check2dia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
					            			check2dia.setContentView(R.layout.custcheckdiag2);
					            			((TextView)check2dia.findViewById(R.id.check2title)).setText("File already exists");
					            			((TextView)check2dia.findViewById(R.id.check2message)).setText("Do you want to replace the file? \n\nThis setting can be changed in Settings\n");
					            			final CheckBox inputcheck = (CheckBox)check2dia.findViewById(R.id.check2box);
					            			inputcheck.setText("Remember my choice");
					            			check2dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					            			check2dia.findViewById(R.id.check2posbutt).setOnClickListener(new OnClickListener() 
					            			{
					            	            @Override
					            	            public void onClick(View v) 
					            	            {
					            	            	check2dia.dismiss();
					            	            	if(inputcheck.isChecked())
					            	            	{
					            	            		//first one to save, second one to know which was clicked
					            	            		prefs.edit().putBoolean("writeexist", true).commit();
					            	            		prefs.edit().putBoolean("writeexists", true).commit();
					            	            	}
					            	            	writeoffqr task = new writeoffqr();
					            	            	task.execute(new String[] {namestring, contents});
					            	            }
					            			});
					            			check2dia.findViewById(R.id.check2negbutt).setOnClickListener(new OnClickListener() 
					            			{
					            	            @Override
					            	            public void onClick(View v) 
					            	            {
					            	            	check2dia.dismiss();
					            	            	if(inputcheck.isChecked())
					            	            	{		            		        		   
					            	            		 prefs.edit().putBoolean("writeexist", true).commit();
					            	            		 prefs.edit().putBoolean("writeexists", false).commit();
					            	            	}
					            	            }
					            			});
					            			check2dia.setCancelable(false);
					            			check2dia.show();
					            		}
				        			}
					            	else
				        			{
					            		writeoffqr task = new writeoffqr();
						            	task.execute(new String[] {namestring, contents});
				        			}
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
			            	}
			        		//end of writing file - check if save to calendar is enabled and save
		            		boolean caloption = prefs.getBoolean("calimport", true);
			            	if (caloption==true)
			            	{			
			            		String caltitle = date1 + " appointment";
			            		String caldesc = issue1 + " appointment with " + docname + " at " + locatio + " on " + date1 + " at " + time1;
			            		addtocalendar2(caltitle, caldesc, locatio, calDate.getTimeInMillis());        		
			            	}
		            	}
		            	else
		            	{
		            		DialogFragment newFragment = new singledialog();
		        	    	Bundle args = new Bundle();
		        	    	args.putString("title", "Missed Appointment");
		        	    	args.putString("text", "The appointment date has already passed");
		        	    	newFragment.setArguments(args);
		        	        newFragment.show(getSupportFragmentManager(), "misseddialog");
		            	}	        
	            	}
	            	else
	            	{
	            		Toast.makeText(getApplicationContext(), "Invalid QR code", Toast.LENGTH_LONG).show();
	            	}           	    	
	            }
	            else
	            {
	        		startActivityForResult(intent, 0);
	        		Toast.makeText(getApplicationContext(), "Invalid QR code", Toast.LENGTH_LONG).show();
	            }
	        }
	        else if (resultCode == RESULT_CANCELED)
	        {
	            // Handle cancel
	        }
	    }
	}
        
    public void launchftp(String contents2)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (isOnline()==true)
		{
    		String usernam = prefs.getString("usern", "medduler");
    		String passwo = prefs.getString("passw", "medpass");
    		String port = prefs.getString("portn", "");	            		
    		String [] linkarr, linkarr2, linkarr3;	            		
        	linkarr = contents2.trim().split("//");
        	//Protocol type is linkarr[0]
        	linkarr2 = linkarr[1].trim().split("/", 2);
        	//Hostname is + linkarr2[0]
    		String hostname = linkarr2[0];
    		linkarr3 = linkarr2[1].trim().split(".medduler", 2);
        	linkarr3 = linkarr3[0].trim().split("/");
        	//Name of file is linkarr3[linkarr3.length-1]
    		ftpdownload task = new ftpdownload();
    	    task.execute(new String[] {hostname, "/"+linkarr2[1], linkarr3[linkarr3.length-1], usernam, passwo, port, contents2});
		}
		else
		{
			DialogFragment wifidia = new WifiDialog();
	    	Bundle wifiargs = new Bundle();
	    	wifiargs.putString("title", "Unable to download appointment");
	    	wifiargs.putString("text", "Please check if you have a connection to the internet or connect to a Wi-Fi network");
	    	wifidia.setArguments(wifiargs);
	        wifidia.show(getSupportFragmentManager(), "wifidialog2");
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
        
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle item selection
	    switch (item.getItemId()) 
	    {
	    case R.id.item1:
	    	Intent options1 = new Intent(MainActivity.this, calendaroptions.class);
			startActivity(options1);
	        return true;
	    case R.id.item4:
	    	Intent options4 = new Intent(MainActivity.this, appoptions.class);
			startActivity(options4);
	        return true;
	    case R.id.item3:
	    	contactme();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
        
    public void contactme()
    {
    	Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    	sendIntent.setType("plain/text");
    	sendIntent.setData(Uri.parse("orphamiel2@gmail.com"));
    	sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
    	sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Medduler");
    	try
    	{
    		startActivity(sendIntent);
    	}
    	catch(ActivityNotFoundException e)
    	{
    		DialogFragment newFragment = new singledialog();
	    	Bundle args = new Bundle();
	    	args.putString("title", "Gmail could not be found");
	    	args.putString("text", "Gmail needs to be installed for this action");
	    	newFragment.setArguments(args);
	        newFragment.show(getSupportFragmentManager(), "dialog");
    	}
    }
    
    public class loadasync extends AsyncTask<String, Void, Integer>
    {   
    	int success = 0;
    	LinearLayout alltext = (LinearLayout)findViewById(R.id.textmerge);
    	TextView tv1 = (TextView) findViewById(R.id.textView2);
    	TextView tv2 = (TextView) findViewById(R.id.textView3);
    	TextView tv3 = (TextView) findViewById(R.id.textView4);
    	TextView tv4 = (TextView) findViewById(R.id.textView5);
    	TextView tv5 = (TextView) findViewById(R.id.textView6);
    	TextView tv6 = (TextView) findViewById(R.id.textView7);
    	TextView tv7 = (TextView) findViewById(R.id.textView8);
    	TextView tv8 = (TextView) findViewById(R.id.textView9);
    	int filefail = 0;
    	int filetot = 0;
    	
    	protected void onPreExecute() 
 		{    		    		
			tv3.setGravity(Gravity.CENTER);
			tv3.setText("Loading...");
	        tv1.setText("");	        
	        tv2.setText(""); 		         	        
	        tv4.setText("");	        
	        tv5.setText("");	        
	        tv6.setText("");	        
	        tv7.setText("");	        
	        tv8.setText("");
 		}    	
    	
    	@Override
	    protected Integer doInBackground(String... writeinfo) 
	    {
    		try
			{
				Thread.sleep(getResources().getInteger(R.integer.fliptime)/2);
			} 
    		catch (InterruptedException e1)
			{
    			Log.e("maincaughtload", "Could not sleep thread", e1);
			}
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
         		if (numoffiles!=0)
         		{
         			final ArrayList<String> filelist = new ArrayList<String>();
         			final ArrayList<Date> datelist = new ArrayList<Date>();
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
         		    	    Log.e("maincaughtload", "could not read from file refresh " + fileArray[numoffiles-1], e);
         		    	}
         				String [] textarr;
         				int fail = 0;
         				try
         	            {     					
         	            	textarr = text.toString().trim().split("#&%");
         	            	apptdate = textarr[6] + " " + textarr[7];
         	            }
         				catch(Exception e)
         	            {
         					fail++;
         					filefail++;
         	            }
         				if (fail==0)
         				{
         					filelist.add(apptdate+searchfile.getName());
         					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
         				    Date convertedDate = new Date();
         				    Date currentDate = new Date();
         				    try 
         				    {
         				        convertedDate = dateFormat.parse(apptdate);
         				    } 
         				    catch (ParseException e) 
         				    {
         				    	Log.e("maincaughtload", "could not parse " + apptdate + "to date", e);
         				    }
         				    if (convertedDate.after(currentDate))
        				    {
            					datelist.add(convertedDate);
            					success++;
        				    }
         				}
         				else
         				{
         					//if it failed we reset it
         					fail=0;
         				}     				
         				numoffiles--;     				
         			}
         			int numoffiles2 = filelist.size();   
         			if (filefail==filetot)
      				{
         				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
         				prefs.edit().putString("latestfile", "noappt").commit();
         				return 1;
      				}
         			else
         			{
         				Collections.sort(datelist);
             			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
             			Date earliestdate = new Date();
             			Boolean noupappt = false;
             			try
             			{
             				earliestdate = datelist.get(0);
             			}
             			catch (Exception e)
             			{
             				noupappt = true;
             			}
             			if(noupappt == false)
             			{
             				String earlieststring = dateFormat.format(earliestdate);
                 			String hhtime = earlieststring.substring(11, 13);
                 			int convertedint = 0;   			
                 			try 
                 	    	{
                 	    	    convertedint = Integer.parseInt(hhtime);
                 	    	} 
                 	    	catch(NumberFormatException nfe) 
                 	    	{
                 	    		Log.e("maincaughtload", "Failed to parse hhtime " + hhtime);
                 	    	}
                 			String convertedfinal = "";
                 			convertedfinal = Integer.toString(convertedint);
                 			String latestfile = "";
                 			int testfordup = 0;
             				for (int i=0; i<numoffiles2; i++)
                 			{
                 				String temp1 = filelist.get(i);
                 				String j = earlieststring.substring(0, 11) + convertedfinal + earlieststring.substring((earlieststring.length())-6, earlieststring.length());
                 				if (temp1.contains(j.toUpperCase(Locale.ENGLISH)))
                 				{
                 					latestfile = temp1.substring((temp1.length())-23, temp1.length());
                 					testfordup++;
                 				}
                 			}
             				if (testfordup>1)
             				{
             					runOnUiThread(new Runnable() 
                        		{
                        		     public void run() 
                        		     {
                        		    	 Toast.makeText(getApplicationContext(), "Warning : You have another appointment during your next appointment.", Toast.LENGTH_SHORT).show();
                        		     }
                        		});
             				}
             				//Add to shared preference after list is done loading
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
             				prefs.edit().putString("latestfile", latestfile).commit();
             				final StringBuilder text = new StringBuilder();
             				try 
             		    	{
             		    		 File file = new File(Environment.getExternalStorageDirectory() + "/Medduler/", latestfile);
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
             		    	    Log.e("maincaughtload", "Could not read from file " + latestfile, e);
             		    	}
             				String [] textarr;
             				String latepatn = "", latedocn = "", latedepa = "", lateissu = "", latedate = "";
                 			String latetime = "", lateloca = "", laterema = "";
             				try
             	         	{
             	         		textarr = text.toString().trim().split("#&%");
             	         		latepatn = textarr[1] + " " + textarr[2];
             	         		latedocn = textarr[10];
             	         		latedepa = textarr[3];
             	         		lateissu = textarr[9];
             	         		latedate = textarr[6];
             	         		latetime = textarr[7];
             	         		lateloca = textarr[4];
             	         		laterema = textarr[5];
             	         	}
             	         	catch(Exception e)
             	         	{
             	         		Log.e("maincaughtload", "Invalid text from " + latestfile, e);
             	         	}
             				final String latepatn2 = "Name : " + latepatn;
             				final String latedocn2 = "Doctor : " + latedocn;
                 			final String latedepa2 = "Department : " + latedepa;
                 			final String lateissu2 = "Issue : " + lateissu;
                 			final String latedate2 = "Date : " + latedate;
                 			final String latetime2 = "Time : " + latetime;
                 			final String lateloca2 = "Location : " + lateloca;
                 			final String laterema2 = "Remarks : " + laterema;
                    		runOnUiThread(new Runnable() 
                    		{
                    		     public void run() 
                    		     {
                    		    	 TextView tv1 = (TextView) findViewById(R.id.textView2);
                    		         tv1.setText(latepatn2);
                    		         TextView tv2 = (TextView) findViewById(R.id.textView3);
                    		         tv2.setText(latedocn2);
                    		         TextView tv3 = (TextView) findViewById(R.id.textView4);
                    		         tv3.setGravity(Gravity.NO_GRAVITY);
                    		         tv3.setTextSize(14);
                    		         tv3.setText(latedepa2);        		         
                    		         TextView tv4 = (TextView) findViewById(R.id.textView5);
                    		         tv4.setText(lateissu2);
                    		         TextView tv5 = (TextView) findViewById(R.id.textView6);
                    		         tv5.setText(latedate2);
                    		         TextView tv6 = (TextView) findViewById(R.id.textView7);
                    		         tv6.setText(latetime2);
                    		         TextView tv7 = (TextView) findViewById(R.id.textView8);
                    		         tv7.setText(lateloca2);
                    		         TextView tv8 = (TextView) findViewById(R.id.textView9);
                    		         tv8.setText(laterema2);
                    		    }
                    		});
             				return 0;
             			}
             			else
             			{
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
             				prefs.edit().putString("latestfile", "noupappt").commit();
             				return 2;
             			}   
         			}     			 		
         		}
         		else
         		{
         			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
     				prefs.edit().putString("latestfile", "noappt").commit();
         			return 1;
         		}
        	}
     		else
     		{
     			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
 				prefs.edit().putString("latestfile", "noappt").commit();
     			return 1;
     		}     		
	    }
    	
    	protected void onPostExecute(final Integer result) 
     	{
    		if (result==0)
    		{
    			
    		}
    		else if (result==1)
    		{
    			TextView tv1 = (TextView) findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no appointments");
    		}
    		else if (result==2)
    		{
    			TextView tv1 = (TextView) findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no upcoming appointments");
    		}
    		else
    		{
    			Log.e("maincaughtload", "Weird error");
    		}
     	}
    }
    
    //caled when refresh button is clicked. adds animaton
    public class loadasync2 extends AsyncTask<String, Void, Integer>
    {   
    	int success = 0;
    	LinearLayout alltext = (LinearLayout)findViewById(R.id.textmerge);
    	TextView tv1 = (TextView) findViewById(R.id.textView2);
    	TextView tv2 = (TextView) findViewById(R.id.textView3);
    	TextView tv3 = (TextView) findViewById(R.id.textView4);
    	TextView tv4 = (TextView) findViewById(R.id.textView5);
    	TextView tv5 = (TextView) findViewById(R.id.textView6);
    	TextView tv6 = (TextView) findViewById(R.id.textView7);
    	TextView tv7 = (TextView) findViewById(R.id.textView8);
    	TextView tv8 = (TextView) findViewById(R.id.textView9);
    	int filefail = 0;
    	int filetot = 0;
    	
    	protected void onPreExecute() 
 		{    		    		
			tv3.setGravity(Gravity.CENTER);
			tv3.setText("Loading...");
	        tv1.setText("");	        
	        tv2.setText(""); 		         	        
	        tv4.setText("");	        
	        tv5.setText("");	        
	        tv6.setText("");	        
	        tv7.setText("");	        
	        tv8.setText("");
	        //start animation
    		AnimatorSet ani = (AnimatorSet)(AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.card_flip));
    		ani.setTarget(alltext); 
    		ani.start();
 		}    	
    	
    	@Override
	    protected Integer doInBackground(String... writeinfo) 
	    {
    		try
			{
				Thread.sleep(getResources().getInteger(R.integer.fliptime)/2);
			} 
    		catch (InterruptedException e1)
			{
    			Log.e("maincaughtload", "Could not sleep thread", e1);
			}
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
         		if (numoffiles!=0)
         		{
         			final ArrayList<String> filelist = new ArrayList<String>();
         			final ArrayList<Date> datelist = new ArrayList<Date>();
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
         		    	    Log.e("maincaughtload", "could not read from file refresh " + fileArray[numoffiles-1], e);
         		    	}
         				String [] textarr;
         				int fail = 0;
         				try
         	            {     					
         	            	textarr = text.toString().trim().split("#&%");
         	            	apptdate = textarr[6] + " " + textarr[7];
         	            }
         				catch(Exception e)
         	            {
         					fail++;
         					filefail++;
         	            }
         				if (fail==0)
         				{
         					filelist.add(apptdate+searchfile.getName());
         					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
         				    Date convertedDate = new Date();
         				    Date currentDate = new Date();
         				    try 
         				    {
         				        convertedDate = dateFormat.parse(apptdate);
         				    } 
         				    catch (ParseException e) 
         				    {
         				    	Log.e("maincaughtload", "could not parse " + apptdate + "to date", e);
         				    }
         				    if (convertedDate.after(currentDate))
        				    {
            					datelist.add(convertedDate);
            					success++;
        				    }
         				}
         				else
         				{
         					//if it failed we reset it
         					fail=0;
         				}     				
         				numoffiles--;     				
         			}
         			int numoffiles2 = filelist.size();   
         			if (filefail==filetot)
      				{
         				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
         				prefs.edit().putString("latestfile", "noappt").commit();
         				return 1;
      				}
         			else
         			{
         				Collections.sort(datelist);
             			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
             			Date earliestdate = new Date();
             			Boolean noupappt = false;
             			try
             			{
             				earliestdate = datelist.get(0);
             			}
             			catch (Exception e)
             			{
             				noupappt = true;
             			}
             			if(noupappt == false)
             			{
             				String earlieststring = dateFormat.format(earliestdate);
                 			String hhtime = earlieststring.substring(11, 13);
                 			int convertedint = 0;   			
                 			try 
                 	    	{
                 	    	    convertedint = Integer.parseInt(hhtime);
                 	    	} 
                 	    	catch(NumberFormatException nfe) 
                 	    	{
                 	    		Log.e("maincaughtload", "Failed to parse hhtime " + hhtime);
                 	    	}
                 			String convertedfinal = "";
                 			convertedfinal = Integer.toString(convertedint);
                 			String latestfile = "";
                 			int testfordup = 0;
             				for (int i=0; i<numoffiles2; i++)
                 			{
                 				String temp1 = filelist.get(i);
                 				String j = earlieststring.substring(0, 11) + convertedfinal + earlieststring.substring((earlieststring.length())-6, earlieststring.length());
                 				if (temp1.contains(j.toUpperCase(Locale.ENGLISH)))
                 				{
                 					latestfile = temp1.substring((temp1.length())-23, temp1.length());
                 					testfordup++;
                 				}
                 			}
             				if (testfordup>1)
             				{
             					runOnUiThread(new Runnable() 
                        		{
                        		     public void run() 
                        		     {
                        		    	 Toast.makeText(getApplicationContext(), "Warning : You have another appointment during your next appointment.", Toast.LENGTH_SHORT).show();
                        		     }
                        		});
             				}
             				//Add to shared preference after list is done loading
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
             				prefs.edit().putString("latestfile", latestfile).commit();
             				final StringBuilder text = new StringBuilder();
             				try 
             		    	{
             		    		 File file = new File(Environment.getExternalStorageDirectory() + "/Medduler/", latestfile);
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
             		    	    Log.e("maincaughtload", "Could not read from file " + latestfile, e);
             		    	}
             				String [] textarr;
             				String latepatn = "", latedocn = "", latedepa = "", lateissu = "", latedate = "";
                 			String latetime = "", lateloca = "", laterema = "";
             				try
             	         	{
             	         		textarr = text.toString().trim().split("#&%");
             	         		latepatn = textarr[1] + " " + textarr[2];
             	         		latedocn = textarr[10];
             	         		latedepa = textarr[3];
             	         		lateissu = textarr[9];
             	         		latedate = textarr[6];
             	         		latetime = textarr[7];
             	         		lateloca = textarr[4];
             	         		laterema = textarr[5];
             	         	}
             	         	catch(Exception e)
             	         	{
             	         		Log.e("maincaughtload", "Invalid text from " + latestfile, e);
             	         	}
             				final String latepatn2 = "Name : " + latepatn;
             				final String latedocn2 = "Doctor : " + latedocn;
                 			final String latedepa2 = "Department : " + latedepa;
                 			final String lateissu2 = "Issue : " + lateissu;
                 			final String latedate2 = "Date : " + latedate;
                 			final String latetime2 = "Time : " + latetime;
                 			final String lateloca2 = "Location : " + lateloca;
                 			final String laterema2 = "Remarks : " + laterema;
                    		runOnUiThread(new Runnable() 
                    		{
                    		     public void run() 
                    		     {
                    		    	 TextView tv1 = (TextView) findViewById(R.id.textView2);
                    		         tv1.setText(latepatn2);
                    		         TextView tv2 = (TextView) findViewById(R.id.textView3);
                    		         tv2.setText(latedocn2);
                    		         TextView tv3 = (TextView) findViewById(R.id.textView4);
                    		         tv3.setGravity(Gravity.NO_GRAVITY);
                    		         tv3.setTextSize(14);
                    		         tv3.setText(latedepa2);        		         
                    		         TextView tv4 = (TextView) findViewById(R.id.textView5);
                    		         tv4.setText(lateissu2);
                    		         TextView tv5 = (TextView) findViewById(R.id.textView6);
                    		         tv5.setText(latedate2);
                    		         TextView tv6 = (TextView) findViewById(R.id.textView7);
                    		         tv6.setText(latetime2);
                    		         TextView tv7 = (TextView) findViewById(R.id.textView8);
                    		         tv7.setText(lateloca2);
                    		         TextView tv8 = (TextView) findViewById(R.id.textView9);
                    		         tv8.setText(laterema2);
                    		    }
                    		});
             				return 0;
             			}
             			else
             			{
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
             				prefs.edit().putString("latestfile", "noupappt").commit();
             				return 2;
             			}   
         			}     			 		
         		}
         		else
         		{
         			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
     				prefs.edit().putString("latestfile", "noappt").commit();
         			return 1;
         		}
        	}
     		else
     		{
     			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
 				prefs.edit().putString("latestfile", "noappt").commit();
     			return 1;
     		}     		
	    }
    	
    	protected void onPostExecute(final Integer result) 
     	{
    		if (result==0)
    		{
    			
    		}
    		else if (result==1)
    		{
    			TextView tv1 = (TextView) findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no appointments");
    		}
    		else if (result==2)
    		{
    			TextView tv1 = (TextView) findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no upcoming appointments");
    		}
    		else
    		{
    			Log.e("maincaughtload", "Weird error");
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
    public void addtocalendar(String eventtitle, String eventdesc, String eventloc, long startTime, String filenam)
    {
    	Uri evuri = CalendarContract.Calendars.CONTENT_URI;
    	long calid = 0, medcalcount = 0;
    	Cursor result = getContentResolver().query(evuri, new String[] {CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
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
       	    	//starttest
       	    	File file = getBaseContext().getFileStreamPath("calmap.bin");
	  	       	if(file.exists())
	  	       	{
	       	    	try
	  	       		{
		     	    	HashMap<String, Long> calmap;
			     	    FileInputStream fos = getApplicationContext().openFileInput("calmap.bin");
			     	    ObjectInputStream inputstream = new ObjectInputStream(fos);
			     	    calmap = (HashMap<String, Long>) inputstream.readObject();
			     	    inputstream.close();
			     	    calmap.put(filenam, id);
			     	    FileOutputStream fos2 = getApplicationContext().openFileOutput("calmap.bin", Context.MODE_PRIVATE);   
			     	    ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
			     	    outputStream.writeObject(calmap);
			     	    outputStream.flush();
			     	    outputStream.close();
		          	}
		     	    catch (Exception e)
		          	{
		     	    	Log.e("IO Exception3", e.getMessage());
		          	}
	  	       	}
	  	       	else
	  	       	{
	  	    	  	try
	  	    	  	{
	  	    	  		HashMap<String, Long> calmap = new HashMap<String, Long>();
	  	    	  		calmap.put(filenam, id);
	  	    	  		FileOutputStream fos2 = getApplicationContext().openFileOutput("calmap.bin", Context.MODE_PRIVATE);   
	  	    	  		ObjectOutputStream outputStream = new ObjectOutputStream(fos2);
	  	    	  		outputStream.writeObject(calmap);
	  	    	  		outputStream.flush();
	  	    	  		outputStream.close();
	  	    	  	}
	  	    	  	catch (Exception e)
	  	    	  	{
	  	    	  		Log.e("IO Exception3", e.getMessage());
	  	    	  	}
	  	       	}
	  	       	//endtest

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
        	addtocalendar(eventtitle, eventdesc, eventloc, startTime, filenam);
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
    public void addtocalendar2(String eventtitle, String eventdesc, String eventloc, long startTime)
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
        	addtocalendar2(eventtitle, eventdesc, eventloc, startTime);
    	}	
    }
        
    public void refreshbutton(View view)
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String latestfile = prefs.getString("latestfile", "");
		if (latestfile!= null&&!latestfile.equals(""))
		{
			loadasync2 task = new loadasync2();
			task.execute( new String[] {"nothing"}); 
		}
		else
		{
			//this means the appointment hasnt finish loading/reloading
		}
    }
            
    public void sharebutton(View view)
    {
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String latestfile = prefs.getString("latestfile", "");
		if (latestfile!= null&&!latestfile.equals(""))
		{
			if ((!latestfile.equals("noappt"))&&(!latestfile.equals("noupappt")))
			{
				Boolean catcherror = false;
				final StringBuilder text = new StringBuilder();
				final File sendfile = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + latestfile);
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
		   	 		Log.e("maincaughtshare", "Could not read from file " + latestfile, e);
		   	 		catcherror = true;
		   	 	}
				String [] textarr;
				String apptdate = "";
				try
		    	{
		    		 textarr = text.toString().trim().split("#&%");
		    	 	 apptdate = textarr[6];
		    	}
				catch(Exception e)
				{
					Log.e("maincaughtshare", "Could not parse array sharebutt", e);
					catcherror = true;
				}
				final String apptdate2 = apptdate; 
				final String latestfile2 = latestfile;
		   	 	if (catcherror == false)
		   	 	{
		   	 		final Dialog singledia2 = new Dialog(this);
		   	 		singledia2.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		   	 		singledia2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
		   	 		singledia2.setContentView(R.layout.custduodiag);
		   	 		((TextView)singledia2.findViewById(R.id.diagtitle2)).setText(latestfile);
		   	 		((TextView)singledia2.findViewById(R.id.diagmessage2)).setText("How should " + latestfile +" be shared?");
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
									Log.e("maincaughtshare", "Failed to encrypt " + text.toString(), e);
								} 
								final String qrtext = encr;
		   	 					Bitmap gend = BarcodeGen.datatoqr(encr, 400, 400);
		   	 					final Dialog picdia = new Dialog(MainActivity.this);
		   	 					picdia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		   	 					picdia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
		   	 					picdia.setContentView(R.layout.qrsharediag);
		   	 					((TextView)picdia.findViewById(R.id.qrdiagtitle)).setText(latestfile2);
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
		   	 					newFragment.show(getSupportFragmentManager(), "dialog");  
		   	 				}
		   	 				//end new share button
                    	}
        			});
        			singledia2.show();
		   	 	}
		   	 	else
		   	 	{
		   	 		refreshactivity();
		   	 	}
			}
			else if(latestfile.equals("noupappt"))
			{
				DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "No upcoming appointments");
    	    	args.putString("text", "You have no upcoming appointments to share!");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
			}
			else
			{
				DialogFragment newFragment = new singledialog();
    	    	Bundle args = new Bundle();
    	    	args.putString("title", "No appointments");
    	    	args.putString("text", "You have no appointments to share!");
    	    	newFragment.setArguments(args);
    	        newFragment.show(getSupportFragmentManager(), "dialog");
			}
		}
		else
		{
			DialogFragment newFragment = new singledialog();
	    	Bundle args = new Bundle();
	    	args.putString("title", "Loading appointments");
	    	args.putString("text", "Appointments have not loaded yet... Please wait");
	    	newFragment.setArguments(args);
	        newFragment.show(getSupportFragmentManager(), "dialog");
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
    		Log.e("maincaught", "Error in parsing " + debugcode + ". Value was " + inpstring);
    	}
    	return convertedint;
    }

	/**
	 * Check if there's permission to write to storage
	 * @return Returns true if there's permission
	 */
  	public static boolean isExternalStorageWritable() 
  	{
  	    String state = Environment.getExternalStorageState();
  	    if (state.equals(Environment.MEDIA_MOUNTED))
  	    {
  	        return true;
  	    }
  	    return false;
  	}
  	
  	/**
  	 * Check if there's a connection to the internet
  	 * @return Returns true if there's a connection
  	 */
  	public boolean isOnline() 
  	{
  	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
  	    if (netInfo != null && netInfo.isConnectedOrConnecting()) 
  	    {
  	        return true;
  	    }
  	    return false;
  	}
  	
}
