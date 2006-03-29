/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.ScreenControlPanel;
import edu.colorado.phet.waveinterference.view.ScreenNode;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestScreenGraphic extends TestWaveColor {
    private ScreenNode screenGraphic;

    public TestScreenGraphic() {
        super( "Screen Graphic" );
        getWaveModel().setSize( 50, 50 );
        screenGraphic = new ScreenNode( getWaveModel(), getLatticeScreenCoordinates(), getWaveModelGraphic().getColorMap() );
        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );
        getWaveModelGraphic().setOffset( 100, 100 );

        getControlPanel().addControl( new ScreenControlPanel( screenGraphic ) );
        getWaveModelGraphic().addListener( new WaveModelGraphic.Listener() {
            public void colorMapChanged() {
                screenGraphic.setColorMap( getWaveModelGraphic().getColorMap() );
            }
        } );
    }

//    protected void setColorMap( ColorMap colorMap ) {
//        super.setColorMap( colorMap );
//        if( screenGraphic != null ) {
//            screenGraphic.setColorMap( colorMap );
//        }
//    }

    protected void step() {
        super.step();
        screenGraphic.updateScreen();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestScreenGraphic() );
    }
}
