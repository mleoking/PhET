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
public class ZoomPins implements TickListener {
    DefaultClock clock;
    Rectangle2D.Double targetCoordinates;
    ModelViewTransform2d transform;
    WallRep targetRepresentation;
    private double speedx;
    private double speedy;
    private int numSteps;
    private double speedx2;
    private double speedy2;

    int step;

    public ZoomPins( DefaultClock clock, Rectangle2D.Double targetCoordinates, ModelViewTransform2d transform, int numSteps ) {
        this.numSteps = numSteps;
        this.targetRepresentation = new WallRep( targetCoordinates );
        WallRep initialRep = new WallRep( transform.getModelBounds() );
        this.clock = clock;
        this.targetCoordinates = targetCoordinates;
        this.transform = transform;
        this.speedx = ( targetRepresentation.getX() - initialRep.getX() ) / numSteps;
        this.speedy = ( targetRepresentation.getY() - initialRep.getY() ) / numSteps;
        this.speedx2 = ( targetRepresentation.getX2() - initialRep.getX2() ) / numSteps;
        this.speedy2 = ( targetRepresentation.getY2() - initialRep.getY2() ) / numSteps;
        step = 0;
    }

    public void clockTicked( AbstractClock source ) {
        Rectangle2D.Double current = transform.getModelBounds();
        if( current.equals( targetCoordinates ) || step >= numSteps ) {
            transform.setModelBounds( targetRepresentation.toRectangle() );
            clock.removeTickListener( this );
        }
        else {
//            PinRectangle currentPins=new PinRectangle(current);
            WallRep currentWallRep = new WallRep( current );
            currentWallRep.move( speedx, speedy, speedx2, speedy2 );
//            currentPins.moveTo(pr, speed);
            transform.setModelBounds( currentWallRep.toRectangle() );
            step++;
        }
    }


}
