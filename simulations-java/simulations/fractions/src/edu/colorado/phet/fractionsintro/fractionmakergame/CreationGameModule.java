// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.fractionmakergame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

/**
 * Module for "create fraction game"
 *
 * @author Sam Reid
 */
public class CreationGameModule extends AbstractFractionsModule {
    public CreationGameModule() {
        super( "Creation Game", new ConstantDtClock() );
        setSimulationPanel( new PhetPCanvas() );
    }
}