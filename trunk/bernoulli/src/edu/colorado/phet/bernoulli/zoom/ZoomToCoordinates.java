package edu.colorado.phet.bernoulli.zoom;

import edu.colorado.phet.coreadditions.clock2.AbstractClock;
import edu.colorado.phet.coreadditions.clock2.DefaultClock;
import edu.colorado.phet.coreadditions.clock2.TickListener;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 3:36:06 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class ZoomToCoordinates implements TickListener {
    DefaultClock clock;
    Rectangle2D.Double targetCoordinates;
    ModelViewTransform2d transform;
    double speedx;
    double speedy;
    MoveTo movex;
    MoveTo movey;
    MoveTo movewidth;
    MoveTo moveheight;

    public ZoomToCoordinates( DefaultClock clock, Rectangle2D.Double targetCoordinates, ModelViewTransform2d transform, double speed ) {
        this.speedx = speed;
        this.speedy = speed;
        this.movex = new MoveTo( targetCoordinates.x, speedx );
        this.movey = new MoveTo( targetCoordinates.y, speedy );
        this.movewidth = new MoveTo( targetCoordinates.width, speedx );
        this.moveheight = new MoveTo( targetCoordinates.height, speedy );
        this.clock = clock;
        this.targetCoordinates = targetCoordinates;
        this.transform = transform;
    }

    public void clockTicked( AbstractClock source ) {
        Rectangle2D.Double current = transform.getModelBounds();
        if( current.equals( targetCoordinates ) ) {
            clock.removeTickListener( this );
        }
        else {
            Rectangle2D.Double closer = new Rectangle2D.Double( movex.moveCloser( current.x ),
                                                                movey.moveCloser( current.y ),
                                                                movewidth.moveCloser( current.width ),
                                                                moveheight.moveCloser( current.height ) );
            transform.setModelBounds( closer );
        }
    }

    private double moveHeight( Rectangle2D.Double current ) {
        return 0;
    }

    private double moveWidth( Rectangle2D.Double current ) {
        return 0;
    }

    private double moveY( Rectangle2D.Double current ) {
        return 0;
    }

    private double moveX( Rectangle2D.Double current ) {
        return movex.moveCloser( current.x );
    }

}
