// main.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Utilities.h"

using namespace std;
using namespace cv;

void rgb2cmyk(Mat& src, vector<Mat>& cmyk);

int _tmain(int argc, _TCHAR* argv[])
{
	cout << "Using OpenCV " << CV_VERSION << endl;
	char* file = "Media/scratchcard.png";  // Location of bus stop sign images

	Mat im0 = imread(file, 1);
	//imshow("im0", im0);
	vector<Mat> cmyk;

	rgb2cmyk(im0, cmyk);

	//imshow("C", cmyk[0]);
	//imshow("M", cmyk[1]);
	//imshow("Y", cmyk[2]);
	//imshow("K", cmyk[3]);

	Mat im1;
	im1 = cmyk[3].mul(1 - cmyk[1]) > 0.25;
	//imshow("im1", im1);
	
	// Keep only the largest contour
	Mat im2;
	im1.convertTo(im2, CV_8U);

	vector<vector<Point>> contours;
	findContours(im2, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

	double max_area = 0;
	int max_idx = 0;
	for (int i = 0; i < contours.size(); i++) {
		double area = cv::contourArea(contours[i]);
		max_idx = area > max_area ? i : max_idx;
		max_area = area > max_area ? area : max_area;
	}

	im2.setTo(cv::Scalar(0));
	cv::drawContours(im2, contours, max_idx, cv::Scalar(255), -1);
	//imshow("im2", im2);

	// Use the image to extract the digits from the original image
	Mat im3;
	cvtColor(im0, im3, CV_BGR2GRAY);
	im3 = ((255 - im3) & im2) > 200;
	//imshow("im3", im3);

	// Remove the remaining noise
	Mat dst = im3.clone();
	findContours(dst.clone(), contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
	for (int i = 0; i < contours.size(); i++) {
		if (contourArea(contours[i]) < 100)
			drawContours(dst, contours, i, Scalar(0), -1);
	}
	imshow("dst", dst);
	
	waitKey(0);
	return 0;
}

void rgb2cmyk(Mat& src, vector<Mat>& cmyk) {
	CV_Assert(src.type() == CV_8UC3);

	cmyk.clear();

	for (int i = 0; i < 4; ++i) {
		cmyk.push_back(Mat(src.size(), CV_32F));
	}

	for (int i = 0; i < src.rows; ++i) {
		for (int j = 0; j < src.cols; ++j) {
			Vec3b p = src.at<Vec3b>(i, j);

			float r = p[2] / 255.;
			float g = p[1] / 255.;
			float b = p[0] / 255.;
			float k = (1 - max(max(r, g), b));

			cmyk[0].at<float>(i, j) = (1 - r - k) / (1 - k);
			cmyk[1].at<float>(i, j) = (1 - g - k) / (1 - k);
			cmyk[2].at<float>(i, j) = (1 - b - k) / (1 - k);
			cmyk[3].at<float>(i, j) = k;
		}
	}

	return;
}