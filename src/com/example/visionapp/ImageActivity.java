package com.example.visionapp;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageActivity extends Activity {
	Mat image = new Mat();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HLS);
		Utils.matToBitmap(image, bitmap);
		
		ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(bitmap);
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