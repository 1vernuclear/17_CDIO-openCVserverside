import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.util.ArrayList;
import java.util.List;

public class RedRectangleDetectionVideoStream {

    public static void main(String[] args) {

        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Open the video capture
        VideoCapture capture = new VideoCapture(0);

        // Set the capture frame size
        //double maxWidth = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        //double aspectRatio = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT) / capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        //capture.set(Videoio.CAP_PROP_FRAME_WIDTH,capture.get(Videoio.CAP_PROP_FRAME_WIDTH));
        //capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));

        // Check if the video capture is open
        if (!capture.isOpened()) {
            System.out.println("Error opening video capture.");
            return;
        }

        // Create the video writer
        //Size frameSize = new Size(640,480);
        //VideoWriter writer = new VideoWriter("output.avi", VideoWriter.fourcc('M', 'J', 'P', 'G'), 25, frameSize);

        Mat frame = new Mat();
        Mat gray = new Mat();
        Mat edges = new Mat();
        Mat hierarchy = new Mat();

        // Loop over the frames of the video stream
        while (true) {

            // Read a frame from the video capture
            if (!capture.read(frame)) {
                break;
            }
            // Convert the frame to grayscale
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

            // Apply Canny edge detection
            int threshold1 = 50;
            int threshold2 = 200;
            Imgproc.Canny(gray, edges, threshold1, threshold2);

            // Find contours
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            // Find the largest contour with minimum size
            double maxArea = -1;
            int maxIdx = -1;
            for (int i = 0; i < contours.size(); i++) {
                double area = Imgproc.contourArea(contours.get(i));
                if (area > maxArea) {
                    Rect rect = Imgproc.boundingRect(contours.get(i));
                    if (rect.height >= 400 && rect.width >= 400) {
                        maxArea = area;
                        maxIdx = i;
                    }
                }
            }

            // Approximate the contour with a polygon
            if (maxIdx >= 0) {
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxIdx).toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                // Retrieve the coordinates of the polygon's corners
                Point[] corners = approxCurve.toArray();
                for (Point point : corners){
                    Imgproc.circle(frame,point,5,new Scalar(0, 255, 0),10);
                }
                /*
                // Draw the rectangle on the image
                Imgproc.line(frame, corners[0], corners[1], new Scalar(0, 255, 0), 2);
                Imgproc.line(frame, corners[1], corners[2], new Scalar(0, 255, 0), 2);
                Imgproc.line(frame, corners[2], corners[3], new Scalar(0, 255, 0), 2);
                Imgproc.line(frame, corners[3], corners[0], new Scalar(0, 255, 0), 2);
                */

            /*
            Imgproc.line(frame, new Point(0, frame.rows() / 2), new Point(frame.cols(), frame.rows() / 2), new Scalar(0, 255, 0), 1 );
            Imgproc.line(frame, new Point(frame.cols() / 2, 0), new Point(frame.cols() / 2, frame.rows()), new Scalar(0, 255, 0), 1 );
            Imgproc.circle(frame,new Point(0,0),50,new Scalar(0, 255, 0));
            Imgproc.circle(frame,new Point(frame.cols() / 2,frame.rows() / 2),50,new Scalar(0, 255, 0));
            System.out.println(frame.cols()/1 + " " + frame.rows()/1);

            // Convert the frame to HSV color space
            Mat hsvFrame = new Mat();
            Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);

            // Threshold the frame
            Scalar lowerRed = new Scalar(0, 100, 100);
            Scalar upperRed = new Scalar(10, 255, 255);
            Mat redMask = new Mat();
            Core.inRange(hsvFrame, lowerRed, upperRed, redMask);

            // Find contours
            Imgproc.findContours(redMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Check for rectangles
            for (MatOfPoint contour : contours) {
                MatOfPoint2f polygon = new MatOfPoint2f();
                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                Imgproc.approxPolyDP(contour2f, polygon, 0.05 * Imgproc.arcLength(contour2f, true), true);
                if (polygon.toArray().length == 4 && Imgproc.isContourConvex(new MatOfPoint(polygon.toArray()))) {
                    Scalar meanColor = Core.mean(frame, redMask);
                    if (meanColor.val[0] > meanColor.val[1] && meanColor.val[0] > meanColor.val[2]) {
                        Imgproc.drawContours(frame, contours, contours.indexOf(contour), new Scalar(0, 255, 0), 2);
                    }
                }
            }
             */
                // Write the frame to the video writer
                //writer.write(frame);

                // Display the result
                HighGui.imshow("Result", frame);
                if (HighGui.waitKey(1) == 27) {
                    break;
                }
            }

            // Release the video capture and video writer
            capture.release();
            //writer.release();

            // Close the window
            HighGui.destroyAllWindows();
        }
    }
}




