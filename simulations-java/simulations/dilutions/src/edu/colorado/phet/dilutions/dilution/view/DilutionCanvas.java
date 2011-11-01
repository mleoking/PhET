// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.colorado.phet.dilutions.dilution.model.DilutionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionCanvas extends AbstractDilutionsCanvas {

    private static final double BEAKER_SCALE_X = 0.33;
    private static final double BEAKER_SCALE_Y = 0.50;
    private static final PhetFont BEAKER_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension BEAKER_LABEL_SIZE = new PDimension( 100, 50 );

    public DilutionCanvas( DilutionModel model, Frame parentFrame ) {

        // solution nodes
        BeakerNode solutionBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, Strings.SOLUTION, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT );
        final PDimension solutionCylinderSize = solutionBeakerNode.getCylinderSize();
        final double solutionCylinderEndHeight = solutionBeakerNode.getCylinderEndHeight();
        SolutionNode solutionNode = new SolutionNode( solutionCylinderSize, solutionCylinderEndHeight, model.solution, model.getDiutionVolumeRange() );
        DilutionsSliderNode solutionVolumeSliderNode = new DilutionsSliderNode( Strings.VOLUME_V1, Strings.EMPTY, Strings.SMALL,
                                                                                new PDimension( 5, 0.2 * solutionCylinderSize.getHeight() ), //TODO 0.2 is based on solution volume range
                                                                                model.solution.volume, model.getSolutionVolumeRange() );

        // water nodes
        BeakerNode waterBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, Symbols.WATER, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT );
        final PDimension waterCylinderSize = waterBeakerNode.getCylinderSize();
        final double waterCylinderEndHeight = waterBeakerNode.getCylinderEndHeight();
        SolutionNode waterNode = new SolutionNode( waterCylinderSize, waterCylinderEndHeight, model.water, model.getDiutionVolumeRange() );

        PNode equalsNode = new FancyEqualsNode();

        // dilution nodes
        BeakerNode dilutionBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), BEAKER_SCALE_X, BEAKER_SCALE_Y, Strings.DILUTION, BEAKER_LABEL_SIZE, BEAKER_LABEL_FONT );
        final PDimension dilutionCylinderSize = dilutionBeakerNode.getCylinderSize();
        final double dilutionCylinderEndHeight = dilutionBeakerNode.getCylinderEndHeight();
        SolutionNode dilutionNode = new SolutionNode( dilutionCylinderSize, dilutionCylinderEndHeight, model.dilution, model.getDiutionVolumeRange() );
        DilutionsSliderNode dilutionVolumeSliderNode = new DilutionsSliderNode( Strings.VOLUME_V2, Strings.SMALL, Strings.BIG,
                                                                                new PDimension( 5, 0.8 * dilutionCylinderSize.getHeight() ), //TODO 0.8 is based on dilution volume range
                                                                                model.dilution.volume, model.getDiutionVolumeRange() );

        PDimension concentrationBarSize = new PDimension( 20, dilutionCylinderSize.getHeight() + 50 );
        DilutionsSliderNode concentrationSliderNode = new DilutionsSliderNode( Strings.CONCENTRATION_M1, Strings.ZERO, Strings.HIGH,
                                                                               concentrationBarSize, model.solutionConcentration, model.getConcentrationRange() );
        ConcentrationDisplayNode dilutionConcentrationNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M2,
                                                                                           concentrationBarSize, model.dilution, model.getConcentrationRange() );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( concentrationSliderNode );
            addChild( solutionNode );
            addChild( solutionBeakerNode );
            addChild( solutionVolumeSliderNode );
            addChild( waterNode );
            addChild( waterBeakerNode );
            addChild( dilutionNode );
            addChild( dilutionBeakerNode );
            addChild( dilutionVolumeSliderNode );
            addChild( dilutionConcentrationNode );
            addChild( equalsNode );
            addWorldChild( resetAllButtonNode ); // don't add to root node, so this button isn't involved in centering of rootNode
        }

        // layout, all beakers vertically aligned
        {
            final double waterBeakerYOffset = 0;
            // far left, vertically aligned with bottom of beakers
            concentrationSliderNode.setOffset( 5 - PNodeLayoutUtils.getOriginXOffset( concentrationSliderNode ),
                                               waterBeakerYOffset + waterCylinderSize.getHeight() - concentrationBarSize.getHeight() );
            // to right of M1, vertically aligned with ticks on Solution beaker
            solutionVolumeSliderNode.setOffset( concentrationSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 5,
                                                waterBeakerYOffset + waterCylinderSize.getHeight() - ( 0.2 * solutionCylinderSize.getHeight() ) ); //TODO 0.2 is based on solution volume range
            // to right of V1
            solutionBeakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionBeakerNode ) + 5,
                                          waterBeakerYOffset );
            // in the same coordinate frame as the Solution beaker
            solutionNode.setOffset( solutionBeakerNode.getOffset() );
            // to right of the Solution beaker
            waterBeakerNode.setOffset( solutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( waterBeakerNode ) + 5,
                                       waterBeakerYOffset );
            // in the same coordinate frame as the Water beaker
            waterNode.setOffset( waterBeakerNode.getOffset() );
            // to right of the Water beaker
            equalsNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 5,
                                  waterBeakerNode.getYOffset() + ( waterCylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            // to right of Water equals sign, vertically aligned with bottom of beakers
            dilutionConcentrationNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionConcentrationNode ) + 5,
                                                 waterBeakerNode.getFullBoundsReference().getMaxY() - dilutionConcentrationNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( dilutionConcentrationNode ) );
            // to right of M2, vertically aligned with ticks on Dilution beaker
            dilutionVolumeSliderNode.setOffset( dilutionConcentrationNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionVolumeSliderNode ) + 8,
                                                waterBeakerNode.getYOffset() );
            // to right of V2
            dilutionBeakerNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionBeakerNode ) + 5,
                                          waterBeakerNode.getYOffset() );
            // in the same coordinate frame as the Dilution beaker
            dilutionNode.setOffset( dilutionBeakerNode.getOffset() );
            // upper-right corner of stage
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - 50, 50 );
        }
        scaleRootNodeToFitStage();
        centerRootNodeOnStage();
    }
}
