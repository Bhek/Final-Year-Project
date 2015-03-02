#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/video/background_segm.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <stdio.h>
#include <iostream>
#include <iostream>
#define PI 3.14159265358979323846

using namespace std;
using namespace cv;

void invertImage(Mat &image, Mat &result_image);
void ImagesDemos(Mat& image1, Mat& image2, Mat& logo_image, Mat& people_image);
void HistogramsDemos(Mat& dark_image, Mat& fruit_image, Mat& people_image, Mat& skin_image, Mat all_images[], int number_of_images);
void BinaryDemos(Mat& pcb_image, Mat& stationery_image);
void GeometricDemos(Mat& image1, Mat& image2, Mat& image3);
void VideoDemos(VideoCapture& surveillance_video, int starting_frame, bool clean_binary_images);
void EdgeDemos(Mat& image1, Mat& image2);
void FeaturesDemos(Mat& image1, Mat& image2, Mat& image3);
void RecognitionDemos(Mat& full_image, Mat& template1, Mat& template2, Mat& template1locations, Mat& template2locations, VideoCapture& bicycle_video, Mat& bicycle_background, Mat& bicycle_model, VideoCapture& people_video, CascadeClassifier& cascade, Mat& numbers);
void TrackingDemo(VideoCapture& video, Rect& starting_position, int starting_frame, int end_frame);
int CameraCalibration(string passed_settings_filename);

class TimestampEvent {
private:
	String mEventName;
	double mAverageDuration;
	double mLastDuration;
	int mEventCount;
public:
	TimestampEvent();
	void Reset(String event_name);
	void RecordEvent(int duration);
	double getLastTime();
	double getAverageTime();
	String getEventName();
	String getString(bool average = true, bool last = true);
};


class Timestamper {
private:
#define MAX_EVENTS 20
	TimestampEvent mEvents[MAX_EVENTS];
	int mEventCount;
	double mLastTickCount;
	double mTickFrequency;
public:
	Timestamper();
	void reset();
	void ignoreTimeSinceLastRecorded();
	void recordTime(String event = "");
	void putTimes(Mat output_image);
};

void invertImage(Mat &image, Mat &result_image);
Mat StretchImage(Mat& image);
void show_32bit_image(char* window_name, Mat& passed_image, double zero_maps_to = 0.0, double passed_scale_factor = -1.0);
Mat ComputeDefaultImage(Mat& passed_image);
