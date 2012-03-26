package com.fenritz.safecamera.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fenritz.safecamera.R;

public class Helpers {
	public static SecretKey key;

	public static final String JPEG_FILE_PREFIX = "IMG_";
	
	public static AESCrypt getAESCrypt() {
		try {
			return new AESCrypt(key);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		catch (AESCryptException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static SecretKey getAESKey(Activity activity, String password) {
		try {
			return AESCrypt.getSecretKey(password);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Helpers.showAlertDialog(activity, String.format(activity.getString(R.string.unexpected_error), "100"));
		}
		catch (NoSuchProviderException e) {
			e.printStackTrace();
			Helpers.showAlertDialog(activity, String.format(activity.getString(R.string.unexpected_error), "101"));
		}
		catch (AESCryptException e) {
			e.printStackTrace();
			Helpers.showAlertDialog(activity, String.format(activity.getString(R.string.unexpected_error), "102"));
		}
		
		return null;
	}

	public static String getFilename(Context context, String prefix) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = prefix + timeStamp;

		return imageFileName + ".jpg" + context.getString(R.string.file_extension);
	}

	public static void printMaxKeySizes() {
		try {
			Set<String> algorithms = Security.getAlgorithms("Cipher");
			for (String algorithm : algorithms) {
				int max = Cipher.getMaxAllowedKeyLength(algorithm);
				Log.d("keys", String.format("%s: %dbit", algorithm, max));
			}
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void showAlertDialog(Activity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		builder.setNegativeButton(activity.getString(R.string.ok), null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public static String getHomeDir(Context context){
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		return sdcardPath + "/" + sharedPrefs.getString("home_folder", context.getString(R.string.default_home_folder_name));
	}
	
	public static String getThumbsDir(Context context){
		return getHomeDir(context) + "/" + context.getString(R.string.default_thumb_folder_name);
	}
	
	public static void createFolders(Context context){
		File dir = new File(getThumbsDir(context));
		if(!dir.exists() || !dir.isDirectory()) {
		    dir.mkdirs();
		}
	}
	
	public static void generateThumbnail(Context context, byte[] data, String fileName) throws FileNotFoundException{
		BitmapFactory.Options bitmapOptions=new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
		if(bitmap != null){
			Bitmap thumbBitmap = Helpers.getThumbFromBitmap(bitmap, Integer.valueOf(context.getString(R.string.thumb_size)));
		
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] imageByteArray = stream.toByteArray();
			
			FileOutputStream out = new FileOutputStream(Helpers.getThumbsDir(context) + "/" + fileName);
			Helpers.getAESCrypt().encrypt(imageByteArray, out);
		}
	}
	
	public static Bitmap getThumbFromBitmap(Bitmap bitmap, int squareSide){
	    int imgWidth = bitmap.getWidth();
	    int imgHeight = bitmap.getHeight();
	    
	    int cropX, cropY, cropWidth, cropHeight;
	    if(imgWidth >= imgHeight){
	    	cropX = imgWidth/2 - imgHeight/2;
	    	cropY = 0;
	    	cropWidth = imgHeight;
	    	cropHeight = imgHeight;
	    }
	    else{
	    	cropX = 0;
	    	cropY = imgHeight/2 - imgWidth/2;
	    	cropWidth = imgWidth;
	    	cropHeight = imgWidth;
	    }
	    
	    Bitmap cropedImg = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight);
	    
	    return Bitmap.createScaledBitmap(cropedImg, squareSide, squareSide, true);
	}
}
