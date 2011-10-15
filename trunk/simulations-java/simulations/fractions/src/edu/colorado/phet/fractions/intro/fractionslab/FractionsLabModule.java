// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.fractionslab;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractions.intro.matchinggame.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class FractionsLabModule extends AbstractFractionsModule {
    public FractionsLabModule() {
        super( "Fractions Lab" );
        setSimulationPanel( new PhetPCanvas() );
    }
}