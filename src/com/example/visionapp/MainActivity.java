package com.example.visionapp;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends Activity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static Uri uri;
	Mat m;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OCVSample::Activity", "OpenCV loaded successfully");
                    m = new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) 
            bgDrawable.draw(canvas);
        else 
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
    
    public void toYuv(View view) {
		File picture = new File ("/storage/sdcard0/DCIM/image.jpg");
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(picture.getAbsolutePath());
		//View photo = findViewById(R.id.imageView1);
		//ImageView image = (ImageView) findViewById(R.id.imageView1);
		//Bitmap bm = getBitmapFromView(photo);
		Utils.bitmapToMat(bm, m);
		Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2YUV);
		Utils.matToBitmap(m, bm);
		image.setImageBitmap(bm);
	}
    
    public void toRgb(View view) {
		File picture = new File ("/storage/sdcard0/DCIM/image.jpg");
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(picture.getAbsolutePath());
		//View photo = findViewById(R.id.imageView1);
		//ImageView image = (ImageView) findViewById(R.id.imageView1);
		//Bitmap bm = getBitmapFromView(photo);
		Utils.bitmapToMat(bm, m);
		//Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2GRAY);
		//Utils.matToBitmap(m, bm);
		image.setImageBitmap(bm);
	}
	
    public void toHls(View view) {
		File picture = new File ("/storage/sdcard0/DCIM/image.jpg");
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(picture.getAbsolutePath());
		//View photo = findViewById(R.id.imageView1);
		//ImageView image = (ImageView) findViewById(R.id.imageView1);
		//Bitmap bm = getBitmapFromView(photo);
		Utils.bitmapToMat(bm, m);
		Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2HLS);
		Utils.matToBitmap(m, bm);
		image.setImageBitmap(bm);
	}
    
    public void toGrey(View view) {
		File picture = new File ("/storage/sdcard0/DCIM/image.jpg");
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		Bitmap bm = BitmapFactory.decodeFile(picture.getAbsolutePath());
		//View photo = findViewById(R.id.imageView1);
		//ImageView image = (ImageView) findViewById(R.id.imageView1);
		//Bitmap bm = getBitmapFromView(photo);
		Utils.bitmapToMat(bm, m);
		Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2GRAY);
		Utils.matToBitmap(m, bm);
		image.setImageBitmap(bm);
	}
	
	public void takePic(View view) {
		//Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    //}
	}
	
	public void filecheck(View view) {
		File f = new File("/storage");        
		File file[] = f.listFiles();
		Log.d("Files", "Size: "+ file.length);
		for (int i=0; i < file.length; i++) {
		    Log.d("Files", "FileName:" + file[i].getName());
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE) {
	        if (resultCode == RESULT_OK) {
	        	uri = data.getData();
	        }
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		/*File picture = new File ("/storage/sdcard0/DCIM/image.jpg");
		Bitmap bm = BitmapFactory.decodeFile(picture.getAbsolutePath());
		m = new Mat (bm.getWidth(), bm.getHeight(), CvType.CV_8UC1);*/
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);
    } 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
