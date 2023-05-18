package ObjectDetection;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.net.URL;

public class RedRectangleDetection {

    public RedRectangleDetection(VideoCapture videoCapture){
        detectField(videoCapture);
    }

    public void detectField(VideoCapture videoCapture){

        retrieveFrame(videoCapture);

    }

    public void retrieveFrame(VideoCapture videoCapture){
        // Check if the VideoCapture object is opened successfully
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam.");
            return;
        }

        // Read a frame from the video capture
        Mat frame = new Mat();
        if (videoCapture.read(frame)) {
            // Save the frame as a PNG file
            String imagePath = getRessourcePath();
            Imgcodecs.imwrite(imagePath, frame);
            System.out.println("Frame saved as " + imagePath);
        } else {
            System.out.println("Failed to capture a frame.");
        }
    }

    private String getRessourcePath(){
        // Get the resource path
        URL resourceUrl = RedRectangleDetection.class.getClassLoader().getResource("resources");

        String resourcePath = null;

        // Check if the resource URL is not null
        if (resourceUrl != null) {
            // Convert the resource URL to a file path
            resourcePath = new File(resourceUrl.getFile()).getAbsolutePath() + "/FieldImages/runtimeimage.png";

        }

        return (resourcePath != null) ? resourcePath : "file not found";
    }

}
