// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.Frame;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsResources.Strings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

public class ChemicalReactionsModule extends PiccoloModule {

    public ChemicalReactionsModule( Frame parentFrame ) {
        super( Strings.TITLE__CHEMICAL_REACTIONS, new ConstantDtClock() );

        setSimulationPanel( new ChemicalReactionsCanvas() );
        setClockControlPanel( null );

        setControlPanel( null );
        setLogoPanelVisible( false );
    }

}
