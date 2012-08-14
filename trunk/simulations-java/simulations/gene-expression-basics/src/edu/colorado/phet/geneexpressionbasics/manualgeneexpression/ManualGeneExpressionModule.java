// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.ManualGeneExpressionCanvas;

/**
 * Main module for the tab where the user manually performs the steps for
 * expressing proteins within a cell.
 *
 * @author John Blanco
 */
public class ManualGeneExpressionModule extends Module {

    private static final long ZOOM_ANIMATION_TIME = 2000; // In milliseconds.

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

    public void setCanvasZoomedIn( boolean isZoomedIn ) {
        if ( isZoomedIn ) {
            canvas.zoomIn( ZOOM_ANIMATION_TIME );
        }
        else {
            canvas.zoomOut( ZOOM_ANIMATION_TIME );
        }
    }

    public ObservableProperty<Boolean> getCanvasZoomedInProperty() {
        return canvas.getZoomedInProperty();
    }

    @Override public void reset() {
        model.reset();
        canvas.reset();
    }
}
