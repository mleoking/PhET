/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.AbstractScreenGraphic;
import edu.colorado.phet.waveinterference.view.BrightnessScreenGraphic;
import edu.colorado.phet.waveinterference.view.ColorMap;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestScreenGraphic extends TestWaveColor {
    private AbstractScreenGraphic screenGraphic;

    public TestScreenGraphic() {
        super( "Test Screen Graphic" );
        getWaveModel().setSize( 50, 50 );
        screenGraphic = new BrightnessScreenGraphic( getWaveModel(), getLatticeScreenCoordinates(), getWaveModelGraphic().getColorMap() );

        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );
        getWaveModelGraphic().setOffset( 100, 100 );
        screenGraphic.setOffset( getWaveModelGraphic().getFullBounds().getMaxX(), getWaveModelGraphic().getFullBounds().getY() );
//        getWaveModelGraphic().setBorderPaint( Color.black );
    }

    protected void setColorMap( ColorMap colorMap ) {
        super.setColorMap( colorMap );
        if( screenGraphic != null ) {
            screenGraphic.setColorMap( colorMap );
        }
    }

    protected void step() {
        super.step();
        screenGraphic.update();
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new TestScreenGraphic() );
    }
}
