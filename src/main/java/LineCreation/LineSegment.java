package LineCreation;

import org.opencv.core.Point;

public class LineSegment {
    private Point startPoint;
    private Point endPoint;
    private double a;
    private double b;
    private boolean infiniteSlope = false;

    public LineSegment(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        if(startPoint.x == endPoint.x)
            this.infiniteSlope = true;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public double getA(){return this.a;}
    public double getB(){return this.b;}

    public boolean isInfiniteSlope() {
        return infiniteSlope;
    }

    public void determineEquation(){
        if(!this.infiniteSlope) {
            double[] arr = ConstructLine.constructLine(this.startPoint, this.endPoint);
            this.a = arr[0];
            this.b = arr[1];
        }
    }
}
