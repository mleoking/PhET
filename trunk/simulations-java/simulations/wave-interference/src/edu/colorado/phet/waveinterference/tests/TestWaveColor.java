// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.view.ColorMap;
import edu.colorado.phet.waveinterference.view.WavelengthControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 */

public class TestWaveColor extends TestTopView {
    private WavelengthControlPanel wavelengthControlPanel;

    public TestWaveColor() {
        this( "Wave Color" );
    }

    public TestWaveColor( String name ) {
        super( name );
        wavelengthControlPanel = new WavelengthControlPanel( getWaveModelGraphic(), getOscillator() );
        getControlPanel().addControl( wavelengthControlPanel );

        getOscillator().setAmplitude( 2.5 );
    }

    protected void setColorMap( ColorMap colorMap ) {
        getWaveModelGraphic().setColorMap( colorMap );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestWaveColor() );
    }
}
