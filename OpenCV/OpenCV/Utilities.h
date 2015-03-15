#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/video/background_segm.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <stdio.h>
#include <iostream>
#include <iostream>
#define PI 3.14159265358979323846

//using namespace std;
//using namespace cv;

void invertImage(cv::Mat &image, cv::Mat &result_image);
void ImagesDemos(cv::Mat& image1, cv::Mat& image2, cv::Mat& logo_image, cv::Mat& people_image);
void HistogramsDemos(cv::Mat& dark_image, cv::Mat& fruit_image, cv::Mat& people_image, cv::Mat& skin_image, cv::Mat all_images[], int number_of_images);
void BinaryDemos(cv::Mat& pcb_image, cv::Mat& stationery_image);
void GeometricDemos(cv::Mat& image1, cv::Mat& image2, cv::Mat& image3);
void VideoDemos(cv::VideoCapture& surveillance_video, int starting_frame, bool clean_binary_images);
void EdgeDemos(cv::Mat& image1, cv::Mat& image2);
void FeaturesDemos(cv::Mat& image1, cv::Mat& image2, cv::Mat& image3);
void RecognitionDemos(cv::Mat& full_image, cv::Mat& template1, cv::Mat& template2, cv::Mat& template1locations, cv::Mat& template2locations, cv::VideoCapture& bicycle_video, cv::Mat& bicycle_background, cv::Mat& bicycle_model, cv::VideoCapture& people_video, cv::CascadeClassifier& cascade, cv::Mat& numbers);
void TrackingDemo(cv::VideoCapture& video, cv::Rect& starting_position, int starting_frame, int end_frame);
int CameraCalibration(std::string passed_settings_filename);

class TimestampEvent {
private:
	cv::String mEventName;
	double mAverageDuration;
	double mLastDuration;
	int mEventCount;
public:
	TimestampEvent();
	void Reset(cv::String event_name);
	void RecordEvent(int duration);
	double getLastTime();
	double getAverageTime();
	cv::String getEventName();
	cv::String getString(bool average = true, bool last = true);
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
	void recordTime(cv::String event = "");
	void putTimes(cv::Mat output_image);
};

void invertImage(cv::Mat &image, cv::Mat &result_image);
cv::Mat StretchImage(cv::Mat& image);
void show_32bit_image(char* window_name, cv::Mat& passed_image, double zero_maps_to = 0.0, double passed_scale_factor = -1.0);
cv::Mat ComputeDefaultImage(cv::Mat& passed_image);
