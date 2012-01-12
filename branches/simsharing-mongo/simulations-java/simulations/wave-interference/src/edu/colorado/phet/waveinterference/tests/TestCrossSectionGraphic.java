// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.ModuleApplication;
import edu.colorado.phet.waveinterference.view.CrossSectionGraphic;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 9:05:15 PM
 */

public class TestCrossSectionGraphic extends TestTopView {
    public TestCrossSectionGraphic() {
        super( "Cross Section" );
        CrossSectionGraphic crossSectionGraphic = new CrossSectionGraphic( getWaveModel(), getLatticeScreenCoordinates() );
        getPhetPCanvas().addScreenChild( crossSectionGraphic );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestCrossSectionGraphic() );
    }
}
