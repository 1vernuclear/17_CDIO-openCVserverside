package LineCreation;

public class LineEquation {
    private double a;
    private double b;

    public LineEquation(double a, double b) {
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

