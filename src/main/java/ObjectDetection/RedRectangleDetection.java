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
        Point point = new Point(137.0,540.0);
        Imgproc.circle(frame, point, 5, new Scalar(0, 255, 0), -1);


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

        return findRectangleCorners(corners,redMask);
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

}
