package ObjectDetection;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.CvType.*;

import java.util.ArrayList;
import java.util.List;

public class MrRobotDetection {

    private Point[] areaOfInterest = new Point[4];

    public MrRobotDetection(Point[] area){
        System.arraycopy(area, 4, areaOfInterest, 0, areaOfInterest.length);
    }

    public void detectRobot() {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the input image
        Mat image = Imgcodecs.imread("src/main/resources/FieldImages/detectMrRobot.jpg");

        MatOfPoint roiCorners = new MatOfPoint(areaOfInterest);

        // Create a mask for the ROI
        Mat mask = Mat.zeros(image.size(), CvType.CV_8UC1);
        Mat roiMask = new Mat();
        Imgproc.fillConvexPoly(mask, roiCorners, new Scalar(255));

        // Extract the ROI
        Mat roiImage = new Mat();
        image.copyTo(roiImage, mask);

        // Convert ROI image to HSV color space
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(roiImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper threshold values for red color detection in HSV
        Scalar lowerRed = new Scalar(0, 50, 50);
        Scalar upperRed = new Scalar(10, 255, 255);

        // Thresholding to detect red objects
        Mat redMask = new Mat();
        Core.inRange(hsvImage, lowerRed, upperRed, redMask);

        // Find contours of red objects
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(redMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter contours based on desired criteria (e.g., contour area)
        List<MatOfPoint> filteredContours = new ArrayList<>();
        double minContourArea = 100; // Adjust as per your requirement
        for (MatOfPoint contour : contours) {
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > minContourArea) {
                filteredContours.add(contour);
            }
        }

        // Draw bounding boxes or contours around the filtered contours
        Mat result = new Mat();
        image.copyTo(result);
        Imgproc.drawContours(result, filteredContours, -1, new Scalar(0, 0, 255), 2);

        // Display the result
        HighGui.imshow("Result", result);
        HighGui.waitKey();

        // Clean up resources
        HighGui.destroyAllWindows();
    }

}

