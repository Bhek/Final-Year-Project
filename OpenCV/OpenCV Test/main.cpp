// OpenCV Test.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Utilities.h"

Mat* loadImages(int numberOfImages, char* location, char** files);
void showImage(char* name, Mat image);
void findSign(Mat image);
void findSign(Mat image, Mat temp);

int _tmain(int argc, _TCHAR* argv[])
{
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
		"stop&no2.png"
	};

	int numberOfTestImages = sizeof(testFiles) / sizeof(testFiles[0]);
	Mat* busStops = loadImages(numberOfTestImages, testLocation, testFiles);
	int numberOfTemplateImages = sizeof(templateFiles) / sizeof(templateFiles[0]);
	Mat* templates = loadImages(numberOfTemplateImages, templateLocation, templateFiles);

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
	}

	return 0;
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
}

void findSign(Mat image) {
	Mat hsv;
	cvtColor(image, hsv, CV_BGR2HSV);
	inRange(hsv, Scalar(15, 135, 140), Scalar(30, 255, 255), hsv);
	showImage("Result", hsv);
}