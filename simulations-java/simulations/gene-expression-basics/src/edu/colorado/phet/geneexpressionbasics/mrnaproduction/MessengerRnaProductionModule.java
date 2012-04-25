// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.model.MessengerRnaProductionModel;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.view.MessengerRnaProductionCanvas;

/**
 * @author John Blanco
 */
public class MessengerRnaProductionModule extends Module {
    public MessengerRnaProductionModule( String name ) {
        this( name, new MessengerRnaProductionModel() );
        setClockControlPanel( null );
    }

    private MessengerRnaProductionModule( String name, MessengerRnaProductionModel model ) {
        // TODO: i18n
        super( name, model.getClock() );
        setSimulationPanel( new MessengerRnaProductionCanvas( model ) );
        reset();
    }
}
