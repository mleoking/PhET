/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.AbstractScreenGraphic;
import edu.colorado.phet.waveinterference.view.ColorMap;
import edu.colorado.phet.waveinterference.view.CurveScreenGraphic;

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
        screenGraphic = new CurveScreenGraphic( getWaveModel(), getLatticeScreenCoordinates() );

        getPhetPCanvas().addScreenChild( screenGraphic );
        getPhetPCanvas().removeScreenChild( getWaveModelGraphic() );
        getPhetPCanvas().addScreenChild( getWaveModelGraphic() );
        getWaveModelGraphic().setOffset( 100, 100 );
        screenGraphic.setOffset( getWaveModelGraphic().getFullBounds().getMaxX(), getWaveModelGraphic().getFullBounds().getY() );
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
