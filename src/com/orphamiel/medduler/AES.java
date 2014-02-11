package com.orphamiel.medduler;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AES
{
	//the \\ is actually one \ because of java
	public final static byte[] aeskey = "st3\\/3n[h!ng0rph@m1e]k3nn3dy2O|3".getBytes();
	public final static byte[] iv = "2013GpA@ch13ver5".getBytes();
	
	public static String encrypt(String text, byte[] key) throws Exception
	{
		Cipher cipher;
		byte[] bytes = null;
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try 
		{
			cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(iv));
			bytes = cipher.doFinal(text.getBytes("UTF-8"));
		} 
		catch (NoSuchAlgorithmException e) 
		{
			throw new Exception(e);
		} 
		catch (NoSuchPaddingException e) 
		{
			throw new Exception(e);
		} 
		catch (InvalidKeyException e) 
		{
			throw new Exception(e);
		} 
		catch (IllegalBlockSizeException e) 
		{
			throw new Exception(e);
		} 
		catch (BadPaddingException e) 
		{
			throw new Exception(e);
		} 
		catch (UnsupportedEncodingException e) 
		{
			throw new Exception(e);
		}
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	public static String decrypt(String text, byte[] key) throws Exception
	{
		Cipher cipher;
		byte[] bytes = null;
		String result = new String();
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		try 
		{
			cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");			
			cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(iv));
			bytes = cipher.doFinal(Base64.decode(text, Base64.NO_WRAP));
			result = new String(bytes, "UTF-8");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			throw new Exception(e);
		} 
		catch (NoSuchPaddingException e) 
		{
			throw new Exception(e);
		}
		catch (InvalidKeyException e) 
		{
			throw new Exception(e);
		} 
		catch (IllegalBlockSizeException e) 
		{
			throw new Exception(e);
		} 
		catch (BadPaddingException e) 
		{
			throw new Exception(e);
		} 
		catch (UnsupportedEncodingException e) 
		{
			throw new Exception(e);
		} 
		return result;
	}
}