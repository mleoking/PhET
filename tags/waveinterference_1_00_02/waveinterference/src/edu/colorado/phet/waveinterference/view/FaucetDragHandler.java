/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 12:58:21 PM
 * Copyright (c) May 18, 2006 by Sam Reid
 */

public class FaucetDragHandler extends PDragEventHandler {

    private Oscillator oscillator;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private FaucetGraphic faucetGraphic;

    private Point2D origPosition;
    private Point origOscLoc;

    public FaucetDragHandler( FaucetGraphic faucetGraphic ) {
        this.oscillator = faucetGraphic.getOscillator();
        this.latticeScreenCoordinates = faucetGraphic.getLatticeScreenCoordinates();
        this.faucetGraphic = faucetGraphic;
    }

    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        origPosition = event.getPosition();
        origOscLoc = new Point( oscillator.getCenterX(), oscillator.getCenterY() );
    }

    protected void drag( PInputEvent event ) {
        if( origPosition == null || origOscLoc == null ) {
            startDrag( event );
        }
        Point offset = getOscillatorDragOffset( event );
        applyOffset( offset );
    }

    protected void applyOffset( Point offset ) {
        oscillator.setLocation( origOscLoc.x + offset.x, origOscLoc.y + offset.y );
    }

    private Point getOscillatorDragOffset( PInputEvent event ) {
        int dx = (int)latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( event.getPosition().getX() - origPosition.getX() );
        int dy = (int)latticeScreenCoordinates.toLatticeCoordinatesDifferentialY( event.getPosition().getY() - origPosition.getY() );
        return new Point( dx, dy );
    }

    protected void endDrag( PInputEvent event ) {
        origPosition = null;
        origOscLoc = null;
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public Point getOriginalOscillatorLocation() {
        return origOscLoc;
    }
}
