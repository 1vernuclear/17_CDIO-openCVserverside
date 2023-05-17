package java;


import LineCreation.ConstructLine;
import LineCreation.LineSegment;
import org.junit.Test;
import org.opencv.core.Point;

import static org.junit.jupiter.api.Assertions.*;

public class LineCreationTest{

    ConstructLine line = new ConstructLine();

    @Test
    public void testLine(){
        LineSegment lineSegment = line.constructLine(new Point(50,150), new Point(150,50),720,480);
        assertEquals(new Point(200,0),lineSegment.getEndPoint());
    }


}