import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CornerDetector {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        VideoCapture capture = new VideoCapture(0);

        if(!capture.isOpened()){
            System.out.println("Failed to open camera!");
            return;
        }

        //ViewResolution
        // Get the width and height of the video stream
        double width = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double height = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        // Print the resolution of the video stream
        System.out.println("The video stream has a resolution of " + width + "x" + height + " pixels");

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        Mat binaryFrame = new Mat();
        Mat dilatedFrame = new Mat();
        Mat hierarchy = new Mat();

        while(true){
            if(capture.read(frame)){
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(grayFrame, binaryFrame, 100, 255, Imgproc.THRESH_BINARY);
                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
                Imgproc.dilate(binaryFrame, dilatedFrame, kernel);

                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(dilatedFrame, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                List<Point> rectangleCorners = new ArrayList<>();
                for(MatOfPoint contour : contours){
                    MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                    double contourPerimeter = Imgproc.arcLength(contour2f, true);
                    MatOfPoint2f approxCurve = new MatOfPoint2f();
                    Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * contourPerimeter, true);

                    if(approxCurve.total() == 4){
                        Rect rect = Imgproc.boundingRect(new MatOfPoint(approxCurve.toArray()));
                        if(rect.width >= 400 && rect.height >= 200){
                            rectangleCorners.add(new Point(rect.x, rect.y));
                            rectangleCorners.add(new Point(rect.x + rect.width, rect.y));
                            rectangleCorners.add(new Point(rect.x + rect.width, rect.y + rect.height));
                            rectangleCorners.add(new Point(rect.x, rect.y + rect.height));
                        }
                    }
                    for(Point  point :rectangleCorners){
                        Imgproc.circle(frame,point,5,new Scalar(0, 255, 0),10);
                    }
                }
                System.out.println("Rectangle corners: " + rectangleCorners.toString());
                // do something with the corner points

            } else {
                System.out.println("Failed to capture frame!");
                break;
            }
        }

        capture.release();
    }

}
