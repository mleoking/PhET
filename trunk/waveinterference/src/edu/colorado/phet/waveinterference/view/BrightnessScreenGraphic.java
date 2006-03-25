/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.nodes.PPath;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 10:35:15 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class BrightnessScreenGraphic extends AbstractScreenGraphic {
    private ArrayList stripes = new ArrayList();

    public BrightnessScreenGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( waveModel, latticeScreenCoordinates );
        update();
    }

    public void update() {
        removeAllStripes();
        for( int i = 0; i < getWaveModel().getHeight(); i++ ) {
            PPath path = new PPath();
            path.moveTo( 0, super.getYValue( i ) );
            path.lineTo( 0 + getDx(), getYValue( i ) - getDy() );
            addBrightnessStripe( path );
        }
    }

    private void removeAllStripes() {
        while( stripes.size() > 0 ) {
            removeStripe( (PPath)stripes.get( 0 ) );
        }
    }

    private void removeStripe( PPath path ) {
        removeChild( path );
        stripes.remove( path );
    }

    private void addBrightnessStripe( PPath path ) {
        addChild( path );
        stripes.add( path );
    }

}
