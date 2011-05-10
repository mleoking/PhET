// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.tests;

import junit.framework.TestCase;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;
import edu.colorado.phet.bendinglight.view.LaserColor;

/**
 * Test that the model produces the correct rays
 *
 * @author Sam Reid
 */
public class TestModel extends TestCase {
    public void testRays() {
        IntroModel introModel = new IntroModel( BendingLightModel.WATER );
        introModel.getLaser().color.set( new LaserColor.OneColor( 479E-9 ) );
        introModel.getLaser().on.set( true );
        for ( LightRay lightRay : introModel.getRays() ) {
            System.out.println( "lightRay = " + lightRay + ", length = " + lightRay.getLength() + ", angularFrequency = " + lightRay.getAngularFrequency() + ", time = " + lightRay.getTime() + ", frequency = " + lightRay.getFrequency() + ", wavelength = " + lightRay.getWavelength() + ", cosarg(0) = " + lightRay.getCosArg( 0 ) + ", phaseOffset = " + lightRay.getPhaseOffset() + ", numWavelengthsPhaseOffset = " + lightRay.getNumWavelengthsPhaseOffset() );
        }
    }
}
