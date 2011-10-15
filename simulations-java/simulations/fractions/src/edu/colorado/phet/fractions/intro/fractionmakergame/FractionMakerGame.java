// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.fractionmakergame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class FractionMakerGame extends AbstractFractionsModule {
    public FractionMakerGame() {
        super( "Fraction Maker Game" );
        setSimulationPanel( new PhetPCanvas() );
    }
}