// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.*;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsResources.Strings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

public class ChemicalReactionsModule extends PiccoloModule {

    private final ChemicalReactionsCanvas canvas;

    public ChemicalReactionsModule( Frame parentFrame ) {
        super( Strings.TITLE__CHEMICAL_REACTIONS, new ConstantDtClock( 60 ) );

        // slow debugging mode
//        super( Strings.TITLE__CHEMICAL_REACTIONS, new ConstantDtClock( 1000 / 60, 0.001 ) );

        canvas = new ChemicalReactionsCanvas( getClock() );
        setSimulationPanel( canvas );
        setClockControlPanel( null );

        setControlPanel( null );
        setLogoPanelVisible( false );
    }

    public ChemicalReactionsCanvas getCanvas() {
        return canvas;
    }
}
