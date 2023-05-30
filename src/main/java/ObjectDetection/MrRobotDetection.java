package ObjectDetection;

import LineCreation.LineSegment;
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
    private LineSegment front;
    private Point frontCenter;


    public MrRobotDetection(Point[] area){
        System.arraycopy(area, 4, areaOfInterest, 0, areaOfInterest.length);
        Mat greenMask = detectRobot();
        front = findLinesegment(greenMask);
        frontCenter = determineFrontCenter();
        System.out.println(frontCenter.x + " and " + frontCenter.y);
    }

    private Point determineFrontCenter() {
        return new Point((front.getEndPoint().x + front.getStartPoint().x) / 2.0, (front.getEndPoint().y + front.getStartPoint().y) / 2.0);
    }

    private LineSegment findLinesegment(Mat greenMask) {

        // Apply the Hough Line Transform
        Mat lines = new Mat();
        Imgproc.HoughLines(greenMask, lines, 1, Math.PI / 180, 100);

        // Find the start and end points of a line
        int lineIndex = 0; // Index of the line you want to find the start and end points for

        double[] lineData = lines.get(lineIndex, 0);
        double rho = lineData[0];
        double theta = lineData[1];

        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        // Compute the start and end points of the line
        double x0 = cosTheta * rho;
        double y0 = sinTheta * rho;
        Point startPoint = new Point(Math.round(x0 - 1000 * sinTheta), Math.round(y0 + 1000 * cosTheta));
        Point endPoint = new Point(Math.round(x0 + 1000 * sinTheta), Math.round(y0 - 1000 * cosTheta));

        // Draw the line on the original image
        Imgproc.line(greenMask, startPoint, endPoint, new Scalar(0, 0, 255), 2);

        // Save the image with the line
        Imgcodecs.imwrite("result.jpg", greenMask);

        System.out.println(startPoint.x);
        System.out.println(startPoint.y);
        System.out.println(endPoint.x);
        System.out.println(endPoint.y);
        return new LineSegment(startPoint, endPoint);
    }


    public Mat detectRobot() {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the input image
        Mat image = Imgcodecs.imread("C:\\Users\\emil1\\OneDrive\\Documents\\GitHub\\17_CDIO-openCVserverside\\src\\main\\resources\\FieldImages\\InkedMrRobotBlackGreenEnds.jpg");

        Mat mask = Mat.zeros(image.size(), CvType.CV_8UC1);

        // Define the area of interest as a polygon
        MatOfPoint roi = new MatOfPoint(areaOfInterest[0], areaOfInterest[1], areaOfInterest[3], areaOfInterest[2]);
        MatOfPoint[] roiContours = { roi };

        // Fill the area of interest with white color (255) in the bitmask
        Imgproc.fillPoly(mask, List.of(roiContours), new Scalar(255));

        // Convert the frame to the HSV color space
        Mat hsvFrame = new Mat();
        Imgproc.cvtColor(image, hsvFrame, Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper thresholds for lighter green color in HSV
        Scalar greenLowerThreshold = new Scalar(40, 50, 50);
        Scalar greenUpperThreshold = new Scalar(80, 255, 255);

        //create green mask with defined bounds
        Mat greenMask =  new Mat();
        Core.inRange(hsvFrame, greenLowerThreshold, greenUpperThreshold, greenMask);

        //applying area of interest to green mask ie. painting all pixels outside the area black.
        Core.bitwise_and(greenMask, mask, greenMask);

        return greenMask;
    }


}

