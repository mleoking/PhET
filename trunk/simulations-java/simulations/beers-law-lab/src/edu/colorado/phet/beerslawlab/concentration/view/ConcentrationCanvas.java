// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationModel;
import edu.colorado.phet.beerslawlab.concentration.view.BeakerNode.TicksLocation;
import edu.colorado.phet.beerslawlab.concentration.view.ColorSchemeEditorDialog.ColorSchemeEditorButton;
import edu.colorado.phet.beerslawlab.concentration.view.ConcentrationModelDialog.ConcentrationModelButton;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
        PNode dropperNode = new DropperNode( model.dropper, model.solution.solvent, model.solution.solute );
        StockSolutionNode stockSolutionNode = new StockSolutionNode( model.solution.solvent, model.solute, model.dropper, model.beaker, DropperNode.TIP_WIDTH );

        // Faucets
        BLLFaucetNode solventFaucetNode = new BLLFaucetNode( UserComponents.solventFaucet, model.solventFaucet );
        BLLFaucetNode drainFaucetNode = new BLLFaucetNode( UserComponents.drainFaucet, model.drainFaucet );
        final double solventFluidHeight = model.beaker.location.getY() - model.solventFaucet.location.getY();
        FaucetFluidNode solventFluidNode = new FaucetFluidNode( model.solventFaucet, model.solution.solvent, solventFaucetNode.getFluidWidth(), solventFluidHeight );
        final double drainFluidHeight = 1000; // tall enough that resizing the play area is unlikely to show bottom of fluid
        FaucetFluidNode drainFluidNode = new FaucetFluidNode( model.drainFaucet, model.solution, drainFaucetNode.getFluidWidth(), drainFluidHeight );

        // Meter
        ConcentrationMeterNode concentrationMeterNode = new ConcentrationMeterNode( model.concentrationMeter, model.solution, model.dropper, solutionNode,
                                                                   stockSolutionNode, solventFluidNode, drainFluidNode );

        // Various controls
        PNode soluteControlNode = new SoluteControlsNode( model.getSolutes(), model.solute, model.soluteForm );
        PNode evaporationControlNode = new EvaporationControlNode( model.evaporator );
        PNode removeSoluteButtonNode = new RemoveSoluteButtonNode( model.solution, model.shakerParticles );
        PNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, BLLConstants.CONTROL_FONT_SIZE, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};
        PNode modelButton = new PSwing( new ConcentrationModelButton( parentFrame, model ) );
        PNode colorSchemeEditorButton = new PSwing( new ColorSchemeEditorButton( parentFrame, model ) );

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
            addChild( shakerParticlesNode );
            addChild( shakerNode );
            addChild( dropperNode );
            addChild( evaporationControlNode );
            addChild( removeSoluteButtonNode );
            addChild( resetAllButtonNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( modelButton );
                addChild( colorSchemeEditorButton );
            }
            addChild( concentrationMeterNode );
            addChild( soluteControlNode );
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
            // to the left of the model button
            colorSchemeEditorButton.setOffset( modelButton.getFullBoundsReference().getMinX() - colorSchemeEditorButton.getFullBoundsReference().getWidth() - 5,
                                               modelButton.getYOffset() );
        }

        // #3322 - If the body of the concentration meter is not fully inside the stage, shift it to the left.
        PNode bodyNode = concentrationMeterNode.bodyNode;
        if ( bodyNode.getFullBoundsReference().getMaxX() > getStageSize().getWidth() ) {
            final double x = getStageSize().getWidth() - bodyNode.getFullBoundsReference().getWidth() - 4;
            final double y = model.concentrationMeter.body.location.get().getY();
            model.concentrationMeter.body.location.set( new ImmutableVector2D( x, y ) );
        }
    }
}
