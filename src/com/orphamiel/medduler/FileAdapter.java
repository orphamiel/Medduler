package com.orphamiel.medduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<MedduFiles>
{
    Context context; 
    int layoutResourceId;    
    ListView listview;
	
	//start prevention of recycling of views
	@Override
	public int getViewTypeCount() 
	{                 
	    return getCount();
	}

	@Override
	public int getItemViewType(int position) 
	{
	    return position;
	}
	//end prevention    
    
    //this is the constructor
    public FileAdapter(Context context, int layoutResourceId, List<MedduFiles> rowfile, ListView listview) 
    {
        super(context, layoutResourceId, rowfile);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.listview = listview;
    }

    //set stuff from constructor to view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View row = convertView;
        FileHolder holder = null;
        
        if(row == null)
        {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new FileHolder();
            holder.dateshow = (TextView)row.findViewById(R.id.dateshow);            
            holder.dayshow = (TextView)row.findViewById(R.id.dayshow);
            holder.locateshow = (TextView)row.findViewById(R.id.locateshow);
            holder.remarkshow = (TextView)row.findViewById(R.id.remarkshow);
            holder.timeshow = (TextView)row.findViewById(R.id.timeshow);
            holder.monthshow = (TextView)row.findViewById(R.id.monthshow);
            holder.issueshow = (TextView)row.findViewById(R.id.issueshow);
            holder.noappt = (TextView)row.findViewById(R.id.noappt);
            
            row.setTag(holder);
        }
        else
        {
            holder = (FileHolder)row.getTag();
        }
        
        MedduFiles meddu = getItem(position);
        //check flag to see what to do
        if (meddu.flag==0)
        {
        	Date date1 = meddu.aptdate;       
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.US);
            String convertedDate = dateFormat.format(date1);               
            SimpleDateFormat dayweek = new SimpleDateFormat("EEEE", Locale.US);
            String dayofweek = dayweek.format(date1);        
            
            String [] datearr, datearr2;
            String appttime = "", apptdate = "", apptday = "", apptmonth = "";
    		try
        	{
        		 datearr = convertedDate.split(" ");
        	 	 apptdate = datearr[0];
        	 	 appttime = datearr[1] + " " + datearr[2];
        	}
    		catch(Exception e)
    		{
    			Log.e("caught", "could not parse date in adapter");
    		}
    		try
    		{
    			datearr2 = apptdate.split("-");
    			apptday = datearr2[0];
    			apptmonth = datearr2[1];
    		}
    		catch(Exception e)
    		{
    			Log.e("caught", "could not split date further in adapter");
    		}
    		
    		//this statement converts it to int then back to string so the 0 in front is gone
    		apptday = Integer.toString(Integer.parseInt(apptday));
    		apptmonth = converttofullmonth(apptmonth);
            
            holder.dateshow.setText(apptday);
            holder.dayshow.setText(dayofweek);
            holder.locateshow.setText("Location: " + meddu.locat);
            holder.remarkshow.setText("Remarks: " + meddu.remarks);
            holder.timeshow.setText("Time: " + appttime.toUpperCase(Locale.ENGLISH));
            holder.monthshow.setText(apptmonth);
            holder.issueshow.setText("Issue: " + meddu.issue);        
        }
        else if (meddu.flag==1||meddu.flag==2)
        {
        	row.setBackgroundColor(0xffdddddd);
        	holder.dateshow.setBackgroundColor(0x00000000);
        	holder.dayshow.setBackgroundColor(0x00000000);
        	holder.monthshow.setBackgroundColor(0x00000000);
        	holder.noappt.setEnabled(true);
        	holder.noappt.setText(meddu.filename);
        	holder.noappt.setVisibility(View.VISIBLE);
        	row.setMinimumHeight(this.listview.getHeight());
        }
        else
        {
        	//Header for every year
        	row.setBackgroundColor(0xffdddddd);
        	holder.dateshow.setVisibility(View.GONE);
        	holder.dayshow.setVisibility(View.GONE);
        	holder.monthshow.setVisibility(View.GONE);
        	holder.locateshow.setVisibility(View.GONE);
            holder.remarkshow.setVisibility(View.GONE);
            holder.timeshow.setVisibility(View.GONE);
            holder.issueshow.setVisibility(View.GONE);
        	holder.noappt.setEnabled(true);
        	holder.noappt.setPadding(0,10,0,10);
        	holder.noappt.setTextSize(14); 
        	holder.noappt.setText(meddu.filename);    
        	holder.noappt.setVisibility(View.VISIBLE);
        }
        return row;
    }
    
    static class FileHolder
    {
        TextView dateshow;
        TextView dayshow;
        TextView locateshow;
        TextView remarkshow;
        TextView timeshow;
        TextView monthshow;
        TextView issueshow;
        /**
         * A nice custom textview that can be enabled to be on top of all other objects
         */
        TextView noappt;
    }
    
    public String converttofullmonth(String q)
    {
    	if (q.equals("01"))
		{
			q = "January";
		}
		else if(q.equals("02"))
		{
			q = "February";
		}
		else if(q.equals("03"))
		{
			q = "March";
		}
		else if(q.equals("04"))
		{
			q = "April";
		}
		else if(q.equals("05"))
		{
			q = "May";
		}
		else if(q.equals("06"))
		{
			q = "June";
		}
		else if(q.equals("07"))
		{
			q = "July";
		}
		else if(q.equals("08"))
		{
			q = "August";
		}
		else if(q.equals("09"))
		{
			q = "September";
		}
		else if(q.equals("10"))
		{
			q = "October";
		}
		else if(q.equals("11"))
		{
			q = "November";
		}
		else if(q.equals("12"))
		{
			q = "December";
		}
		else
		{
			q = "Error";
		}
    	return q;
    }
}