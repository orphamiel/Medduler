package com.orphamiel.medduler;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class WifiDialog extends DialogFragment 
{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
    	String titledia = getArguments().getString("title");
		String textdia = getArguments().getString("text");
		Dialog wifidia = new Dialog(getActivity());
		wifidia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		wifidia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
		wifidia.setContentView(R.layout.custduodiag);
		((TextView)wifidia.findViewById(R.id.diagtitle2)).setText(titledia);
		((TextView)wifidia.findViewById(R.id.diagmessage2)).setText(textdia);
		wifidia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		Button posbutt = (Button)wifidia.findViewById(R.id.pos_butt);
		posbutt.setText("Wi-Fi Settings");			
		posbutt.setOnClickListener(new OnClickListener() 
		{
            @Override
            public void onClick(View v) 
            {
                dismiss();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
		});
		Button negbutt = (Button)wifidia.findViewById(R.id.nega_butt);
		negbutt.setText("Cancel");	
		negbutt.setOnClickListener(new OnClickListener() 
		{
            @Override
            public void onClick(View v) 
            {
                dismiss();
            }
		});
		setCancelable(false);
		return wifidia;	
	}
}