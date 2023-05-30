package run;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CircleRecognition {

    public static void main(String[] args) throws IOException {

        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load image
        File file = new File("C:\\Users\\emil1\\OneDrive\\Documents\\17_CDIO\\src\\main\\resources\\table-tennis-balls-12-pc-_51_201a.jfif");
        BufferedImage image = ImageIO.read(file);
        Mat mat = Imgcodecs.imread(file.getPath());

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new org.opencv.core.Size(9, 9), 2, 2);

        // Detect circles using HoughCircles algorithm
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 100, 50, 0, 0);

        // Draw circles on the original image
        for (int i = 0; i < circles.cols(); i++) {
            double[] circle = circles.get(0, i);
            Point center = new Point(circle[0], circle[1]);
            int radius = (int) circle[2];
            Imgproc.circle(mat, center, radius, new Scalar(0, 255, 0), 3);
        }

        // Save output image
        String outputFilename = "circle_output.jpg";
        Imgcodecs.imwrite(outputFilename, mat);

        // Display output image
        BufferedImage outputImage = ImageIO.read(new File(outputFilename));



        displayImage(outputImage);

    }

    public static void displayImage(Image img)
    {
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
