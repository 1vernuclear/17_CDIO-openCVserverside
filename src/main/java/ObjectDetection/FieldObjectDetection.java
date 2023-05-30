package ObjectDetection;

import LineCreation.LineSegment;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FieldObjectDetection {

    public static LineSegment[] crossLines = new LineSegment[2];

    public FieldObjectDetection(VideoCapture videoCapture, Point[] areaOfInterest) {
        String imagePath = "src/main/resources/FieldImages/fieldwithcross.png";
        Mat frame = Imgcodecs.imread(imagePath);
        Mat redCrossMask = createRedCrossMask(frame, areaOfInterest[4], areaOfInterest[5], areaOfInterest[7], areaOfInterest[6]);

        //Mat redCrossMask = createRedCrossMask(Objects.requireNonNull(RedRectangleDetection.retrieveFrame(videoCapture)), areaOfInterest[0], areaOfInterest[1], areaOfInterest[3], areaOfInterest[2]);
        fillObstableArray(redCrossMask);

    }

    private void fillObstableArray(Mat redCrossMask) {
        //vertical line
        crossLines[0] = findLinesegment(redCrossMask, true);

        //horizontal line
        crossLines[1] = findLinesegment(redCrossMask, false);

        for (LineSegment x : crossLines){
            System.out.println(x.getEndPoint() + " AND " + x.getStartPoint());
        }

    }

    public static Mat createRedCrossMask(Mat frame, Point topLeft, Point topRight, Point bottomRight, Point bottomLeft) {
        // Create a blank bitmask with the same size as the frame
        Mat mask = Mat.zeros(frame.size(), CvType.CV_8UC1);

        // Define the area of interest as a polygon
        MatOfPoint roi = new MatOfPoint(topLeft, topRight, bottomRight, bottomLeft);
        MatOfPoint[] roiContours = { roi };

        // Fill the area of interest with white color (255) in the bitmask
        Imgproc.fillPoly(mask, List.of(roiContours), new Scalar(255));

        // Convert the frame to the HSV color space
        Mat hsvFrame = new Mat();
        Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper bounds of the red color in HSV
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        // Create the red color mask using the defined bounds
        Mat redMask = new Mat();
        Core.inRange(hsvFrame, lowerRed, upperRed, redMask);

        // Apply the area of interest mask to the red color mask
        Core.bitwise_and(redMask, mask, redMask);

        return redMask;
    }

    private LineSegment findLinesegment(Mat binaryImage, boolean vertical) {
        Mat lines = new Mat();
        int rho = 1; // Distance resolution of the accumulator in pixels
        double theta = Math.PI / 180; // Angle resolution of the accumulator in radians
        int threshold = 100; // Minimum number of intersections to detect a line
        int minLineLength = 50; // Minimum length of a line in pixels
        int maxLineGap = 10; // Maximum gap between line segments allowed in pixels

        // The houghLinesP function helps us look for line shapes and patterns.
        Imgproc.HoughLinesP(binaryImage, lines, rho, theta, threshold, minLineLength, maxLineGap);

        double maxLineLength = 0;
        Point startPoint = new Point();
        Point endPoint = new Point();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];

            double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            double angle = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;

            //checking if vertical
            //if true we look for vertical, otherwise we look for horizontal, as seen by the degree constraints.
            if(vertical) {
                if (Math.abs(angle) >= 45 && Math.abs(angle) <= 135 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }else{
                if (Math.abs(angle) >= -55 && Math.abs(angle) <= 55 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }
        }

        return new LineSegment(startPoint, endPoint);
    }

}
