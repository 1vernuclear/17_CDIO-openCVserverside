package main.java.OpenCVRecognition;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.List;

public class CircleDetection {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load OpenCV library

        // Initialize video capture from default camera
        VideoCapture cap = new VideoCapture(0);
        if (!cap.isOpened()) {
            System.out.println("Failed to open camera!");
            System.exit(-1);
        }

        //ViewResolution
        // Get the width and height of the video stream
        double width = cap.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double height = cap.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        // Print the resolution of the video stream
        System.out.println("The video stream has a resolution of " + width + "x" + height + " pixels");

        // Load Haar cascade classifier for circle detection
        CascadeClassifier circleDetector = new CascadeClassifier("myballdetector.xml");

        // Initialize list to store detected circles
        List<Point> ballsDetected = new ArrayList<>();

        // Continuously process frames from video stream
        Mat frame = new Mat();
        while (ballsDetected.size() < 20) {
            // Capture frame from video stream
            cap.read(frame);

            // Convert frame to grayscale
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            // Detect circles using Haar cascade classifier
            MatOfRect circles = new MatOfRect();
            circleDetector.detectMultiScale(grayFrame, circles);

            // Extract centers of detected circles and store in ArrayList
            Rect[] circlesArray = circles.toArray();
            for (Rect circle : circlesArray) {
                int x = circle.x + circle.width / 2;
                int y = circle.y + circle.height / 2;
                if(isFound(circlesArray,x,y)){
                    //skips the pooint
                }else{
                    ballsDetected.add(new Point(x, y));

                }
            }
            if(ballsDetected.size() == 10){
                display(circlesArray, frame);
            }
        }

        // Release resources
        cap.release();
        HighGui.destroyAllWindows();

        // Print centers of detected circles
        System.out.println("Detected " + ballsDetected.size() + " circles:");
        for (Point ball : ballsDetected) {
            System.out.println("(" + (int) ball.x + ", " + (int) ball.y + ")");
        }
    }

    public static boolean isFound(Rect[] circlesArray, int x, int y){
        int i = 0;
        for (Rect circle : circlesArray){
            int xDiff = circle.x - x;
            int yDiff = circle.y - y;
            if ((xDiff < 10 && xDiff > -10) && (yDiff < 10 && yDiff > -10)){
                i = 1;
                break;
            }
        }
        return i == 1;
    }
    public static void display(Rect[] circlesArray, Mat frame){
        do {
            // Display frame with detected circles
            for (Rect circle : circlesArray) {
                Imgproc.circle(frame, new Point(circle.x + circle.width / 2f, circle.y + circle.height / 2f),
                        circle.width / 2, new Scalar(0, 255, 0), 2);
            }

            HighGui.imshow("Circle Detection", frame);

            // Wait for key press and exit if "q" is pressed
        } while (HighGui.waitKey(1) != 'q');
    }

}

