package OpenCVRecognition;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class WhiteBallDetector {
    public static void main(String[] args) {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //VideoCapture capture = new VideoCapture(0); while(true){ capture.open(0); }


        // Load YOLOv3 model files
        String modelConfiguration = "yolov3-tiny.cfg";
        String modelWeights = "yolov3-tiny.weights";
        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);

        // Set up video capture device
        VideoCapture capture = new VideoCapture(0);

        // Check if video capture device is open
        if (!capture.isOpened()) {
            System.out.println("Error opening video capture device!");
            return;
        }

        // Loop over frames in video stream
        Mat frame = new Mat();
        while (capture.read(frame)) {
            // Resize frame to YOLOv3 input size
            int inputSize = 416;
            Mat resized = new Mat();
            Imgproc.resize(frame, resized, new org.opencv.core.Size(inputSize, inputSize));

            // Convert image to blob format
            double scale = 1.0 / 255.0;
            org.opencv.core.Size imageSize = resized.size();
            Mat blob = Dnn.blobFromImage(resized, scale, imageSize, new Scalar(0), true, false);
            // Pass image through YOLOv3 model
            List<String> outputLayerNames = new ArrayList<>(List.of("yolo_82", "yolo_94", "yolo_106"));
            net.setInput(blob);
            MatOfRect detections = new MatOfRect();
            net.forward((List<Mat>) detections, outputLayerNames.toString());


            // Filter results to keep only white balls
            double confidenceThreshold = 0.5;
            Scalar color = new Scalar(0, 255, 0); // green
            Rect[] boundingBoxes = detections.toArray();
            for (Rect box : boundingBoxes) {
                // Get confidence score and class ID for this detection
                int classId = -1;
                double confidence = -1;
                for (int i = 5; i < detections.cols(); i++) {
                    double[] data = detections.get(0, i);
                    if (data[4] > confidence) {
                        confidence = data[4];
                        classId = (int) data[1];
                    }
                }

                // Check if the detected object is a white ball
                if (classId == 0 && confidence >= confidenceThreshold) {
                    // Draw bounding box around white ball
                    Imgproc.rectangle(frame, box.tl(), box.br(), color, 2);
                }
            }

            // Display the resulting frame
            String windowName = "White Ball Detector";
            Imgproc.putText(frame, "Press 'q' to quit", new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, color, 2);

            HighGui.imshow(windowName, frame);
            int key = HighGui.waitKey(1);
            if (key == 'q') {
                break;
            }
        }

        // Release video capture device and clean up resources
        capture.release();
        HighGui.destroyAllWindows();
    }
}