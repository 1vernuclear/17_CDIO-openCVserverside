package LineCreation;

import org.opencv.core.Point;

public class LineSegment {
    private double a;
    private double b;

    public LineSegment(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double getA() {
        return this.a;
    }

    public double getB() {
        return this.b;
    }

    /*public double getLength() {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;
        return Math.sqrt(dx * dx + dy * dy);
    }*/
}

