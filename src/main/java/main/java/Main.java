package main.java;

import org.opencv.core.Core;
import ObjectDetection.*;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    //load the opencv library into the JVM at runtime
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        //print current version of opencv
        System.out.println(Core.VERSION);

        // Open the video capture
        //VideoCapture videoCapture = new VideoCapture(0);

        //variable for testing
        VideoCapture videoCapture = null;

        FieldObjectDetection fieldObjectDetection = new FieldObjectDetection(executorservice(videoCapture));

        //stop capturing
        videoCapture.release();

    }

    private static Point[] executorservice(VideoCapture videoCapture){
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Point[] areaOfInterest = new Point[4];

        // Create an instance of your task
        Callable<Point[]> task = new findAreaOfInterestTask(videoCapture);
        // Submit the task to the executor
        Future<Point[]> future = executor.submit(task);

        // Retrieve the result from the future object
        try {
            areaOfInterest = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Shutdown the executor when done
            executor.shutdown();
        }

        return areaOfInterest;
    }
}

