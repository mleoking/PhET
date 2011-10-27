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
        BeakerNode solutionBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), 0.5, Strings.SOLUTION, new PDimension( 120, 50 ), new PhetFont( Font.BOLD, 20 ) );
        final PDimension solutionCylinderSize = solutionBeakerNode.getCylinderSize();
        final double solutionCylinderEndHeight = solutionBeakerNode.getCylinderEndHeight();
        SolutionNode solutionNode = new SolutionNode( solutionCylinderSize, solutionCylinderEndHeight, model.solution, model.getDiutionVolumeRange() );
        SolutionVolumeSliderNode solutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.VOLUME_V1, Strings.EMPTY, Strings.SMALL,
                                                                                          new PDimension( 5, 0.2 * solutionCylinderSize.getHeight() ), //TODO 0.2 is based on solution volume range
                                                                                          model.solution.volume, model.getSolutionVolumeRange() );

        // water nodes
        BeakerNode waterBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), 0.5, Symbols.WATER, new PDimension( 120, 50 ), new PhetFont( Font.BOLD, 20 ) );
        final PDimension waterCylinderSize = waterBeakerNode.getCylinderSize();
        final double waterCylinderEndHeight = waterBeakerNode.getCylinderEndHeight();
        SolutionNode waterNode = new SolutionNode( waterCylinderSize, waterCylinderEndHeight, model.water, model.getDiutionVolumeRange() );

        PNode equalsNode = new FancyEqualsNode();

        // dilution nodes
        BeakerNode dilutionBeakerNode = new BeakerNode( model.getMaxBeakerVolume(), 0.5, Strings.DILUTION, new PDimension( 120, 50 ), new PhetFont( Font.BOLD, 20 ) );
        final PDimension dilutionCylinderSize = dilutionBeakerNode.getCylinderSize();
        final double dilutionCylinderEndHeight = dilutionBeakerNode.getCylinderEndHeight();
        SolutionNode dilutionNode = new SolutionNode( dilutionCylinderSize, dilutionCylinderEndHeight, model.dilution, model.getDiutionVolumeRange() );
        SolutionVolumeSliderNode dilutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.VOLUME_V2, Strings.SMALL, Strings.BIG,
                                                                                          new PDimension( 5, 0.8 * dilutionCylinderSize.getHeight() ), //TODO 0.8 is based on dilution volume range
                                                                                          model.dilution.volume, model.getDiutionVolumeRange() );

        PDimension concentrationBarSize = new PDimension( 40, dilutionCylinderSize.getHeight() + 50 );
        //TODO this display is temporary, replace with a custom slider control
        ConcentrationDisplayNode solutionConcentrationNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M1, concentrationBarSize,
                                                                                           model.solution, model.getConcentrationRange() );
        ConcentrationDisplayNode dilutionConcentrationNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M2, concentrationBarSize,
                                                                                           model.dilution, model.getConcentrationRange() );

        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, new Color( 235, 235, 235 ) ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( solutionConcentrationNode );
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
            addChild( resetAllButtonNode );
        }

        // layout
        {
            final double waterBeakerYOffset = 200;
            solutionConcentrationNode.setOffset( 5 - PNodeLayoutUtils.getOriginXOffset( solutionConcentrationNode ),
                                                 waterBeakerYOffset + waterCylinderSize.getHeight() - concentrationBarSize.getHeight() );
            solutionVolumeSliderNode.setOffset( solutionConcentrationNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 5,
                                                waterBeakerYOffset + waterCylinderSize.getHeight() - ( 0.2 * solutionCylinderSize.getHeight() ) ); //TODO 0.2 is based on solution volume range
            solutionBeakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionBeakerNode ) + 5,
                                          waterBeakerYOffset );
            solutionNode.setOffset( solutionBeakerNode.getOffset() );
            waterBeakerNode.setOffset( solutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( waterBeakerNode ) + 5,
                                       waterBeakerYOffset );
            waterNode.setOffset( waterBeakerNode.getOffset() );
            equalsNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 5,
                                  waterBeakerNode.getYOffset() + ( waterCylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            dilutionBeakerNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 25,
                                          waterBeakerNode.getYOffset() );
            dilutionNode.setOffset( dilutionBeakerNode.getOffset() );
            dilutionVolumeSliderNode.setOffset( dilutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionVolumeSliderNode ) + 5,
                                                dilutionBeakerNode.getYOffset() );
            dilutionConcentrationNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionConcentrationNode ) + 5,
                                                 dilutionBeakerNode.getFullBoundsReference().getMaxY() - dilutionConcentrationNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( dilutionConcentrationNode ) );
            resetAllButtonNode.setOffset( ( getStageSize().getWidth() / 2 ) - ( resetAllButtonNode.getFullBoundsReference().getWidth() / 2 ),
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - 15 );
        }
    }

    public void reset() {
        // do nothing
    }
}
