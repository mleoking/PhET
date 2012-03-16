// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.proteinlevelsincell;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.proteinlevelsincell.view.ProteinLevelsInCellCanvas;

/**
 * @author John Blanco
 */
public class ProteinLevelsInCellModule extends Module {
    public ProteinLevelsInCellModule( String name ) {
        this( name, new ManualGeneExpressionModel() );
        setClockControlPanel( null );
    }

    private ProteinLevelsInCellModule( String name, ManualGeneExpressionModel model ) {
        // TODO: i18n
        super( name, model.getClock() );
        setSimulationPanel( new ProteinLevelsInCellCanvas( model ) );
        reset();
    }
}
