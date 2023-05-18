package ObjectDetection;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RedRectangleDetection {

    public RedRectangleDetection(VideoCapture videoCapture){
        //detectField(videoCapture);
    }

    public void detectField(VideoCapture videoCapture){

        Point[] corners = findRedBitMask(retrieveFrame(videoCapture));
        if (corners == null)
            System.out.println("field detection failed");
    }

    /**
     * method to test how well working the methods are using png images.
     */
    public void testRedRectangleDetection(){
        // Read the PNG file as a Mat object
        //String imagePath = getRessourcePath() + "/FieldImages/fieldwithtape.png";
        String imagePath = "src/main/resources/FieldImages/fieldwithcross.png";
        Mat frame = Imgcodecs.imread(imagePath);
        Point[] corners = findRedBitMask(frame);
        for (Point x : corners){
            System.out.println("X coordinate = " + x.x + " AND y coordinate = " + x.y);
        }
        drawCorners(corners, frame);
    }

    private void drawCorners(Point[] corners, Mat frame) {
        // Draw circles for each coordinate
        for (Point coordinate : corners) {
            Imgproc.circle(frame, coordinate, 5, new Scalar(0, 255, 0), -1);
        }

        // Display the frame
        HighGui.imshow("Frame", frame);
        HighGui.waitKey();

        frame.release();
    }

    public Mat retrieveFrame(VideoCapture videoCapture){
        // Check if the VideoCapture object is opened successfully
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam.");
            return null;
        }

        // mate object to store frame
        Mat frame = new Mat();
        //String imagePath = null;

        if (videoCapture.read(frame)) { //reads next frame of videocapture into the frame variable.
            // Save the frame as a PNG file
            //imagePath = getRessourcePath();
            //Imgcodecs.imwrite(imagePath, frame);
            //System.out.println("Frame saved as " + imagePath);
        } else {
            System.out.println("Failed to capture a frame.");
        }
        return frame;
        //return (imagePath != null) ? imagePath : "no file";
    }

    private String getRessourcePath(){
        // Get the resource path
        URL resourceUrl = RedRectangleDetection.class.getClassLoader().getResource("resources");

        String resourcePath = null;

        // Check if the resource URL is not null
        if (resourceUrl != null) {
            // Convert the resource URL to a file path
            resourcePath = new File(resourceUrl.getFile()).getAbsolutePath();

        }

        return (resourcePath != null) ? resourcePath : "file not found";
    }

    private Point[] findRedBitMask(Mat frame){
        Point[] corners = new Point[4];

        // Define the center region to exclude
        int centerX = frame.cols() / 2; // X-coordinate of the center
        int centerY = frame.rows() / 2; // Y-coordinate of the center
        int exclusionRadius = 100; // Radius of the center region to exclude

        // Create a mask to exclude the center region
        Mat mask = new Mat(frame.size(), CvType.CV_8UC1, Scalar.all(255));
        Imgproc.circle(mask, new Point(centerX, centerY), exclusionRadius, new Scalar(0), -1);

        //create hsv frame
        Mat hsvFrame = new Mat();
        //turn original frame into hsv frame for better color detection
        Imgproc.cvtColor(frame,hsvFrame,Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper thresholds for red color
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        //bit mask for all the red areas in the frame
        Mat redMask = new Mat();
        //all red areas will be represented as white dots while non red areas will be black.
        Core.inRange(hsvFrame, lowerRed, upperRed, redMask);

        // Extract the line edge points from the bitmask
        List<Point[]> lineEdgePoints = extractLineEdgePoints(redMask);

        // Print the coordinates of the line edge points
        for (Point[] edgePoints : lineEdgePoints) {
            System.out.println("Line Edge Points:");
            System.out.println("Point 1: " + edgePoints[0]);
            Point startPoint = edgePoints[0];
            System.out.println("Point 2: " + edgePoints[1]);
            Point endPoint = edgePoints[1];

            // Draw a green line on the frame
            Imgproc.line(frame, startPoint, endPoint, new Scalar(0, 255, 0), 2);
        }

        // Display the frame
        HighGui.imshow("Green Line", frame);
        HighGui.waitKey(0);

        //return findRectangleCorners(corners,redMask);
        //return  findParallelogramCorners(corners, redMask);
        return findShapeCorners(corners, redMask);
    }

    private static List<Point[]> extractLineEdgePoints(Mat bitmask) {
        // Apply Hough Line Transform to detect lines in the bitmask
        Mat lines = new Mat();
        Imgproc.HoughLinesP(bitmask, lines, 1, Math.PI / 180, 100, 0, 0);

        // Sort the lines into vertical and horizontal based on their angles
        List<Point[]> verticalLines = new ArrayList<>();
        List<Point[]> horizontalLines = new ArrayList<>();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0], y1 = line[1];
            double x2 = line[2], y2 = line[3];

            double angle = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;

            if (Math.abs(angle) < 45 || Math.abs(angle) > 135) {
                verticalLines.add(new Point[]{new Point(x1, y1), new Point(x2, y2)});
            } else {
                horizontalLines.add(new Point[]{new Point(x1, y1), new Point(x2, y2)});
            }
        }

        // Extract two points on each vertical line
        List<Point[]> verticalEdgePoints = extractEdgePoints(verticalLines);

        // Extract two points on the longest horizontal line
        List<Point[]> horizontalEdgePoints = extractEdgePoints(horizontalLines);

        // Combine the vertical and horizontal edge points
        List<Point[]> lineEdgePoints = new ArrayList<>();
        lineEdgePoints.addAll(verticalEdgePoints);
        lineEdgePoints.addAll(horizontalEdgePoints);

        return lineEdgePoints;
    }

    private static List<Point[]> extractEdgePoints(List<Point[]> lines) {
        List<Point[]> edgePoints = new ArrayList<>();

        if (lines.size() >= 2) {
            // Sort the lines by their length in descending order
            lines.sort((line1, line2) -> {
                double length1 = Math.sqrt(Math.pow(line1[0].x - line1[1].x, 2) + Math.pow(line1[0].y - line1[1].y, 2));
                double length2 = Math.sqrt(Math.pow(line2[0].x - line2[1].x, 2) + Math.pow(line2[0].y - line2[1].y, 2));
                return Double.compare(length2, length1);
            });

            // Get the two longest lines
            Point[] line1 = lines.get(0);
            Point[] line2 = lines.get(1);

            // Calculate the minimum distance between the two points
            double minDistance = 300;

            // Calculate the edge points on the lines
            Point[] edgePoints1 = calculateEdgePoints(line1, minDistance);
            Point[] edgePoints2 = calculateEdgePoints(line2, minDistance);

            // Add the line edge points to the list
            edgePoints.add(edgePoints1);
            edgePoints.add(edgePoints2);
        }

        return edgePoints;
    }

    private static Point[] calculateEdgePoints(Point[] line, double minDistance) {
        double midX = (line[0].x + line[1].x) / 2;
        double midY = (line[0].y + line[1].y) / 2;

        double angle = Math.atan2(line[1].y - line[0].y, line[1].x - line[0].x);
        double offsetX = Math.cos(angle) * minDistance;
        double offsetY = Math.sin(angle) * minDistance;

        Point[] edgePoints = new Point[2];
        edgePoints[0] = new Point(midX - offsetX, midY - offsetY);
        edgePoints[1] = new Point(midX + offsetX, midY + offsetY);

        return edgePoints;
    }


    private Point[] findRectangleCorners(Point[] corners, Mat redMask){
        // Find contours in the bitmask
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(redMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the contour with the largest area (assuming it represents the rectangle)
        double maxArea = -1;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > maxArea) {
                maxArea = contourArea;
                largestContour = contour;
            }
        }

        // Checks if the bitmask was created as expected
        if (largestContour == null)
            return null;

        // Find the bounding rectangle of the largest contour
        Rect boundingRect = Imgproc.boundingRect(largestContour);

        // Extract the corner coordinates of the bounding rectangle
        corners[0] = (new Point(boundingRect.x, boundingRect.y)); // Top-left corner
        corners[1] = (new Point(boundingRect.x + boundingRect.width, boundingRect.y)); // Top-right corner
        corners[2] = (new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height)); // Bottom-right corner
        corners[3] = (new Point(boundingRect.x, boundingRect.y + boundingRect.height)); // Bottom-left corner

        return corners;
    }

    private static Point[] findShapeCorners(Point[] corners, Mat redBitmask) {
        // Find contours in the red bitmask
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(redBitmask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest contour (assumed to be the shape inside the red bitmask)
        double maxArea = 0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        // Approximate the largest contour to a polygon
        MatOfPoint2f approx = new MatOfPoint2f();
        double epsilon = 0.01 * Imgproc.arcLength(new MatOfPoint2f(largestContour.toArray()), true);
        Imgproc.approxPolyDP(new MatOfPoint2f(largestContour.toArray()), approx, epsilon, true);

        // Get the corners of the approximated polygon
        corners = approx.toArray();

        return corners;
    }

    private static Point[] findParallelogramCorners(Point[] corners, Mat bitmask) {
        // Find contours in the bitmask
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bitmask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest contour (assumed to be the parallelogram shape)
        double maxArea = 0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        // Approximate the largest contour to a polygon
        MatOfPoint2f approx = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(largestContour.toArray()), approx, 0.01 * Imgproc.arcLength(new MatOfPoint2f(largestContour.toArray()), true), true);

        // Get the corners of the approximated polygon
        corners = approx.toArray();

        return corners;
    }

}
