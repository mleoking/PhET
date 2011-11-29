// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.multiplecells.view.MultipleCellsCanvas;

/**
 * @author John Blanco
 */
public class MultipleCellsModule extends Module {
    public MultipleCellsModule( String name ) {
        this( name, new ManualGeneExpressionModel() );
        setClockControlPanel( null );
    }

    private MultipleCellsModule( String name, ManualGeneExpressionModel model ) {
        // TODO: i18n
        super( name, model.getClock() );
        setSimulationPanel( new MultipleCellsCanvas( model ) );
        reset();
    }
}
