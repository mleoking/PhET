// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLSimSharing.Objects;
import edu.colorado.phet.beerslawlab.control.EvaporationControlNode;
import edu.colorado.phet.beerslawlab.control.RemoveSoluteButtonNode;
import edu.colorado.phet.beerslawlab.control.SoluteControlNode;
import edu.colorado.phet.beerslawlab.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.view.BLLFaucetNode;
import edu.colorado.phet.beerslawlab.view.BeakerNode;
import edu.colorado.phet.beerslawlab.view.ConcentrationModelDebugger;
import edu.colorado.phet.beerslawlab.view.DropperNode;
import edu.colorado.phet.beerslawlab.view.OutputFluidNode;
import edu.colorado.phet.beerslawlab.view.ShakerNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationCanvas extends BLLCanvas {

    public ConcentrationCanvas( final ConcentrationModel model, Frame parentFrame ) {

        // Nodes
        PNode beakerNode = new BeakerNode( model.beaker );
        BLLFaucetNode inputFaucetNode = new BLLFaucetNode( Objects.INPUT_FAUCET_SLIDER, model.inputFaucet );
        BLLFaucetNode outputFaucetNode = new BLLFaucetNode( Objects.OUTPUT_FAUCET_SLIDER, model.outputFaucet );
        PNode inputFluidNode = new OutputFluidNode( inputFaucetNode, 350, model.solution, model.inputFaucet );
        PNode outputFluidNode = new OutputFluidNode( outputFaucetNode, 100, model.solution, model.outputFaucet );
        PNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solute, model.soluteForm );
        PNode shakerNode = new ShakerNode( model.shaker, model.soluteForm );
        PNode dropperNode = new DropperNode( model.dropper, model.soluteForm );
        PNode evaporationControlNode = new EvaporationControlNode( ConcentrationModel.MAX_EVAPORATION_RATE, model.evaporationRate );
        PNode removeSoluteButtonNode = new RemoveSoluteButtonNode( model.solution );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode modelDebugNode = new ConcentrationModelDebugger( model );

        // rendering order
        {
            addChild( inputFluidNode );
            addChild( inputFaucetNode );
            addChild( outputFluidNode );
            addChild( outputFaucetNode );
            addChild( beakerNode );
            addChild( shakerNode );
            addChild( dropperNode );
            addChild( evaporationControlNode );
            addChild( removeSoluteButtonNode );
            addChild( resetAllButtonNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( modelDebugNode );
            }
            addChild( soluteControlNode ); // on top, because it has a combo box popup
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            // upper right
            soluteControlNode.setOffset( getStageSize().getWidth() - soluteControlNode.getFullBoundsReference().getWidth() - xMargin, yMargin );
            // left aligned below beaker
            evaporationControlNode.setOffset( beakerNode.getFullBoundsReference().getMinX(),
                                              getStageSize().getHeight() - evaporationControlNode.getFullBoundsReference().getHeight() - yMargin );
            // left of evaporation control
            removeSoluteButtonNode.setOffset( evaporationControlNode.getFullBoundsReference().getMaxX() + 10,
                                              evaporationControlNode.getFullBoundsReference().getCenterY() - ( removeSoluteButtonNode.getFullBoundsReference().getHeight() / 2 ) );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // below solute control panel
            modelDebugNode.setOffset( beakerNode.getFullBoundsReference().getMaxX() + 20, soluteControlNode.getFullBoundsReference().getMaxY() + 20 );
        }
    }
}
