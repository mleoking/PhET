package edu.colorado.phet.batteryvoltage;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.electron.paint.Painter;
import edu.colorado.phet.batteryvoltage.common.electron.paint.StrokedRectanglePainter;

public class WallPainter implements Painter {
    StrokedRectanglePainter srp;

    public WallPainter( Rectangle r, int distFromWall ) {
        this.srp = new StrokedRectanglePainter( r.x - distFromWall, r.y - distFromWall, r.width + distFromWall * 2, r.height + distFromWall * 2, new BasicStroke( 5 ), Color.blue );
    }

    public void paint( Graphics2D g ) {
        srp.paint( g );
    }
}
