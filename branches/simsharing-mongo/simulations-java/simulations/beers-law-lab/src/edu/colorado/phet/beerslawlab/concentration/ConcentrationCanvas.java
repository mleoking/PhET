// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.control.EvaporationControlNode;
import edu.colorado.phet.beerslawlab.control.RemoveSoluteButtonNode;
import edu.colorado.phet.beerslawlab.control.SoluteControlNode;
import edu.colorado.phet.beerslawlab.dev.ConcentrationModelButton;
import edu.colorado.phet.beerslawlab.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.view.BLLFaucetNode;
import edu.colorado.phet.beerslawlab.view.BeakerNode;
import edu.colorado.phet.beerslawlab.view.BeakerNode.TicksLocation;
import edu.colorado.phet.beerslawlab.view.ConcentrationMeterNode;
import edu.colorado.phet.beerslawlab.view.DropperNode;
import edu.colorado.phet.beerslawlab.view.OutputFluidNode;
import edu.colorado.phet.beerslawlab.view.PrecipitateNode;
import edu.colorado.phet.beerslawlab.view.SaturatedIndicatorNode;
import edu.colorado.phet.beerslawlab.view.ShakerNode;
import edu.colorado.phet.beerslawlab.view.ShakerParticlesNode;
import edu.colorado.phet.beerslawlab.view.SolutionNode;
import edu.colorado.phet.beerslawlab.view.StockSolutionNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationCanvas extends BLLCanvas {

    public ConcentrationCanvas( final ConcentrationModel model, Frame parentFrame ) {

        // Beaker and stuff inside it
        PNode beakerNode = new BeakerNode( model.beaker, TicksLocation.LEFT );
        SolutionNode solutionNode = new SolutionNode( model.solution, model.beaker );
        PNode precipitateNode = new PrecipitateNode( model.precipitate );
        PNode saturatedIndicatorNode = new SaturatedIndicatorNode( model.solution );

        // Shaker and Dropper
        PNode shakerNode = new ShakerNode( model.shaker );
        PNode shakerParticlesNode = new ShakerParticlesNode( model.shakerParticles );
        PNode dropperNode = new DropperNode( model.dropper );
        StockSolutionNode stockSolutionNode = new StockSolutionNode( model.solution.solvent, model.solute, model.dropper, model.beaker );

        // Faucets
        BLLFaucetNode solventFaucetNode = new BLLFaucetNode( UserComponents.solventFaucet, model.solventFaucet );
        BLLFaucetNode drainFaucetNode = new BLLFaucetNode( UserComponents.drainFaucet, model.drainFaucet );
        final double solventFluidHeight = model.beaker.getY() - model.solventFaucet.getY();
        OutputFluidNode solventFluidNode = new OutputFluidNode( model.solventFaucet, model.solution.solvent, solventFaucetNode.getFluidWidth(), solventFluidHeight );
        final double drainFluidHeight = 1000; // tall enough that resizing the play area is unlikely to show bottom of fluid
        OutputFluidNode drainFluidNode = new OutputFluidNode( model.drainFaucet, model.solution, drainFaucetNode.getFluidWidth(), drainFluidHeight );

        // Meter
        PNode concentrationMeterNode = new ConcentrationMeterNode( model.concentrationMeter, model.solution, solutionNode,
                                                                   model.solventFaucet, solventFluidNode, model.drainFaucet, drainFluidNode,
                                                                   model.dropper, stockSolutionNode );

        // Various controls
        PNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solute, model.soluteForm );
        PNode evaporationControlNode = new EvaporationControlNode( model.evaporator );
        PNode removeSoluteButtonNode = new RemoveSoluteButtonNode( model.solution, model.shakerParticles );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode modelButton = new PSwing( new ConcentrationModelButton( parentFrame, model ) );

        // rendering order
        {
            addChild( solventFluidNode );
            addChild( solventFaucetNode );
            addChild( drainFluidNode );
            addChild( drainFaucetNode );
            addChild( stockSolutionNode );
            addChild( solutionNode );
            addChild( beakerNode );
            addChild( precipitateNode );
            addChild( saturatedIndicatorNode );
            addChild( shakerNode );
            addChild( shakerParticlesNode );
            addChild( dropperNode );
            addChild( evaporationControlNode );
            addChild( removeSoluteButtonNode );
            addChild( resetAllButtonNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( modelButton );
            }
            addChild( concentrationMeterNode ); // on top, so it doesn't get lost behind anything
            addChild( soluteControlNode ); // on top, because it has a combo box popup
        }

        // layout
        {
            // NOTE: Nodes that have corresponding model elements handle their own offsets.
            final double xMargin = 20;
            final double yMargin = 20;
            // aligned with beaker
            solutionNode.setOffset( beakerNode.getOffset() );
            // aligned with beaker
            precipitateNode.setOffset( beakerNode.getOffset() );
            // centered towards bottom of beaker
            saturatedIndicatorNode.setOffset( beakerNode.getFullBoundsReference().getCenterX() - ( saturatedIndicatorNode.getFullBoundsReference().getWidth() / 2 ),
                                              beakerNode.getFullBoundsReference().getMaxY() - saturatedIndicatorNode.getFullBoundsReference().getHeight() - 30 );
            // upper right
            soluteControlNode.setOffset( getStageSize().getWidth() - soluteControlNode.getFullBoundsReference().getWidth() - xMargin, yMargin );
            // left aligned below beaker
            evaporationControlNode.setOffset( beakerNode.getFullBoundsReference().getMinX() + 20,
                                              beakerNode.getFullBoundsReference().getMaxY() + 20 );
            // left of evaporation control
            removeSoluteButtonNode.setOffset( evaporationControlNode.getFullBoundsReference().getMaxX() + 10,
                                              evaporationControlNode.getFullBoundsReference().getCenterY() - ( removeSoluteButtonNode.getFullBoundsReference().getHeight() / 2 ) );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
            // right-justified below solute control panel
            modelButton.setOffset( soluteControlNode.getFullBoundsReference().getMaxX() - modelButton.getFullBoundsReference().getWidth(),
                                   soluteControlNode.getFullBoundsReference().getMaxY() + 5 );
        }
    }
}
