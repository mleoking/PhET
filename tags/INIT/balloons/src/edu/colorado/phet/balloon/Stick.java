package edu.colorado.phet.balloon;

import phet.paint.Painter;

import java.awt.*;

public class Stick implements Painter {
    Point rel;
    BalloonPainter fip;
    MinusPainter pp;
    Charge ch;

    public Stick( BalloonPainter fip, Point rel, Charge ch, MinusPainter pp ) {
        this.ch = ch;
        this.fip = fip;
        this.rel = rel;
        this.pp = pp;
    }

    public void paint( Graphics2D g ) {
        //System.err.println("Paint working...");
        Point fippie = fip.getPosition();
        Point draw = new Point( fippie.x + rel.x, fippie.y + rel.y );
        pp.paint( ch, draw.x, draw.y, g );
    }
}
