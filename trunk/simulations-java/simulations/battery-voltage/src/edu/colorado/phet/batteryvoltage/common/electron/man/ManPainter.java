package edu.colorado.phet.batteryvoltage.common.electron.man;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.electron.paint.Painter;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class ManPainter implements Painter {
    Man man;
    Stroke s;
    Color c;

    public ManPainter( Man man, Stroke s, Color c ) {
        this.man = man;
        this.c = c;
        this.s = s;
    }

    /*The topleft pixel of a box containing the head.*/
    public DoublePoint getHeadRoot() {
        DoublePoint neck = man.getNeck().getPosition();
        return neck.add( new DoublePoint( -man.getHeadWidth() / 2, -man.getHeadHeight() ) );
    }

    public void paint( Graphics2D g ) {
        g.setColor( c );
        g.setStroke( s );

        DoublePoint leftEar = man.getLeftEar().getPosition();
        DoublePoint rightEar = man.getRightEar().getPosition();
        DoublePoint hair = man.getHair().getPosition();

        DoublePoint head = getHeadRoot();

        g.drawOval( (int) head.getX(), (int) head.getY(), (int) man.getHeadWidth(), (int) man.getHeadHeight() );

        paint( g, man.getHip() );
        paint( g, man.getLeftShoulder() );
        paint( g, man.getRightShoulder() );
        int x = (int) man.getNeck().getX();
        int y = (int) man.getNeck().getY();
        g.drawLine( x, y, (int) man.getHip().getX(), (int) man.getHip().getY() );
        g.drawLine( x, y, (int) man.getLeftShoulder().getX(), (int) man.getLeftShoulder().getY() );
        g.drawLine( x, y, (int) man.getRightShoulder().getX(), (int) man.getRightShoulder().getY() );
    }

    public void paint( Graphics2D g, Node root ) {
        int x = (int) root.getX();
        int y = (int) root.getY();
        for ( int i = 0; i < root.numChildren(); i++ ) {
            int a = (int) root.childAt( i ).getX();
            int b = (int) root.childAt( i ).getY();
            //o.O.p("Printing: "+a+", "+b+", "+x+", "+y);
            g.drawLine( x, y, a, b );
            paint( g, root.childAt( i ) );
        }
    }
}
