package ObjectDetection;

import LineCreation.LineSegment;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RedRectangleDetection {

    public RedRectangleDetection(VideoCapture videoCapture){
        //detectField(videoCapture);
    }

    /**
     * Method to be used in real time.
     * @param videoCapture live video capture.
     */
    public void detectField(VideoCapture videoCapture){

        Point[] corners = findCorners(findLines(retrieveFrame(videoCapture))); // find corners.
        //if (corners == null)
            System.out.println("field detection failed");
    }

    /**
     * method to test how well working the methods are using png images.
     */
    public void testRedRectangleDetection(){
        String imagePath = "src/main/resources/FieldImages/fieldwithcross.png";
        Mat frame = Imgcodecs.imread(imagePath);

        Point[] corners = findCorners(findLines(frame));
        drawCorners(corners, frame);
        for (Point x : corners){
            System.out.println("X coordinate = " + x.x + " AND y coordinate = " + x.y);
        }
    }

    /**
     * This method will draw green circles on each point received as input.
     * @param corners coordinates to draw at.
     * @param frame frame to draw on.
     */
    private void drawCorners(Point[] corners, Mat frame) {
        // Draw circles for each coordinate
        for (Point coordinate : corners) {
            Imgproc.circle(frame, coordinate, 5, new Scalar(0, 255, 0), -1);
        }

        // Display the frame
        HighGui.imshow("Frame", frame);
        HighGui.waitKey();

        frame.release();
    }

    /**
     * This method will retrieve a frame to analyze from the videocapture.
     * @param videoCapture the live video.
     * @return frame to analyze.
     */
    public Mat retrieveFrame(VideoCapture videoCapture){
        // Check if the VideoCapture object is opened successfully
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam.");
            return null;
        }

        // mat object to store frame
        Mat frame = new Mat();
        //String imagePath = null;

        if (videoCapture.read(frame)) { //reads next frame of videocapture into the frame variable.
            // Save the frame as a PNG file
            //imagePath = getRessourcePath();
            //Imgcodecs.imwrite(imagePath, frame);
            //System.out.println("Frame saved as " + imagePath);
        } else {
            System.out.println("Failed to capture a frame.");
        }
        return frame;
        //return (imagePath != null) ? imagePath : "no file";
    }

    /**
     * This method is mostly for testing purposes, and should just help create a generic url for an image path.
     * @return path.
     */
    private String getRessourcePath(){
        // Get the resource path
        URL resourceUrl = RedRectangleDetection.class.getClassLoader().getResource("resources");

        String resourcePath = null;

        // Check if the resource URL is not null
        if (resourceUrl != null) {
            // Convert the resource URL to a file path
            resourcePath = new File(resourceUrl.getFile()).getAbsolutePath();

        }

        return (resourcePath != null) ? resourcePath : "file not found";
    }

    /**
     * We loop through our list of lines and perform the findIntersection method on each pair.
     * We end up with a point array of all the corners.
     * @param lines the list of linesegments.
     * @return An array of points, with the coordinates of each corner.
     */
    private Point[] findCorners(List<LineSegment> lines) {

        Point[] corners = new Point[4];

        int j = 0;

        for (int i = 0; i < lines.size() / 2 ; i ++) {
            corners[i] = findIntersection(lines.get(j),lines.get(++j));
            j++;
        }

        return corners;
    }

    /**
     * Using simple math equations, we find the intersection between two lines.
     *
     * Problems experienced (Fixed), when we would have a vertical line, the slope value would be infinite.
     * To counter this problem, we made a check to see if the x values of the start- and endpoint
     * of a vertical line were the same. If this was the case we would set the boolean value "infiniteSlope"
     * to true in the LineSegments object, and skip the individual calculation of the slope (a)
     * and intersection with y-axis (b) for this linesegment.
     * The point of the vertical line would thereby be calculated with a different function as seen in the if statement.
     *
     * @param horizontal lineSegment.
     * @param vertical lineSegment.
     * @return intersection point of the two lines - equal to the corner.
     */
    private Point findIntersection(LineSegment horizontal, LineSegment vertical) {
        horizontal.determineEquation();
        vertical.determineEquation();

        double horizontalA = horizontal.getA();  // slope of line 1
        double horizontalB = horizontal.getB();  // y-intercept of line 1

        double verticalA = vertical.getA(); // slope of line 2
        double verticalB = vertical.getB();  // y-intercept of line 2

        if (vertical.isInfiniteSlope()){
            // Handle the case of a vertical line
            double y = horizontalA * vertical.getEndPoint().x + horizontalB;  // Calculate the y-coordinate of intersection
            return new Point(vertical.getEndPoint().x,y);
        }
        // Calculate the intersection point
        double x = (verticalB - horizontalB) / (horizontalA - verticalA);
        double y = horizontalA * x + horizontalB;

        return new Point(x,y);
    }

    /**
     * In this method we create a red bitmask for the frame.
     * We then divide the bitmask into 4 regions of equal size,
     * that is we divide right down the middle vertically and horizontally to get 4 areas of interest.
     * These areas will each contain one corner of the field.
     * We then loop through each area of interest to find one vertical and one horizontal line segment.
     * The linesegment will be stored in the lineSegments arraylist, where the first 2 entries
     * will be the top left corner.
     * The next two entries will form the top right corner.
     * The next two entries will form the bottom left corner.
     * The last two entries will form the bottom right corner.
     * @param frame the still image from the live video.
     * @return The list of line segments.
     */
    private List<LineSegment> findLines(Mat frame){
        //bit mask for all the red areas in the frame
        Mat redMask = findRedMask(frame);
        //redMask = applyCanny(redMask); //applying the canny edge detection algorithm for more precise detection.

        // Define the number of divisions and the size of each division
        int areaWidth = frame.cols() / 2;
        int areaHeight = frame.rows() / 2;

        List<LineSegment> lineSegments = new ArrayList<>();

        // Divide the bitmask frame into smaller areas and search for line segments
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                // Define the top-left and bottom-right corners of the area
                int startX = j * areaWidth;
                int startY = i * areaHeight;
                int endX = startX + areaWidth;
                int endY = startY + areaHeight;

                // Extract the area of interest from the bitmask frame
                Mat areaOfInterest = new Mat(redMask, new Rect(new Point(startX, startY), new Point(endX, endY)));

                // Find the horizontal and vertical lines in the area of interest
                lineSegments.add(findLinesegment(areaOfInterest, false, (j > 0), (i > 0), areaWidth, areaHeight));
                lineSegments.add(findLinesegment(areaOfInterest, true, (j > 0), (i > 0), areaWidth, areaHeight));
            }
        }

        //Just a test code to view results -- should be deleted when tested thoroughly! :D
        for (LineSegment x : lineSegments){
            System.out.println("Corner = (" + x.getStartPoint() + "," + x.getEndPoint() +")");
        }

        return lineSegments;
    }

    /**
     * This method finds the largest LineSegment in the binary image, and return the linesegment object.
     * To find the biggest line segments we use houghLinesP function.
     * @param binaryImage This image is derived from the original bitmask, but is diviided into four
     *                    smaller areas to easier find each corner.
     * @param vertical, if true the method will look for a vertical line segment.
     *                  If false we will be looking for a horizontal line segment
     * @param addToX Since we split our original frame up into smaller areas of interest,
     *               we need to add the pixels back into the final result of the coordinates.
     *               If X is true we will then be adding the width of the areaOfInterest
     *               to the X values of the starting and end point of the line segment.
     * @param addToY Same way idea as addToX but for the y values.
     * @param areaWidth The width of the area.
     * @param areaHeight The height of the area.
     * @return a linesegment.
     */
    private LineSegment findLinesegment(Mat binaryImage, boolean vertical, boolean addToX, boolean addToY, double areaWidth, double areaHeight) {
        Mat lines = new Mat();
        int rho = 1; // Distance resolution of the accumulator in pixels
        double theta = Math.PI / 180; // Angle resolution of the accumulator in radians
        int threshold = 100; // Minimum number of intersections to detect a line
        int minLineLength = 50; // Minimum length of a line in pixels
        int maxLineGap = 10; // Maximum gap between line segments allowed in pixels

        // The houghLinesP function helps us look for line shapes and patterns.
        Imgproc.HoughLinesP(binaryImage, lines, rho, theta, threshold, minLineLength, maxLineGap);

        double maxLineLength = 0;
        Point startPoint = new Point();
        Point endPoint = new Point();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];

            double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            double angle = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;

            //checking if vertical
            //if true we look for vertical, otherwise we look for horizontal, as seen by the degree constraints.
            if(vertical) {
                if (Math.abs(angle) >= 75 && Math.abs(angle) <= 105 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }else{
                if (Math.abs(angle) >= -25 && Math.abs(angle) <= 25 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }
        }
        if (addToX){ // here we add areawidth to x values, to make them fit with the original frame.
            startPoint.x += areaWidth;
            endPoint.x += areaWidth;
        }

        if (addToY){ // here we add areaHeight to y values, to make them fit with the original frame.
            startPoint.y += areaHeight;
            endPoint.y += areaHeight;
        }

        return new LineSegment(startPoint, endPoint);
    }

    /**
     * This method will give us the red mask, from detection colors within the red threshhold.
     * The binary mask is essentially a binary image, where all the red colors detected
     * are turned into white pixels, and those that are not red will be black.
     * We are also creating a mask to exclude the center region of the frame, which we are not interested in.
     * This mask will color all pixels black, within a radius of 100 pixels.
     * We will hereby avoid getting noise from the red cross in the middle,
     * that could possible interfere with the detection of the red corners.
     * @param frame The frame is thee image we are working with.
     * @return the binary image (red mask) that we will use for fuurther processing.
     */
    private Mat findRedMask(Mat frame){
        // Define the center region to exclude
        int centerX = frame.cols() / 2; // X-coordinate of the center
        int centerY = frame.rows() / 2; // Y-coordinate of the center
        int exclusionRadius = 100; // Radius of the center region to exclude

        // Create a mask to exclude the center region
        Mat mask = new Mat(frame.size(), CvType.CV_8UC1, Scalar.all(255));
        Imgproc.circle(mask, new Point(centerX, centerY), exclusionRadius, new Scalar(0), -1);

        //create hsv frame
        Mat hsvFrame = new Mat();
        //turn original frame into hsv frame for better color detection
        Imgproc.cvtColor(frame,hsvFrame,Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper thresholds for red color
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        Mat redMask = new Mat();
        //all red areas will be represented as white dots while non red areas will be black.
        Core.inRange(hsvFrame, lowerRed, upperRed, redMask);

        return redMask;
    }

    /**
     * NOTE ! Well this method should supposedly make the detection more clear.
     * Through testing however we get more precise result using the red mask alone,
     *  without the canny algorithm, hence the method is left unused.. for now..!
     *
     * The canny algorithm should make shape detection in binary image clear and more precise.
     * Hence we chose to apply canny to our binary image, where we end up with an edge image.
     * An edge image will highlight different regions, ie different colors (black or white),
     * since we were to look for a coherent region of white dots (red mask),
     * the region will end up resemble a line much more when looking at the different regions,
     * which is what we are looking for.
     * @param redMask The red mask is the binary mask we have already created.
     */
    private Mat applyCanny(Mat redMask){
        double threshold1 = 50;  // Lower threshold for the intensity gradient
        double threshold2 = 150; // Upper threshold for the intensity gradient
        Mat edges = new Mat();
        Imgproc.Canny(redMask, edges, threshold1, threshold2);
        return edges;
    }

}
