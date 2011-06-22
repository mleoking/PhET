// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.ManualGeneExpressionCanvas;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionModule extends Module {
    public ManualGeneExpressionModule( String name ) {
        this( name, new ManualGeneExpressionModel() );
        setClockControlPanel( null );
    }

    private ManualGeneExpressionModule( String name, ManualGeneExpressionModel model ) {
        // TODO: i18n
        super( name, model.getClock() );
        setSimulationPanel( new ManualGeneExpressionCanvas( model ) );
        reset();
    }
}
