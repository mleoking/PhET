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
import edu.colorado.phet.beerslawlab.view.PrecipitateNode;
import edu.colorado.phet.beerslawlab.view.SaturatedIndicatorNode;
import edu.colorado.phet.beerslawlab.view.ShakerNode;
import edu.colorado.phet.beerslawlab.view.SolutionNode;
import edu.colorado.phet.beerslawlab.view.StockSolutionNode;
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
        PNode inputFluidNode = new OutputFluidNode( inputFaucetNode, 360, model.solution.solvent, model.inputFaucet ); //TODO derive height
        PNode outputFluidNode = new OutputFluidNode( outputFaucetNode, 1000, model.solution, model.outputFaucet ); //TODO derive height based on play area height
        PNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solute, model.soluteForm );
        PNode shakerNode = new ShakerNode( model.shaker, model.soluteForm );
        PNode dropperNode = new DropperNode( model.dropper, model.soluteForm );
        PNode stockSolutionNode = new StockSolutionNode( model.solution.solvent, model.solute, model.dropper, model.beaker, 15 ); //TODO get hole width from DropperNode
        PNode evaporationControlNode = new EvaporationControlNode( ConcentrationModel.MAX_EVAPORATION_RATE, model.evaporationRate );
        PNode removeSoluteButtonNode = new RemoveSoluteButtonNode( model.solution );
        PNode solutionNode = new SolutionNode( model.solution, model.beaker );
        PNode precipitateNode = new PrecipitateNode( model.solution, model.beaker );
        PNode saturatedIndicatorNode = new SaturatedIndicatorNode( model.solution );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode modelDebugNode = new ConcentrationModelDebugger( model );

        System.out.println( "dropperNode.getFullBoundsReference() = " + dropperNode.getFullBoundsReference() );
        // rendering order
        {
            addChild( inputFluidNode );
            addChild( inputFaucetNode );
            addChild( outputFluidNode );
            addChild( outputFaucetNode );
            addChild( stockSolutionNode );
            addChild( solutionNode );
            addChild( beakerNode );
            addChild( precipitateNode );
            addChild( saturatedIndicatorNode );
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
            // aligned with beaker
            solutionNode.setOffset( beakerNode.getOffset() );
            // aligned with beaker
            precipitateNode.setOffset( beakerNode.getOffset() );
            // centered towards bottom of beaker
            saturatedIndicatorNode.setOffset( beakerNode.getFullBoundsReference().getCenterX() - ( saturatedIndicatorNode.getFullBoundsReference().getWidth() / 2 ),
                                              beakerNode.getFullBoundsReference().getMaxY() - 0.25 * beakerNode.getFullBoundsReference().getHeight() );
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
