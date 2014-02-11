package com.orphamiel.medduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class qrfragment extends Fragment 
{	
	
    public qrfragment() 
    {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.qrscanfrag, container, false);
        Context cont=getActivity();
        new loadasync(cont, rootView).execute();	
        return rootView;
    }
    
    public class loadasync extends AsyncTask<String, Void, Integer>
    {
    	private Context mContext;
    	private View rootView;
    	int success = 0;
    	int filefail = 0;
    	int filetot = 0;
    	
    	public loadasync(Context context, View rootView)
    	{
    		this.mContext=context;
    	    this.rootView=rootView;
    	}
    	
    	protected void onPreExecute() 
 		{
    		TextView tv3 = (TextView) rootView.findViewById(R.id.textView4);
			tv3.setTextSize(16);
			tv3.setGravity(Gravity.CENTER);
			tv3.setText("Loading information...");
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
         		    	     BufferedReader br = new BufferedReader(new FileReader(searchfile));
         		    	     String line;

         		    	     while ((line = br.readLine()) != null) 
         		    	     {
         		    	         text.append(line);
         		    	     }
         		    	     br.close();
         		    	}
         		    	catch (IOException e) 
         		    	{
         		    	    Log.e("qrcaught", "could not read from file" + fileArray[numoffiles-1], e);
         		    	}
         				/**
         				 * Important stuff from here on.
         				 */
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
         				    	Log.e("qrcaught", "could not parse to date " + apptdate);
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
         				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                 	    		Log.e("qrcaught", "failed to convert hhtime" + hhtime);
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
             					getActivity().runOnUiThread(new Runnable() 
                        		{
                        		     public void run() 
                        		     {
                        		    	 Toast.makeText(getActivity().getApplicationContext(), "Warning : You have another appointment during your next appointment.", Toast.LENGTH_SHORT).show();
                        		     }
                        		});
             				}
             				//Add to shared preference after list is done loading
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
             				prefs.edit().putString("latestfile", latestfile).commit();
             				final StringBuilder text = new StringBuilder();
             				try 
             		    	{
             		    		 File file = new File(Environment.getExternalStorageDirectory() + "/Medduler/" + latestfile);
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
             		    	    Log.e("qrcaught", "could not read " + latestfile, e);
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
             	         		Log.e("qrcaught", "invalid text from getting latest" + latestfile, e);
             	         	}
             				final String latepatn2 = "Name : " + latepatn;
             				final String latedocn2 = "Doctor : " + latedocn;
                 			final String latedepa2 = "Department : " + latedepa;
                 			final String lateissu2 = "Issue : " + lateissu;
                 			final String latedate2 = "Date : " + latedate;
                 			final String latetime2 = "Time : " + latetime;
                 			final String lateloca2 = "Location : " + lateloca;
                 			final String laterema2 = "Remarks : " + laterema;
                    		getActivity().runOnUiThread(new Runnable() 
                    		{
                    		     public void run() 
                    		     {
                    		    	 TextView tv1 = (TextView) rootView.findViewById(R.id.textView2);
                    		         tv1.setText(latepatn2);
                    		         TextView tv2 = (TextView) rootView.findViewById(R.id.textView3);
                    		         tv2.setText(latedocn2);
                    		         TextView tv3 = (TextView) rootView.findViewById(R.id.textView4);
                    		         tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.cardnormtext));
                    		         tv3.setGravity(Gravity.NO_GRAVITY);
                    		         tv3.setText(latedepa2);        		         
                    		         TextView tv4 = (TextView) rootView.findViewById(R.id.textView5);
                    		         tv4.setText(lateissu2);
                    		         TextView tv5 = (TextView) rootView.findViewById(R.id.textView6);
                    		         tv5.setText(latedate2);
                    		         TextView tv6 = (TextView) rootView.findViewById(R.id.textView7);
                    		         tv6.setText(latetime2);
                    		         TextView tv7 = (TextView) rootView.findViewById(R.id.textView8);
                    		         tv7.setText(lateloca2);
                    		         TextView tv8 = (TextView) rootView.findViewById(R.id.textView9);
                    		         tv8.setText(laterema2);
                    		    }
                    		});
                    		return 0;
             			}
             			else
             			{
             				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
             				prefs.edit().putString("latestfile", "noupappt").commit();
             				return 2;
             			}
         			}		
         		}
         		else
         		{
         			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
     				prefs.edit().putString("latestfile", "noappt").commit();
         			return 1;
         		}    		
     		}
     		else
     		{
     			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
    			TextView tv1 = (TextView) rootView.findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) rootView.findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no appointments");
    		}
    		else if (result==2)
    		{
    			TextView tv1 = (TextView) rootView.findViewById(R.id.textView2);
    			tv1.setText("");
    			TextView tv3 = (TextView) rootView.findViewById(R.id.textView4);
    			tv3.setTextSize(16);
    			tv3.setGravity(Gravity.CENTER);
    			tv3.setText("You have no upcoming appointments");
    		}
    		else
    		{
    			Log.e("qrcaught", "Weird error");
    		}
     	}
    }
    
}