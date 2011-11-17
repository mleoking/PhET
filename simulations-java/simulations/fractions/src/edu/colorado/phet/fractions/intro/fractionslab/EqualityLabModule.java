// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.fractionslab;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class EqualityLabModule extends AbstractFractionsModule {
    public EqualityLabModule() {
        super( "Equality Lab" );
        setSimulationPanel( new PhetPCanvas() );
    }
}