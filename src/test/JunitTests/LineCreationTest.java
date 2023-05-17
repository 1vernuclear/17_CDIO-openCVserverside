package JunitTests;

import LineCreation.ConstructLine;
import LineCreation.LineSegment;
import org.junit.*;
import org.opencv.core.Point;

public class LineCreationTest{

    ConstructLine line = new ConstructLine();

    @Test
    public void testLine(){
        LineSegment lineSegment = line.constructLine(new Point(50,150), new Point(150,50),720,480);
        Assert.assertEquals(new Point(200,0),lineSegment.getEndPoint());
    }


}