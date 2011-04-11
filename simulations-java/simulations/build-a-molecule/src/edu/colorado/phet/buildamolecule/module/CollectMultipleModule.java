// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.view.CollectMultipleCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

public class CollectMultipleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final CollectMultipleCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public CollectMultipleModule( Frame parentFrame ) {
        super( BuildAMoleculeStrings.TITLE_COLLECT_MULTIPLE, new ConstantDtClock( 30 ) );

        // TODO: add in model

        setClockControlPanel( null );

        // Canvas
        canvas = new CollectMultipleCanvas();
        setSimulationPanel( canvas );

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    @Override
    public void reset() {
        // TODO: global reset entry point
    }
}
