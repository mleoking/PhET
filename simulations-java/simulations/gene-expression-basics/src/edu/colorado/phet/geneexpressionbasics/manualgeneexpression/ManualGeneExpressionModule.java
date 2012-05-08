// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.ManualGeneExpressionCanvas;

/**
 * @author John Blanco
 */
public class ManualGeneExpressionModule extends Module {

    private final ManualGeneExpressionModel model;
    private final ManualGeneExpressionCanvas canvas;

    public ManualGeneExpressionModule( String name ) {
        this( name, new ManualGeneExpressionModel() );
        setClockControlPanel( null );
    }

    private ManualGeneExpressionModule( String name, ManualGeneExpressionModel model ) {
        super( name, model.getClock() );
        this.model = model;
        canvas = new ManualGeneExpressionCanvas( model );
        setSimulationPanel( canvas );
        reset();
    }

    @Override public void reset() {
        model.reset();
        canvas.reset();
    }
}
