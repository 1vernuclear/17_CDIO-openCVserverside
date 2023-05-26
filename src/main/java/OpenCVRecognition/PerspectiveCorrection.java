package OpenCVRecognition;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import java.util.ArrayList;
import java.util.List;

public class PerspectiveCorrection {
    public static void main(String[] args) {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        // Load the source image
        Mat sourceImage = Imgcodecs.imread("C:\\Users\\miro\\OneDrive - Danmarks Tekniske Universitet\\DTU\\4.Semester\\CDIO-Project\\Billeder\\RobotBilleder\\p\\40.jpg");

        // Define the source points in the image (top-left, top-right, bottom-left, bottom-right)
        MatOfPoint2f sourcePoints = new MatOfPoint2f(
                new Point(0, 0),
                new Point(sourceImage.cols(), 0),
                new Point(0, sourceImage.rows()),
                new Point(sourceImage.cols(), sourceImage.rows())
        );

        // Define the destination points in the corrected image (top-left, top-right, bottom-left, bottom-right)
        MatOfPoint2f destinationPoints = new MatOfPoint2f(
                new Point(0, 0),
                new Point(500, 0),
                new Point(0, 300),
                new Point(500, 300)
        );

        // Compute the homography matrix
        Mat homographyMatrix = Calib3d.findHomography(sourcePoints, destinationPoints);

        // List of detected object coordinates (example)
        List<Point> detectedObjectCoordinates = new ArrayList<>();
        detectedObjectCoordinates.add(new Point(100, 100));
        detectedObjectCoordinates.add(new Point(200, 200));
        // Add coordinates of other detected objects

        // Correct the coordinates of detected objects using the homography transformation
        MatOfPoint2f objectPoints = new MatOfPoint2f();
        objectPoints.fromList(detectedObjectCoordinates);
        Core.perspectiveTransform(objectPoints, objectPoints, homographyMatrix);

        // Get the corrected coordinates
        List<Point> correctedObjectCoordinates = objectPoints.toList();

        // Print the corrected coordinates (example)
        for (Point point : correctedObjectCoordinates) {
            System.out.println("Corrected coordinates: " + point);
        }

        // Continue with your code for further processing or robot control
    }
}


//The code you provided demonstrates the usage of a perspective transformation to correct the coordinates of detected objects.
//However, the current implementation assumes a fixed destination area for the corrected image, which might not be suitable for your specific scenario.
//To address the issue of altered distances due to the camera perspective, you can make the following changes to your code:
//Determine the actual dimensions and layout of the course:
//Measure the physical dimensions of the course and determine the positions of the walls, goals, and obstacles accurately.
//This information will be crucial for calculating the corrected distances.
//Calibrate the camera: Perform camera calibration to estimate the intrinsic and extrinsic parameters of the camera.
//This calibration process will help you obtain more accurate measurements and correct for lens distortion.
//Use a more flexible approach for defining the destination points:
//Instead of hard-coding the destination points, you should dynamically calculate them based on the actual dimensions and layout of the course.
//For example, you can define the destination points as a percentage of the course dimensions, rather than fixed pixel values.
//This approach will make the code adaptable to different course sizes and configurations.
//Adjust the perspective transformation: Update the perspective transformation based on the calibrated camera parameters and the dynamically calculated destination points.
//This adjustment will ensure more accurate correction of the detected object coordinates.
//Implement feedback mechanism: During the execution of the robot's movements, continuously monitor and adjust the detected coordinates based on real-time feedback.
//This feedback can come from sensors on the robot or from additional camera frames, allowing you to refine the corrected coordinates throughout the task.
//Remember that achieving precise accuracy in this type of scenario can be challenging due to various factors such as lighting conditions, camera calibration accuracy, and the complexity of the environment.
// It may require iterative improvements and fine-tuning to achieve optimal results.
//Overall, the provided code serves as a starting point,
// but you'll need to incorporate the modifications mentioned above to address the perspective distortion and improve the accuracy of the corrected coordinates in your specific scenario.