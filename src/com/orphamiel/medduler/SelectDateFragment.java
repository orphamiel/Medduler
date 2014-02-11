package com.orphamiel.medduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener 
{	
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Boolean errortest = false;
		int yy = 0, mm = 0, dd = 0;
		String datestring = ((Button)getActivity().findViewById(R.id.datebutt)).getText().toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy", Locale.US);
		Date convertedDate = new Date();
		try 
		{
    		convertedDate = dateFormat.parse(datestring);
		} 
    	catch (ParseException e) 
    	{
    		errortest=true;
    	}
		final Calendar calendar = Calendar.getInstance();
		if (errortest == false)
		{
		    calendar.setTime(convertedDate);
		}
		yy = calendar.get(Calendar.YEAR);
		mm = calendar.get(Calendar.MONTH);
		dd = calendar.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), this, yy, mm, dd);
	}
	
	public void onDateSet(DatePicker view, int yy, int mm, int dd) 
	{
		String daystring = "", monthstring = "";
		Button mEdit = (Button)getActivity().findViewById(R.id.datebutt);
        daystring = Integer.toString(dd);
		try
		{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, mm);
			SimpleDateFormat monthdate = new SimpleDateFormat("MMMM", Locale.US);
			monthdate.setCalendar(calendar);
			monthstring = monthdate.format(calendar.getTime());
		}
		catch (Exception e)
		{
			
		}		
		mEdit.setText(daystring+" "+monthstring+", "+yy);
	}
}