package run;

import org.opencv.core.*;
        import org.opencv.imgproc.Imgproc;
        import org.opencv.videoio.VideoCapture;
        import org.opencv.videoio.Videoio;

        import javax.swing.*;
        import java.awt.image.BufferedImage;
        import java.awt.image.DataBufferByte;

public class CircleDetector {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0);
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 800);
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

        Mat frame = new Mat();
        JFrame window = new JFrame("Circle Detector");
        JLabel label = new JLabel();
        window.setContentPane(label);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        while (true) {

            if (camera.read(frame)) {

                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.medianBlur(grayFrame, grayFrame, 5);
                Mat circles = new Mat();
                Imgproc.HoughCircles(grayFrame, circles, Imgproc.HOUGH_GRADIENT, 1, grayFrame.rows() / 8, 200, 100, 0, 0);

                for (int i = 0; i < circles.cols(); i++) {
                    double[] circle = circles.get(0, i);
                    Point center = new Point(circle[0], circle[1]);
                    int radius = (int) circle[2];
                    Imgproc.circle(frame, center, radius, new Scalar(0, 0, 255), 3);
                }

                BufferedImage image = matToBufferedImage(frame);
                label.setIcon(new ImageIcon(image));
                window.pack();

            } else {
                System.out.println("Error: No camera detected!");
                break;
            }

        }

        camera.release();

    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
        return image;
    }

}