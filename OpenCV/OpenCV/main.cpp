// main.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Utilities.h"

Mat loadImage(char* location, char* file);
Mat* loadImages(int numberOfImages, char* location, char** files);
void showImage(char* name, Mat image);
void findSign(Mat image);
void findSign(Mat image, Mat temp);
void backProject(Mat image, Mat yellow);
void backProjection(Mat image, Mat yellow);
void Hist_and_Backproj(int, void*);
void digitRecognition(Mat image);

Mat src; Mat hsv; Mat hue; Mat hue2;
int bins = 180;

int _tmain(int argc, _TCHAR* argv[])
{
	cout << "Using OpenCV " << CV_VERSION << endl;
	char* testLocation = "Media/Test Images/";
	char* testFiles[] = {
		"2809 a.jpg",
		"2809 b.jpg",
		"2830 a.jpg",
		"2830 b.jpg",
		"2838 a.jpg",
		"2838 b.jpg",
		"2838 c.jpg",
		"2838 d.jpg",
		"2839 a.jpg",
		"2839 b.jpg",
		"2839 c.jpg",
		"2839 d.jpg"
	};

	char* templateLocation = "Media/Templates/";
	char* templateFiles[] = {
		"stop.png",
		"stop&no.png",
		"stop2.png",
		"stop&no2.png",
		"yellow.png"
	};

	Mat sign = loadImage(testLocation, testFiles[6]);
	Mat yellow = loadImage(templateLocation, templateFiles[4]);

	//backProject(sign, yellow);

	//backProjection(sign, yellow);
	backProjection(yellow, sign);

	/*int numberOfTestImages = sizeof(testFiles) / sizeof(testFiles[0]);
	Mat* busStops = loadImages(numberOfTestImages, testLocation, testFiles);
	int numberOfTemplateImages = sizeof(templateFiles) / sizeof(templateFiles[0]);
	Mat* templates = loadImages(numberOfTemplateImages, templateLocation, templateFiles);

	backProject(busStops[6], templates[4]);

	for (int i = 0; i < numberOfTestImages; i++) {
		cout << "Processing image " << (i + 1) << endl;
		findSign(busStops[i]);
		if (i == 0 || i == 1) {
			// templates[0] or templates[1]


		}
		else {
			// templates[2] or templates[3]


		}
		
		showImage("Bus Stop", busStops[i]);
		waitKey(0);
	}*/

	return 0;
}

Mat loadImage(char* location, char* file) {
	string filename(location);
	filename.append(file);
	Mat image = imread(filename, 1);

	return image;
}

Mat* loadImages(int numberOfImages, char* location, char** files) {
	Mat* images = new Mat[numberOfImages];
	for (int i = 0; i < numberOfImages; i++) {
		string filename(location);
		filename.append(files[i]);
		cout << "Loading image " << (i + 1) << ": " << filename << endl;
		images[i] = imread(filename, 1);
	}
	return images;
}

void showImage(char* name, Mat image) {
	resize(image, image, Size(image.cols / 4, image.rows / 4));
	imshow(name, image);

	return;
}

void findSign(Mat image) {
	Mat hsv;
	cvtColor(image, hsv, CV_BGR2HSV);
	inRange(hsv, Scalar(15, 135, 140), Scalar(30, 255, 255), hsv);
	showImage("Result", hsv);

	return;
}

void backProject(Mat image, Mat yellow) {
	Mat hsv;
	cvtColor(image, hsv, COLOR_BGR2HSV);
	Mat hsvt;
	cvtColor(yellow, hsvt, COLOR_BGR2HSV);

	int histSize = 25;
	float hueRanges[] = { 0, 180 };
	const float* ranges = { hueRanges };

	Mat hue;
	hue.create(hsv.size(), hsv.depth());
	int ch[] = { 0, 0 };
	mixChannels(&hsv, 1, &hue, 1, ch, 1);

	MatND hist;
	//calcHist(hsv, 1, 0, Mat(), hist, 1, MAX(25, 2), { 0, 180 }, true, false);
	// TODO: fix the following function
	calcHist(&hue, 1, 0, Mat(), hist, 1, &histSize, &ranges, true, false);
	normalize(hist, hist, 0, 255, NORM_MINMAX, -1, Mat());

	MatND backProj;
	calcBackProject(&hue, 1, 0, hist, backProj, &ranges, 1, true);

	showImage("Back project", backProj);
	showImage("Stop", hue);
	//imshow("Yellow", hsvt);
	waitKey(0);

	return;
}

void backProjection(Mat image, Mat yellow) {


	/** @function main */
	/// Read the image
	//src = imread(argv[1], 1);
	image.copyTo(src);
	/// Transform it to HSV
	cvtColor(image, hsv, CV_BGR2HSV);

	/// Use only the Hue value
	hue.create(hsv.size(), hsv.depth());
	int ch[] = { 0, 0 };
	mixChannels(&hsv, 1, &hue, 1, ch, 1);

	Mat hsv2;
	cvtColor(yellow, hsv2, CV_BGR2HSV);

	hue2.create(hsv2.size(), hsv2.depth());
	int ch2[] = { 0, 0 };
	mixChannels(&hsv2, 1, & hue2, 1, ch2, 1);

	/// Create Trackbar to enter the number of bins
	char* window_image = "Source image";
	namedWindow(window_image, CV_WINDOW_AUTOSIZE);
	createTrackbar("* Hue  bins: ", window_image, &bins, 180, Hist_and_Backproj);
	Hist_and_Backproj(0, 0);

	/// Show the image
	imshow(window_image, src);

	/// Wait until user exits the program
	waitKey(0);
	return;
}

void Hist_and_Backproj(int, void*) {
	MatND hist;
	int histSize = MAX( bins, 2 );
	float hue_range[] = { 0, 180 };
	const float* ranges = { hue_range };

	/// Get the Histogram and normalize it
	calcHist( &hue, 1, 0, Mat(), hist, 1, &histSize, &ranges, true, false );
	normalize( hist, hist, 0, 255, NORM_MINMAX, -1, Mat() );

	/// Get Backprojection
	MatND backproj;
	calcBackProject( &hue2, 1, 0, hist, backproj, &ranges, 1, true );

	/// Draw the backproj
	imshow( "BackProj", backproj );

	/// Draw the histogram
	int w = 400; int h = 400;
	int bin_w = cvRound( (double) w / histSize );
	Mat histImg = Mat::zeros( w, h, CV_8UC3 );

	for( int i = 0; i < bins; i ++ )
		{ rectangle( histImg, Point( i*bin_w, h ), Point( (i+1)*bin_w, h - cvRound( hist.at<float>(i)*h/255.0 ) ), Scalar( 0, 0, 255 ), -1 ); }

	showImage( "Histogram", histImg );
}

void digitRecognition(Mat image) {
	// TODO: complete function

	return;
}