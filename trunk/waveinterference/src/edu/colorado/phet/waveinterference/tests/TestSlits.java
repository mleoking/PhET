/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.view.SlitControlPanel;
import edu.colorado.phet.waveinterference.view.SlitPotentialGraphic;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:35:59 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestSlits extends TestTopView {
    public TestSlits() {
        super( "Slits" );
        SlitPotential slitPotential = new SlitPotential( getWaveModel() );
//        VerticalDoubleSlit potential = new VerticalDoubleSlit( getWaveModel().getWidth(), getWaveModel().getHeight(), 40, 5, 10, 15, 100 );
        getWaveModel().setPotential( slitPotential );
        SlitPotentialGraphic slitPotentialGraphic = new SlitPotentialGraphic( slitPotential, getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( slitPotentialGraphic );
        getOscillator().setAmplitude( 2 );
        getControlPanel().addControlFullWidth( new SlitControlPanel( slitPotential ) );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestSlits() );
    }
}
