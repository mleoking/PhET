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
    private PImage imageNode;

    public ImageOscillatorPNode( Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, String url ) {
        this.oscillator = oscillator;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        imageNode = PImageFactory.create( url );
        addChild( imageNode );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
        oscillator.addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {
            }

            public void locationChanged() {
                updateLocation();
            }

            public void frequencyChanged() {
            }

            public void amplitudeChanged() {
            }
        } );
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    private void updateLocation() {
        Point2D coord = latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );
        setOffset( coord.getX() - getFullBounds().getWidth(), coord.getY() - getFullBounds().getHeight() / 2.0 );
    }

    public void update() {
    }

    public PImage getImageNode() {
        return imageNode;
    }

    public void reset() {
//        update();
//        updateLocation();
    }
}
