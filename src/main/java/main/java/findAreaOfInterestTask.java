package main.java;

import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Callable;

public class findAreaOfInterestTask implements Callable<Point[]> {

    VideoCapture videoCapture = null;

    public findAreaOfInterestTask(VideoCapture capture){
        this.videoCapture = capture;
    }

    public Point[] call() throws Exception{
        RedRectangleDetection detectField = new RedRectangleDetection(this.videoCapture);
        detectField.testRedRectangleDetection();

        return detectField.detectField(this.videoCapture);
    }




}
