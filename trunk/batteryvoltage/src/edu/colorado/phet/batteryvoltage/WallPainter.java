package edu.colorado.phet.batteryvoltage;

import electron.paint.Painter;
import electron.paint.StrokedRectanglePainter;

import java.awt.*;

public class WallPainter implements Painter {
    StrokedRectanglePainter srp;

    public WallPainter( Rectangle r, int distFromWall ) {
        this.srp = new StrokedRectanglePainter( r.x - distFromWall, r.y - distFromWall, r.width + distFromWall * 2, r.height + distFromWall * 2, new BasicStroke( 5 ), Color.blue );
    }

    public void paint( Graphics2D g ) {
        srp.paint( g );
    }
}
