// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.control.DilutionsSliderNode.SoluteAmountSliderNode;
import edu.colorado.phet.dilutions.control.DilutionsSliderNode.SolutionVolumeSliderNode;
import edu.colorado.phet.dilutions.control.ShowValuesNode;
import edu.colorado.phet.dilutions.control.SoluteControlNode;
import edu.colorado.phet.dilutions.model.MolarityModel;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityCanvas extends AbstractDilutionsCanvas implements Resettable {

    private static final PDimension BEAKER_SIZE = new PDimension( 400, 450 ); // the height of controls and displays is based on the height of the beaker

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );

    public MolarityCanvas( MolarityModel model, Frame parentFrame ) {

        // nodes
        BeakerNode beakerNode = new BeakerNode( BEAKER_SIZE, model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, model.solution.solute, valuesVisible );
        SolutionNode solutionNode = new SolutionNode( BEAKER_SIZE, model.solution, model.getSolutionVolumeRange(), model.getConcentrationRange() );
        PrecipitateNode precipitateNode = new PrecipitateNode( model.solution, BEAKER_SIZE );
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute );
        ShowValuesNode showValuesNode = new ShowValuesNode( valuesVisible );
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, Color.YELLOW ) {{
            setConfirmationEnabled( false );
        }};
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( new PDimension( 40, BEAKER_SIZE.getHeight() ),
                                                                                          model.solution, model.getConcentrationRange(),
                                                                                          valuesVisible );
        SoluteAmountSliderNode soluteAmountSliderNode = new SoluteAmountSliderNode( new PDimension( 5, BEAKER_SIZE.getHeight() ), model.solution.soluteAmount, model.getSoluteAmountRange(), valuesVisible );
        SolutionVolumeSliderNode solutionVolumeSliderNode = new SolutionVolumeSliderNode( new PDimension( 5, 0.8 * BEAKER_SIZE.getHeight() ), model.solution.volume, model.getSolutionVolumeRange(), valuesVisible );

        // rendering order
        {
            addChild( solutionNode );
            addChild( beakerNode );
            addChild( precipitateNode ); //TODO should be behind beaker so that precipitate looks like it's in solution, but solution needs to be transparent
            addChild( concentrationDisplayNode );
            addChild( soluteAmountSliderNode );
            addChild( solutionVolumeSliderNode );
            addChild( showValuesNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // combo box on top
        }

        // layout
        {
            soluteControlNode.setOffset( 30, 30 );
            soluteAmountSliderNode.setOffset( soluteControlNode.getXOffset() - PNodeLayoutUtils.getOriginXOffset( soluteAmountSliderNode ),
                                              soluteControlNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( soluteAmountSliderNode ) + 20 );
            solutionVolumeSliderNode.setOffset( soluteAmountSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 30,
                                                soluteAmountSliderNode.getYOffset() );
            beakerNode.setOffset( solutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( beakerNode ) + 20,
                                  solutionVolumeSliderNode.getYOffset() );
            solutionNode.setOffset( beakerNode.getOffset() );
            precipitateNode.setOffset( beakerNode.getOffset() );
            concentrationDisplayNode.setOffset( beakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 50,
                                                soluteAmountSliderNode.getYOffset() );
            resetAllButtonNode.setOffset( concentrationDisplayNode.getFullBoundsReference().getMaxX() - resetAllButtonNode.getFullBoundsReference().getWidth() - 30,
                                          soluteControlNode.getYOffset() );
            showValuesNode.setOffset( resetAllButtonNode.getFullBoundsReference().getMinX() - showValuesNode.getFullBoundsReference().getWidth() - 20,
                                      soluteControlNode.getYOffset() );

        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}
