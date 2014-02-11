package com.orphamiel.medduler;

import java.util.EnumMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeGen extends FragmentActivity 
{
	BarcodeFormat finalformat = BarcodeFormat.QR_CODE;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	setContentView(R.layout.qrgen);
    	makeqr("Medduler");    	
    	final EditText qrmake = (EditText)findViewById(R.id.qrstring);
    	Spinner qrspin = (Spinner)findViewById(R.id.qrspinner);
    	qrspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
    	{
    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
    	    {
    	        Object item = parent.getItemAtPosition(pos);
    	        if((item.toString()).equals("QR Code"))
    	        {
    	        	finalformat = BarcodeFormat.QR_CODE;
    	        }
    	        else
    	        {
    	        	finalformat = BarcodeFormat.CODE_128;
    	        }
    	        if(!qrmake.getText().toString().equals(""))
    	        {
    	        	makeqr(qrmake.getText().toString());
    	        }
    	        else
    	        {
    	        	makeqr("Medduler");
    	        }
    	        
    	    }
    	    public void onNothingSelected(AdapterView<?> parent) 
    	    {
    	    	
    	    }
    	});
    	qrmake.addTextChangedListener(new TextWatcher()
    	{    		 
    		   public void afterTextChanged(Editable s) 
    		   {
    		   }
    		 
    		   public void beforeTextChanged(CharSequence s, int start, int count, int after) 
    		   {
    		   }
    		 
    		   public void onTextChanged(CharSequence s, int start, int before, int count) 
    		   {
    			   	if(!qrmake.getText().toString().equals(""))
       	        	{
    				   makeqr(qrmake.getText().toString());
       	        	}
    			   	else
    			   	{
    			   		makeqr("Medduler");
    			   	}
    		   }
    	});
    }
    
    public static Bitmap datatoqr(String data, int width, int height)
    {
    	try 
    	{
    	    Bitmap bm = encodeAsBitmap(data, BarcodeFormat.QR_CODE, width, height);
    	    return bm;
    	} 
    	catch (WriterException e) 
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public void makeqr(String data)
    {
    	ImageView iv = (ImageView)findViewById(R.id.qrimage);
    	try 
    	{
    		//400*400 as resolution
    	    Bitmap bm = encodeAsBitmap(data, finalformat, 400, 400);
    	    if(bm != null) 
    	    {
    	        iv.setImageBitmap(bm);
    	    }
    	} 
    	catch (WriterException e) 
    	{
    		e.printStackTrace();
    	}
    }
    
    public void hideedit(View v)
    {
    	EditText qrmake = (EditText)findViewById(R.id.qrstring);
    	qrmake.setEnabled(false);
    	qrmake.setVisibility(View.GONE);
    	Button qrbutton =((Button)findViewById(R.id.qrmakebutt));
    	qrbutton.setEnabled(false);
    	qrbutton.setVisibility(View.GONE);   	
    	Spinner qrspin = (Spinner)findViewById(R.id.qrspinner);
    	qrspin.setEnabled(false);
    	qrspin.setVisibility(View.GONE);
    }
    
    public void unhide(View v)
    {
    	EditText qrmake = (EditText)findViewById(R.id.qrstring);
    	qrmake.setEnabled(true);
    	qrmake.setVisibility(View.VISIBLE);
    	Button qrbutton =((Button)findViewById(R.id.qrmakebutt));
    	qrbutton.setEnabled(true);
    	qrbutton.setVisibility(View.VISIBLE);   	
    	Spinner qrspin = (Spinner)findViewById(R.id.qrspinner);
    	qrspin.setEnabled(true);
    	qrspin.setVisibility(View.VISIBLE);   
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
	    {
	    case android.R.id.home:
	        finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     * 
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    //was not static
    static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
    String contentsToEncode = contents;
    if (contentsToEncode == null) {
        return null;
    }
    Map<EncodeHintType, Object> hints = null;
    String encoding = guessAppropriateEncoding(contentsToEncode);
    if (encoding != null) {
        hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, encoding);
    }
    MultiFormatWriter writer = new MultiFormatWriter();
    BitMatrix result;
    try {
        result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
    } catch (IllegalArgumentException iae) {
        // Unsupported format
        return null;
    }
    int width = result.getWidth();
    int height = result.getHeight();
    int[] pixels = new int[width * height];
    for (int y = 0; y < height; y++) {
        int offset = y * width;
        for (int x = 0; x < width; x++) {
        pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
        }
    }

    Bitmap bitmap = Bitmap.createBitmap(width, height,
        Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
    // Very crude at the moment
    for (int i = 0; i < contents.length(); i++) {
        if (contents.charAt(i) > 0xFF) {
        return "UTF-8";
        }
    }
    return null;
    }

}