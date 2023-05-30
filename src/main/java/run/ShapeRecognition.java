package run;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class ShapeRecognition {
    public static void main(String[] args) {
        // Load the image
        Mat image = Imgcodecs.imread("C:\\Users\\emil1\\OneDrive\\Documents\\GitHub\\17_CDIO-openCVserverside\\17_CDIO\\circle_output.jpg");

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Threshold the image
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 127, 255, Imgproc.THRESH_BINARY);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Loop over the contours
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);

            // Approximate the contour to a polygon
            MatOfPoint2f curve = new MatOfPoint2f(contour.toArray());
            MatOfPoint2f approx = new MatOfPoint2f();
            double epsilon = 0.01 * Imgproc.arcLength(curve, true);
            Imgproc.approxPolyDP(curve, approx, epsilon, true);

            // Determine the shape
            double area = Imgproc.contourArea(approx);
            if (approx.total() == 3) {
                System.out.println("Triangle with area " + area);
            } else if (approx.total() == 4) {
                Rect rect = Imgproc.boundingRect(contour);
                double aspectRatio = (double) rect.width / rect.height;
                if (aspectRatio >= 0.95 && aspectRatio <= 1.05) {
                    System.out.println("Square with area " + area);
                } else {
                    System.out.println("Rectangle with area " + area);
                }
            } else if (approx.total() == 5) {
                System.out.println("Pentagon with area " + area);
            } else {
                System.out.println("Circle with area " + area);
            }
        }
    }
}
