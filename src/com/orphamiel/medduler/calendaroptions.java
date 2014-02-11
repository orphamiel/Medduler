package com.orphamiel.medduler;

import java.util.HashSet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class calendaroptions extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//to enable the up button 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.calendarsettings);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.settingsmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    // Handle item selection
	    switch (item.getItemId()) 
	    {
	    case R.id.menitem1:
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    	prefs.edit().remove("calimport").commit();
	    	prefs.edit().remove("dura").commit();
	    	prefs.edit().remove("priva").commit();
	    	prefs.edit().remove("calrecur").commit();
	    	prefs.edit().remove("calrepeat").commit();
	    	prefs.edit().remove("remind").commit();
	    	resetElementValue();
	        return true;
	    //to do job on up button
	    case android.R.id.home:
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void resetElementValue() 
	{
		CheckBoxPreference calimp = (CheckBoxPreference) super.findPreference("calimport");
		calimp.setChecked(true);
		EditTextPreference durat = (EditTextPreference) super.findPreference("dura");
		durat.setText("60");
	    ListPreference privat = (ListPreference) super.findPreference("priva");
	    privat.setValue("2");
	    CheckBoxPreference calrec = (CheckBoxPreference) super.findPreference("calrecur");
		calrec.setChecked(false);
		ListPreference frequ = (ListPreference) super.findPreference("calrepeat");
	    frequ.setValue("DAILY");
	    MultiSelectListPreference rem = (MultiSelectListPreference) super.findPreference("remind");
	    rem.setValues(new HashSet<String>()
	    {{
	    	add("60");
	   	 	add("1440");
	   	}});
	    Toast.makeText(getApplicationContext(), "Calendar settings have been reset", Toast.LENGTH_LONG).show();
	}
}