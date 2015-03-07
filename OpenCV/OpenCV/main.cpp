// main.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "Utilities.h"

Mat loadImage(char* location, char* file);
Mat* loadImages(int numberOfImages, char* location, char** files);
void showImage(char* name, Mat image);
Mat findSign(Mat image);
MatND backProjection(Mat image, Mat yellow);
Mat getHue(Mat image);
Mat templateMatching(Mat image, Mat templateImage);
/*void backProjection(Mat image, Mat yellow);
void Hist_and_Backproj(int, void*);*/
void digitRecognition(Mat image);

/*Mat src; Mat hsv; Mat hue; Mat hue2;
int bins = 180;*/

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
	Mat templateSign = loadImage(testLocation, templateFiles[2]);
	Mat yellow = loadImage(templateLocation, templateFiles[4]);

	showImage("Sign", sign);
	Mat hsvSign = findSign(sign);
	MatND backProjSign = backProjection(sign, yellow);
	Mat templateMatch = templateMatching(sign, templateSign);

	showImage("HSV", hsvSign);
	showImage("Back Projection", backProjSign);
	showImage("Template Matching", templateMatch);
	waitKey(0);

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

Mat findSign(Mat image) {
	Mat hsv;
	cvtColor(image, hsv, CV_BGR2HSV);
	inRange(hsv, Scalar(15, 135, 140), Scalar(30, 255, 255), hsv);

	return hsv;
}

MatND backProjection(Mat image, Mat yellow) {
	Mat imageHue = getHue(image);
	Mat yellowHue = getHue(yellow);

	MatND hist;
	int histSize = 180;
	float hue_range[] = { 0, 180 };
	const float* ranges = { hue_range };

	calcHist(&yellowHue, 1, 0, Mat(), hist, 1, &histSize, &ranges, true, false);
	normalize(hist, hist, 0, 255, NORM_MINMAX, -1, Mat());

	MatND backProject;
	calcBackProject(&imageHue, 1, 0, hist, backProject, &ranges, 1, true);

	return backProject;
}

Mat getHue(Mat image) {
	Mat hsv, hue;
	cvtColor(image, hsv, CV_BGR2HSV);

	hue.create(hsv.size(), hsv.depth());
	int ch[] = { 0, 0 };
	mixChannels(&hsv, 1, &hue, 1, ch, 1);

	return hue;
}

Mat templateMatching(Mat image, Mat templateImage) {
	// TODO: try templateMatching as a method for finding signs
	Mat result;
	image.copyTo(result);

	return result;
}

void digitRecognition(Mat image) {
	// TODO: recognise numbers from sign

	return;
}