/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:05:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class IntensityReaderSet extends PNode {
    private ArrayList intensityReaders = new ArrayList();

    public void addIntensityReader( PhetPCanvas phetPCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        addIntensityReader( phetPCanvas, waveModel, latticeScreenCoordinates, 300, 300 );
    }

    public void addIntensityReader( PhetPCanvas phetPCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, double x, double y ) {
        final IntensityReaderDecorator intensityReader = new IntensityReaderDecorator( phetPCanvas, waveModel, latticeScreenCoordinates );
        intensityReader.addListener( new IntensityReaderDecorator.Listener() {
            public void deleted() {
                intensityReaders.remove( intensityReader );
                removeChild( intensityReader );
            }
        } );
        intensityReader.setOffset( x, y );
        addChild( intensityReader );
        intensityReaders.add( intensityReader );
    }

    public void update() {
        for( int i = 0; i < intensityReaders.size(); i++ ) {
            IntensityReaderDecorator reader = (IntensityReaderDecorator)intensityReaders.get( i );
            reader.update();
        }
    }
}
