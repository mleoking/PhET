// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.fractionmakergame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class CreationGameModule extends AbstractFractionsModule {
    public CreationGameModule() {
        super( "Creation Game" );
        setSimulationPanel( new PhetPCanvas() );
    }
}