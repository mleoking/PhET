// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.colorado.phet.geneexpressionbasics.multiplecells.view.MultipleCellsCanvas;

/**
 * Main module file for the Multiple Cells tab.
 *
 * @author John Blanco
 */
public class MultipleCellsModule extends Module {

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final MultipleCellsModel model;
    private final MultipleCellsCanvas canvas;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public MultipleCellsModule( String name ) {
        this( name, new MultipleCellsModel() );
        setClockControlPanel( null );
    }

    /**
     * Workaround constructor for the issue where the model should really
     * create the clock, but the module needs it.
     *
     * @param name
     * @param model
     */
    private MultipleCellsModule( String name, MultipleCellsModel model ) {
        // TODO: i18n
        super( name, model.getClock() );

        // Keep reference to the model.
        this.model = model;

        // Create canvas.
        canvas = new MultipleCellsCanvas( model );
        setSimulationPanel( canvas );

        // Set initial state.
        reset();
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public void reset() {
        model.reset();
        canvas.reset();
    }
}
