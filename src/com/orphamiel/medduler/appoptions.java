package com.orphamiel.medduler;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class appoptions extends PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.appsettings);
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
	    	prefs.edit().remove("saveoff").commit();
	    	prefs.edit().remove("usern").commit();
	    	prefs.edit().remove("passw").commit();
	    	prefs.edit().remove("portn").commit();
	    	prefs.edit().remove("wifidown").commit();
	    	prefs.edit().remove("writeexists").commit();
	    	prefs.edit().remove("caregiv").commit();
	    	resetElementValue2();
	    	return true;
	    case android.R.id.home:
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void resetElementValue2() 
	{
		CheckBoxPreference calimp = (CheckBoxPreference) super.findPreference("calimport");
		calimp.setChecked(true);
		CheckBoxPreference saveof = (CheckBoxPreference) super.findPreference("saveoff");
		saveof.setChecked(true);
		EditTextPreference userna = (EditTextPreference) super.findPreference("usern");
		userna.setText("medduler");
		EditTextPreference passwo = (EditTextPreference) super.findPreference("passw");
		passwo.setText("medpass");
		EditTextPreference portnu = (EditTextPreference) super.findPreference("portn");
		portnu.setText("21");
		CheckBoxPreference wifid = (CheckBoxPreference) super.findPreference("wifidown");
		wifid.setChecked(false);
		CheckBoxPreference existwrite = (CheckBoxPreference) super.findPreference("writeexists");
		existwrite.setChecked(true);
	    EditTextPreference caregive = (EditTextPreference) super.findPreference("caregiv");
		caregive.setText("");
		Toast.makeText(getApplicationContext(), "Settings have been reset", Toast.LENGTH_LONG).show();
	}
}