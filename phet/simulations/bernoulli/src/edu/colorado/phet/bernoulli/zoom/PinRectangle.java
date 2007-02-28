package edu.colorado.phet.bernoulli.zoom;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 4:09:21 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class PinRectangle {
    PhetVector a;
    PhetVector b;
    PhetVector c;
    PhetVector d;

    public PinRectangle( Rectangle2D.Double source ) {
        a = new PhetVector( source.getX(), source.getY() );
        b = new PhetVector( source.getX(), source.getY() + source.getHeight() );
        c = new PhetVector( source.getX() + source.getWidth(), source.getY() );
        d = new PhetVector( source.getX() + source.getWidth(), source.getY() + source.getWidth() );
    }

    public Rectangle2D.Double toRectangle() {
        return new Rectangle2D.Double( a.getX(), a.getY(), d.getX() - a.getX(), d.getY() - a.getY() );
    }

    public void moveTo( PinRectangle pr, double speed ) {
        this.a = new MoveTo2D( pr.a, speed ).moveCloser( a );
        this.b = new MoveTo2D( pr.b, speed ).moveCloser( b );
        this.c = new MoveTo2D( pr.c, speed ).moveCloser( c );
        this.d = new MoveTo2D( pr.d, speed ).moveCloser( d );
    }

}
