package run;

import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class findAreaOfInterestTask implements Callable<List<Point>> {

    VideoCapture videoCapture = null;

    public findAreaOfInterestTask(VideoCapture capture){
        this.videoCapture = capture;
    }

    public List<Point> call() throws Exception{
        RedRectangleDetection detectField = new RedRectangleDetection(this.videoCapture);
        return detectField.testRedRectangleDetection();

        //return detectField.detectField(this.videoCapture);
    }

}
