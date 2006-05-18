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

public class HorizontalFaucetDragHandler extends PDragEventHandler {

    private Oscillator oscillator;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private FaucetGraphic faucetGraphic;

    private Point2D origPosition;
    private Point origOscLoc;

    public HorizontalFaucetDragHandler( FaucetGraphic faucetGraphic ) {
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
        int dx = (int)latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( event.getPosition().getX() - origPosition.getX() );
        oscillator.setLocation( origOscLoc.x + dx, origOscLoc.y );
    }

    protected void endDrag( PInputEvent event ) {
        origPosition = null;
        origOscLoc = null;
    }
}
