// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.module;

import java.awt.Frame;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.chemicalreactions.ChemicalReactionsResources.Strings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.PNode;

public class ChemicalReactionsModule extends PiccoloModule {

    private final PNode root = new PNode();

    public ChemicalReactionsModule( Frame parentFrame ) {
        super( Strings.TITLE__CHEMICAL_REACTIONS, new ConstantDtClock() );

        setSimulationPanel( new PhetPCanvas() {{
            // Set up the canvas-screen transform.
            setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, ChemicalReactionsConstants.STAGE_SIZE ) );

            setBackground( ChemicalReactionsConstants.CANVAS_BACKGROUND_COLOR );

            addWorldChild( root );

        }} );
        setClockControlPanel( null );
    }
}
