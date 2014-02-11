package com.orphamiel.medduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener 
{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Boolean errortest = false;
		int hour = 0, minute = 0;
		String timestring = ((Button)getActivity().findViewById(R.id.timebutt)).getText().toString();
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa", Locale.US);
		Date convertedDate = new Date();
		try 
		{
    		convertedDate = timeFormat.parse(timestring);
		} 
    	catch (ParseException e) 
    	{
    		errortest=true;
    	}
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		if (errortest == false)
		{
		    c.setTime(convertedDate);
		}
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hour, int minute) 
	{
		Button mEdit2 = (Button)getActivity().findViewById(R.id.timebutt);
		Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, hour);
        ca.set(Calendar.MINUTE, minute);
        hour = ca.get(Calendar.HOUR);
        String hourstring = "", minutestring = "";
        if (hour==0)
        {
        	hourstring = "12";
        }
        else
        {
            hourstring = Integer.toString(hour);
        }
        try
		{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MINUTE, minute);
			SimpleDateFormat minutedate = new SimpleDateFormat("mm", Locale.US);
			minutedate.setCalendar(calendar);
			minutestring = minutedate.format(calendar.getTime());
		}
		catch (Exception e)
		{
			
		}
        int a = ca.get(Calendar.AM_PM);
        if (a==0)
        {
        	mEdit2.setText(hourstring+":"+minutestring+" am");
        }
        else
        {
        	mEdit2.setText(hourstring+":"+minutestring+ " pm");
        }
	}
}