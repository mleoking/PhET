// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:05:39 PM
 */

public class IntensityReaderSet extends PhetPNode {
    private ArrayList intensityReaders = new ArrayList();
    private boolean middle = false;

    public void addIntensityReader( String title, PhetPCanvas phetPCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, IClock clock ) {
        addIntensityReader( title, phetPCanvas, waveModel, latticeScreenCoordinates, 300, 300, clock );
    }

    public void addIntensityReader( String title, PhetPCanvas phetPCanvas, WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, double x, double y, IClock clock ) {
        final IntensityReaderDecorator intensityReader = new IntensityReaderDecorator( title, phetPCanvas, waveModel, latticeScreenCoordinates, clock );
        intensityReader.setConstrainedToMidline( middle );
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
        for ( int i = 0; i < intensityReaders.size(); i++ ) {
            IntensityReaderDecorator reader = (IntensityReaderDecorator) intensityReaders.get( i );
            reader.update();
        }
    }

    public void setConstrainedToMidline( boolean middle ) {
        this.middle = middle;
        for ( int i = 0; i < intensityReaders.size(); i++ ) {
            IntensityReaderDecorator intensityReaderDecorator = (IntensityReaderDecorator) intensityReaders.get( i );
            intensityReaderDecorator.setConstrainedToMidline( middle );
        }
    }

    public void reset() {
        while ( intensityReaders.size() > 0 ) {
            IntensityReaderDecorator intensityReaderDecorator = (IntensityReaderDecorator) intensityReaders.get( 0 );
            intensityReaderDecorator.delete();
        }
    }
}
