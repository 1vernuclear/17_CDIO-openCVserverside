package PointDetection;

import org.opencv.core.Point;

public class LineSegment {
    private Point startPoint;
    private Point endPoint;

    public LineSegment(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public double getLength() {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

