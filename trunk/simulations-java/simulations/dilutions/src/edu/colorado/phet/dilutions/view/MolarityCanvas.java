// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
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

    private static final PDimension BEAKER_SIZE = new PDimension( 300, 350 ); // the height of controls and displays is based on the height of the beaker

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
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( new PDimension( 40, BEAKER_SIZE.getHeight() ), model.solution, model.getConcentrationRange(), valuesVisible );
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
            beakerNode.setOffset( 300, 200 );
            solutionNode.setOffset( beakerNode.getOffset() );
            precipitateNode.setOffset( beakerNode.getOffset() );
            soluteControlNode.setOffset( 50, 30 );
            soluteAmountSliderNode.setOffset( 100, solutionNode.getYOffset() );
            solutionVolumeSliderNode.setOffset( soluteAmountSliderNode.getFullBoundsReference().getMaxX() + 75,
                                                soluteAmountSliderNode.getYOffset() );
            concentrationDisplayNode.setOffset( solutionNode.getFullBoundsReference().getMaxX() + 100,
                                                soluteAmountSliderNode.getYOffset() );
            showValuesNode.setOffset( concentrationDisplayNode.getFullBoundsReference().getMaxX() + 10,
                                      concentrationDisplayNode.getFullBoundsReference().getMaxY() - 40 );
            resetAllButtonNode.setOffset( showValuesNode.getXOffset(), showValuesNode.getFullBoundsReference().getMaxY() + 10 );
        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}
