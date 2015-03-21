package com.example.visionapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.opencv.android.Utils;
//import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageActivity extends Activity {
	Mat image, yellow, hist;
	
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/VisionBusApp/";
	
	public static final String lang = "eng";
	
	private static final String TAG = "ImageActivity.java";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String[] paths = new String[] {DATA_PATH, DATA_PATH + "tessdata/"};
		
		for (String path: paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				}
				else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}
		
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager am = getResources().getAssets();
				InputStream is = am.open("tessdata/" + lang + ".traineddata");
				
				OutputStream os = new FileOutputStream(DATA_PATH + "tessData/" + lang + ".traineddata");
				
				byte[] buf = new byte[1024];
				int len;
				
				while ((len = is.read(buf)) > 0) {
					os.write(buf, 0, len);
				}
				
				is.close();
				os.close();
				
				Log.v(TAG, "Copied " + lang + ".traineddata");
			} catch (IOException e) {
				Log.v(TAG, "Was unable to copy eng.traineddata " + e.toString());
			}
		}
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	protected void onResume() {
        super.onResume();
        try {
			processImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
	
	public void processImage() throws Exception {
		Bitmap bitmap = null;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			bitmap = (Bitmap) getIntent().getParcelableExtra("image");
		}
		
		image = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(bitmap, image);
		backProject();
		Utils.matToBitmap(image, bitmap);
		
		AssetManager am = getResources().getAssets();
		InputStream is = am.open("scratchcard.png");
		Bitmap bm = BitmapFactory.decodeStream(is);
		
		String stopNumber = digitRecognition(bm);
		
		ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(bm);
		
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(stopNumber);
	}
	
	private void backProject() throws IOException {
		AssetManager am = getResources().getAssets();
		InputStream is = am.open("scratchcard.png");
		Bitmap yellowBitmap = BitmapFactory.decodeStream(is);
		yellow = new Mat(yellowBitmap.getWidth(), yellowBitmap.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(yellowBitmap, yellow);
		
		ArrayList<Mat> imageList = new ArrayList<Mat>();
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
		imageList.add(image);
        //MatOfInt ch = new MatOfInt(0);
		//Core.mixChannels(imageList, imageList, ch);
		
		ArrayList<Mat> yellowList = new ArrayList<Mat>();
        Imgproc.cvtColor(yellow, yellow, Imgproc.COLOR_BGR2HSV);
        yellowList.add(yellow);
		//Core.mixChannels(yellowList, yellowList, ch);
		
		MatOfInt channels = new MatOfInt(0);
        Mat hist= new Mat();
        MatOfInt histSize = new MatOfInt(25);
        MatOfFloat ranges = new MatOfFloat(0, 180);
        
        Imgproc.calcHist(yellowList, channels, new Mat(), hist, histSize, ranges);
        
        Imgproc.calcBackProject(imageList, channels, hist, image, ranges, 1);
	}
	
	private String digitRecognition(Bitmap bitmap) {
		TessBaseAPI tess = new TessBaseAPI();
		//tess.init("/storage/sdcard0/FYP/", "eng");
		tess.init(DATA_PATH, lang);
		tess.setVariable("tessedit_char_whitelist", "0123456789");
		tess.setImage(bitmap);
		String stopNumber = tess.getUTF8Text();
		tess.end();
		
		return stopNumber;
	}
 	
 	public void goBack(View view) {
 		finish();
 	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_image,
					container, false);
			return rootView;
		}
	}

}