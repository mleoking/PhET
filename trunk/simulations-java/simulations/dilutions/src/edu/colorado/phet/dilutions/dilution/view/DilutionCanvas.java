// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode.SolutionVolumeSliderNode;
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
public class DilutionCanvas extends AbstractDilutionsCanvas implements Resettable {

    public DilutionCanvas( DilutionModel model, Frame parentFrame ) {

        // solution nodes
        BeakerNode solutionBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.25, model.solution );
        final PDimension solutionCylinderSize = solutionBeakerNode.getCylinderSize();
        final double solutionCylinderEndHeight = solutionBeakerNode.getCylinderEndHeight();
        SolutionNode solutionNode = new SolutionNode( solutionCylinderSize, solutionCylinderEndHeight, model.solution, model.getSolutionVolumeRange() );
        SolutionVolumeSliderNode solutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.VOLUME_V1, Strings.VERY_SMALL, Strings.SMALL,
                                                                                          new PDimension( 5, 0.8 * solutionCylinderSize.getHeight() ), //TODO 0.8 is based on specific volume range
                                                                                          model.solution.volume, model.getSolutionVolumeRange() );

        // water nodes
        BeakerNode waterBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.5, model.water );
        final PDimension waterCylinderSize = waterBeakerNode.getCylinderSize();
        final double waterCylinderEndHeight = waterBeakerNode.getCylinderEndHeight();
        SolutionNode waterNode = new SolutionNode( waterCylinderSize, waterCylinderEndHeight, model.water, model.getSolutionVolumeRange() );

        PNode equalsNode = new FancyEqualsNode();

        // dilution nodes
        BeakerNode dilutionBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.5, model.dilution );
        final PDimension dilutionCylinderSize = dilutionBeakerNode.getCylinderSize();
        final double dilutionCylinderEndHeight = dilutionBeakerNode.getCylinderEndHeight();
        SolutionNode dilutionNode = new SolutionNode( dilutionCylinderSize, dilutionCylinderEndHeight, model.dilution, model.getSolutionVolumeRange() );
        SolutionVolumeSliderNode dilutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.VOLUME_V2, Strings.SMALL, Strings.BIG,
                                                                                          new PDimension( 5, 0.8 * dilutionCylinderSize.getHeight() ), //TODO 0.8 is based on specific volume range
                                                                                          model.dilution.volume, model.getSolutionVolumeRange() );
        PDimension concentrationBarSize = new PDimension( 40, dilutionCylinderSize.getHeight() + 50 );
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M2, concentrationBarSize,
                                                                                          model.dilution, model.getConcentrationRange() );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( solutionNode );
            addChild( solutionBeakerNode );
            addChild( solutionVolumeSliderNode );
            addChild( waterNode );
            addChild( waterBeakerNode );
            addChild( dilutionNode );
            addChild( dilutionBeakerNode );
            addChild( dilutionVolumeSliderNode );
            addChild( concentrationDisplayNode );
            addChild( equalsNode );
            addChild( resetAllButtonNode );
        }

        // layout
        {
            final double waterBeakerYOffset = 200;
            solutionVolumeSliderNode.setOffset( 5,
                                                waterBeakerYOffset + waterCylinderSize.getHeight() - solutionCylinderSize.getHeight() );
            solutionBeakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() + 20,
                                          solutionVolumeSliderNode.getYOffset() );
            solutionNode.setOffset( solutionBeakerNode.getOffset() );
            waterBeakerNode.setOffset( solutionBeakerNode.getFullBoundsReference().getMaxX() + 20,
                                       waterBeakerYOffset );
            waterNode.setOffset( waterBeakerNode.getOffset() );
            equalsNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 5,
                                  waterBeakerNode.getYOffset() + ( waterCylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            dilutionBeakerNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 25,
                                          waterBeakerNode.getYOffset() );
            dilutionNode.setOffset( dilutionBeakerNode.getOffset() );
            dilutionVolumeSliderNode.setOffset( dilutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 20,
                                                dilutionBeakerNode.getYOffset() );
            concentrationDisplayNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 20,
                                                dilutionBeakerNode.getFullBoundsReference().getMaxY() - concentrationDisplayNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( concentrationDisplayNode ) );
            resetAllButtonNode.setOffset( ( getStageSize().getWidth() / 2 ) - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - 15 );
        }
    }

    public void reset() {
        // do nothing
    }
}
