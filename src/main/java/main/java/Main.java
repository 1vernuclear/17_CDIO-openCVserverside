package main.java;

import org.opencv.core.Core;
import ObjectDetection.*;
import org.opencv.videoio.VideoCapture;

public class Main {

    //load the opencv library into the JVM at runtime
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        //print current version of opencv
        System.out.println(Core.VERSION);

        // Open the video capture
        VideoCapture videoCapture = new VideoCapture(0);

        RedRectangleDetection detectField = new RedRectangleDetection(videoCapture);

        //stop capturing
        videoCapture.release();

    }
}

