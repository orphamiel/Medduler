package com.orphamiel.medduler;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
 
public class singledialog extends DialogFragment 
{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		String titledia = getArguments().getString("title");
		String textdia = getArguments().getString("text");
		Dialog singledia = new Dialog(getActivity());
		singledia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		singledia.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);		
		singledia.setContentView(R.layout.customdiag);
		((TextView)singledia.findViewById(R.id.diagtitle)).setText(titledia);
		((TextView)singledia.findViewById(R.id.diagmessage)).setText(textdia);
		singledia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		singledia.findViewById(R.id.positive_button).setOnClickListener(new OnClickListener() 
		{
            @Override
            public void onClick(View v) 
            {
                dismiss();
            }
		});
		setCancelable(false);
		return singledia;
	}	
}