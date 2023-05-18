package LineCreation;

import org.opencv.core.*;

public class ConstructLine {
    public static LineEquation line;


    /**
     * This method creates the constants for the linear line that is determined based on the robots heading and given as
     * f ( x ) = a * x + b;
     * @param p2 is the point that marks  the front of the robot.
     * @param p1 iis the point that marks the back of the robot.
     * @return the lines equation
     */
    public static double[] constructLine(Point p1, Point p2) {
        double[] arr = new double[2];
        // Calculate the slope and y-intercept of the line
        arr[0]= (p2.y - p1.y) / (p2.x - p1.x);
        arr[1] = p1.y - arr[0] * p1.x;

        // Construct and return the line segment
        return arr;
    }

    /**
     * This method is supposed to determine what balls lies in a convinient path to pick up
     * So far this method will just determine what point lies closest to the line.
     * @param p2 front point of robot.
     * @param p1 backpoint of robot.
     * @param p3 An array of points of all the balls detected.
     * @return Not figures out yet. I think it should return a movement command
     * to corrigate the robots direction to pick up the nearest ball.
     */
    public void determineDistance(Point p1, Point p2, Point[] p3){
        //find the lines equation
        /*line = constructLine(p1, p2);

        double supposedYvalue;
        int closestPoint;
        double closestValue = 999;

        for (int i = 0; i < p3.length; i++) {
            supposedYvalue = p3[i].x * line.getA() + line.getB();
            if(Math.abs((supposedYvalue - p3[i].y)) < closestValue) {
                closestValue = Math.abs((supposedYvalue - p3[i].y));
                closestPoint = i;
            }
        }*/

    }


}
