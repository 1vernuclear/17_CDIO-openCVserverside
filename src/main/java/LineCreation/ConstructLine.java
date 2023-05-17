package LineCreation;

import org.opencv.core.*;

public class ConstructLine {

    public LineSegment constructLine(Point p1, Point p2, int imageWidth, int imageHeight) {
        // Calculate the slope and y-intercept of the line
        double slope = (p2.y - p1.y) / (p2.x - p1.x);
        double yIntercept = p1.y - slope * p1.x;

        // Define points for the line segment
        Point startPoint;
        Point endPoint;

        // Determine which edges of the image the line intersects
        if (slope > 0) { // intersects with top/bottom edges
            startPoint = new Point((0 - yIntercept) / slope, 0);
            endPoint = new Point((imageHeight - yIntercept) / slope, imageHeight);
        } else { // intersects with left/right edges
            startPoint = new Point(0, yIntercept);
            endPoint = new Point(imageWidth, slope * imageWidth + yIntercept);
        }

        // Construct and return the line segment
        return new LineSegment(startPoint, endPoint);
    }

}
