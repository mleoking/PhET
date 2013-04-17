// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.view.WaveSideView;
import edu.colorado.phet.waveinterference.view.WaveSideViewFull;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:42:11 AM
 */

public class TestBothViews extends TestTopView {
    private WaveSideView waveSideView;

    public TestBothViews() {
        super( "Test Both Views" );
        waveSideView = new WaveSideViewFull( getWaveModel(), getLatticeScreenCoordinates() );
        getPhetPCanvas().getLayer().addChild( waveSideView );
        getWaveModelGraphic().setCellDimensions( 4, 4 );
        getWaveModelGraphic().setOffset( 100, 300 );
        waveSideView.setOffset( 100, 100 );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestBothViews() );
    }
}
