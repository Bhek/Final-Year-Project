package com.example.visionapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
//import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
		
		AssetManager asset = getResources().getAssets();
		InputStream in = asset.open("sign.jpg");
		bitmap = BitmapFactory.decodeStream(in);
		
		Bitmap signBitmap = bitmap;
		
		image = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(bitmap, image);
		imageProcessing();
		Utils.matToBitmap(image, bitmap);
		
		String stopNumber = digitRecognition(bitmap).split("\n")[1].replace(" ", "");
		
		
				
		ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(signBitmap);
		
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(stopNumber);
		
		Intent intent = new Intent(getBaseContext(), ResultsActivity.class);
		intent.putExtra("rtpi stop", stopNumber);
		startActivity(intent);
	}
	
	private void imageProcessing() throws IOException {
		Mat backProj = backProject();
		
		Mat im1 = new Mat();
		Mat im2 = new Mat();
		backProj.copyTo(im1);
		backProj.copyTo(im2);
		//image.convertTo(im2, CvType.CV_8U);
		backProj.convertTo(im1, CvType.CV_8U);

		//vector<vector<Point>> contours;
		//Point[][] contours;
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//Imgproc.findContours(im2, contours, im2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Imgproc.findContours(im1, contours, im2, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

		double maxArea = 0;
		int maxIdX = 0;
		for (int i = 0; i < contours.size(); i++) {
			//double area = contourArea(contours[i]);
			double area = Imgproc.contourArea(contours.get(i));
			maxIdX = area > maxArea ? i : maxIdX;
			maxArea = area > maxArea ? area : maxArea;
		}

		im1.setTo(new Scalar(0));
		Imgproc.drawContours(im1, contours, maxIdX, new Scalar(255), -1);
		
		backProj.copyTo(image);
		
		Core.absdiff(backProj, im1, image);
		
		Imgproc.erode(image, image, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
		Imgproc.dilate(image, image, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15)));
	}
	
	private Mat backProject() throws IOException {
		Mat backProj = new Mat();
		AssetManager am = getResources().getAssets();
		InputStream is = am.open("yellow.png");
		Bitmap yellowBitmap = BitmapFactory.decodeStream(is);
		yellow = new Mat(yellowBitmap.getWidth(), yellowBitmap.getHeight(), CvType.CV_8UC1);
		Utils.bitmapToMat(yellowBitmap, yellow);
		
		ArrayList<Mat> imageList = new ArrayList<Mat>();
        Imgproc.cvtColor(image, backProj, Imgproc.COLOR_BGR2HSV);
		imageList.add(backProj);
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
        
        Imgproc.calcBackProject(imageList, channels, hist, backProj, ranges, 1);
        
        return backProj;
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