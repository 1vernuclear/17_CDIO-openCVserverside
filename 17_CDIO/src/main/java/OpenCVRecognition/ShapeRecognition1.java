package main.java.OpenCVRecognition;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import java.util.ArrayList;
import java.util.List;

public class ShapeRecognition1 {
    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0);

        if(!camera.isOpened()) {
            System.out.println("Error: Could not open camera");
            System.exit(-1);
        }

        Mat frame = new Mat();
        Mat gray = new Mat();
        Mat thresh = new Mat();
        Mat hierarchy = new Mat();

        while(camera.read(frame)) {

            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);
            Imgproc.threshold(gray, thresh, 150, 255, Imgproc.THRESH_BINARY);

            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            for(int i = 0; i < contours.size(); i++) {

                MatOfPoint contour = contours.get(i);
                double area = Imgproc.contourArea(contour);

                if(area > 500) {

                    MatOfPoint2f approxCurve = new MatOfPoint2f();
                    MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

                    double perimeter = Imgproc.arcLength(contour2f, true);
                    Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * perimeter, true);

                    if(approxCurve.total() == 3) {
                        // Draw triangle
                        drawShape(frame, contour, new Scalar(0, 255, 0));
                    } else if(approxCurve.total() == 4) {
                        // Draw rectangle or square
                        double ratio = getAspectRatio(contour);
                        if(ratio >= 0.95 && ratio <= 1.05) {
                            drawShape(frame, contour, new Scalar(0, 0, 255));
                        } else {
                            drawShape(frame, contour, new Scalar(255, 0, 0));
                        }
                    } else if(approxCurve.total() == 5) {
                        // Draw pentagon
                        drawShape(frame, contour, new Scalar(0, 255, 255));
                    } else {
                        // Draw circle
                        drawShape(frame, contour, new Scalar(255, 255, 0));
                    }

                }

            }

            Imgproc.drawContours(frame, contours, -1, new Scalar(0, 0, 255), 2);

            HighGui.imshow("Shape Recognition", frame);
            if(HighGui.waitKey(1) == 27) {
                break;
            }

        }

        camera.release();
        HighGui.destroyAllWindows();

    }

    private static void drawShape(Mat image, MatOfPoint contour, Scalar color) {

        Point[] points = contour.toArray();
        for(int i = 0; i < points.length; i++) {
            Imgproc.line(image, points[i], points[(i+1)%points.length], color, 3);
        }

    }

    private static double getAspectRatio(MatOfPoint contour) {
        // Get bounding rectangle
        Point[] points = contour.toArray();
        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMax = Double.MIN_VALUE;
        for(int i = 0; i < points.length; i++) {
            if(points[i].x < xMin) {
                xMin = points[i].x;
            }
            if(points[i].y < yMin) {
                yMin = points[i].y;
            }
            if(points[i].x > xMax) {
                xMax = points[i].x;
            }
            if(points[i].y > yMax) {
                yMax = points[i].y;
            }
        }
        double width = xMax - xMin;
        double height = yMax - yMin;
        return width / height;
    }

}
