/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 6:32:34 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class ImageOscillatorPNode extends PNode {
    private Oscillator oscillator;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public ImageOscillatorPNode( Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, String url ) {
        this.oscillator = oscillator;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        PImage image = PImageFactory.create( url );
        addChild( image );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
    }

    private void updateLocation() {
        Point2D coord = latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );
        setOffset( coord.getX() - getFullBounds().getWidth(), coord.getY() - getFullBounds().getHeight() / 2.0 );
    }
}
