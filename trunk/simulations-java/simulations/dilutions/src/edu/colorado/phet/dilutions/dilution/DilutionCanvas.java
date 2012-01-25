// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.DilutionsSimSharing.UserComponents;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.FancyEqualsNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionCanvas extends AbstractDilutionsCanvas {

    // properties common to all 3 beakers
    private static final double BEAKER_SCALE_X = 0.33;
    private static final double BEAKER_SCALE_Y = 0.50;
    private static final PhetFont BEAKER_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final PDimension BEAKER_LABEL_SIZE = new PDimension( 100, 50 );

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );

    public DilutionCanvas( final DilutionModel model, Frame parentFrame ) {

        // Solution beaker, with solution inside of it
        final BeakerNode solutionBeakerNode = new BeakerNode( UserComponents.solutionBeaker,
                                                              model.getMaxBeakerVolume(), Strings.UNITS_LITERS,
                                                              model.solution.getConcentration(), Strings.UNITS_MOLARITY,
                                                              BEAKER_SCALE_X, BEAKER_SCALE_Y, Strings.SOLUTION,
                                                              valuesVisible );
        final PDimension cylinderSize = solutionBeakerNode.getCylinderSize();
        SolutionNode solutionNode = new SolutionNode( cylinderSize, solutionBeakerNode.getCylinderEndHeight(), model.solution, model.getDilutionVolumeRange() );

        // M1 control (Solution concentration)
        PDimension concentrationBarSize = new PDimension( 20, cylinderSize.getHeight() + 50 );
        DilutionsSliderNode concentrationSliderNode = new DilutionsSliderNode( UserComponents.concentrationSlider,
                                                                               Strings.M1_CONCENTRATION,
                                                                               MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_MOLES_PER_LITER ),
                                                                               Strings.ZERO, Strings.HIGH,
                                                                               concentrationBarSize,
                                                                               new GradientPaint( 0f, 0f, model.solute.solutionColor.getMax(), 0f, (float) concentrationBarSize.getHeight(), model.solute.solutionColor.getMin() ),
                                                                               new Color( 0, 0, 0, 0 ), /* invisible track background */
                                                                               model.solutionConcentration, model.getConcentrationRange(),
                                                                               Strings.UNITS_MOLARITY, valuesVisible );

        // V1 control (Solution volume), sized to match tick marks on the beaker
        final double solutionVolumeSliderHeight = ( model.getSolutionVolumeRange().getLength() / model.getMaxBeakerVolume() ) * cylinderSize.getHeight();
        DilutionsSliderNode solutionVolumeSliderNode = new DilutionsSliderNode( UserComponents.solutionVolumeSlider,
                                                                                Strings.V1_VOLUME,
                                                                                MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_LITERS ),
                                                                                Strings.EMPTY, Strings.SMALL,
                                                                                new PDimension( 5, solutionVolumeSliderHeight ),
                                                                                model.solution.volume, model.getSolutionVolumeRange(),
                                                                                Strings.UNITS_LITERS, valuesVisible );

        // Water beaker, with water inside of it
        final BeakerNode waterBeakerNode = new BeakerNode( UserComponents.waterBeaker,
                                                           model.water.getConcentration(), Strings.UNITS_MOLARITY,
                                                           model.getMaxBeakerVolume(), Strings.UNITS_LITERS,
                                                           BEAKER_SCALE_X, BEAKER_SCALE_Y, Symbols.WATER,
                                                           valuesVisible );
        SolutionNode waterNode = new SolutionNode( cylinderSize, waterBeakerNode.getCylinderEndHeight(), model.water, model.getDilutionVolumeRange() );

        // "=" that separates left and right sides of dilution equation
        PNode equalsNode = new FancyEqualsNode();

        // dilution beaker, with solution inside of it
        final BeakerNode dilutionBeakerNode = new BeakerNode( UserComponents.dilutionBeaker,
                                                              model.getMaxBeakerVolume(), Strings.UNITS_LITERS,
                                                              model.dilution.getConcentration(), Strings.UNITS_MOLARITY,
                                                              BEAKER_SCALE_X, BEAKER_SCALE_Y, Strings.DILUTION,
                                                              valuesVisible );
        SolutionNode dilutionNode = new SolutionNode( cylinderSize, dilutionBeakerNode.getCylinderEndHeight(), model.dilution, model.getDilutionVolumeRange() );

        // M2 display (Dilution concentration)
        ConcentrationDisplayNode dilutionConcentrationNode = new ConcentrationDisplayNode( Strings.M2_CONCENTRATION,
                                                                                           MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_MOLARITY ),
                                                                                           concentrationBarSize,
                                                                                           model.dilution, model.getConcentrationRange(),
                                                                                           Strings.UNITS_MOLARITY, valuesVisible );

        // V2 control (Dilution volume), sized to match tick marks on the beaker
        final double dilutionVolumeSlider = ( model.getDilutionVolumeRange().getLength() / model.getMaxBeakerVolume() ) * cylinderSize.getHeight();
        DilutionsSliderNode dilutionVolumeSliderNode = new DilutionsSliderNode( UserComponents.dilutionVolumeSlider,
                                                                                Strings.V2_VOLUME,
                                                                                MessageFormat.format( Strings.PATTERN_PARENTHESES_0TEXT, Strings.UNITS_LITERS ),
                                                                                Strings.SMALL, Strings.BIG,
                                                                                new PDimension( 5, dilutionVolumeSlider ),
                                                                                model.dilution.volume, model.getDilutionVolumeRange(),
                                                                                Strings.UNITS_LITERS, valuesVisible );

        // Reset All button
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
            // all beakers must have the same height
            assert ( solutionBeakerNode.getCylinderSize().getHeight() == waterBeakerNode.getCylinderSize().getHeight() );
            assert ( waterBeakerNode.getCylinderSize().getHeight() == dilutionBeakerNode.getCylinderSize().getHeight() );

            final double waterBeakerYOffset = 0;
            // far left, vertically aligned with bottom of beakers
            concentrationSliderNode.setOffset( 5 - PNodeLayoutUtils.getOriginXOffset( concentrationSliderNode ),
                                               waterBeakerYOffset + cylinderSize.getHeight() - concentrationBarSize.getHeight() );
            // to right of M1, vertically aligned with ticks on Solution beaker
            solutionVolumeSliderNode.setOffset( concentrationSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( solutionVolumeSliderNode ) + 5,
                                                waterBeakerYOffset + cylinderSize.getHeight() - solutionVolumeSliderHeight );
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
                                  waterBeakerNode.getYOffset() + ( cylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            // to right of Water equals sign, vertically aligned with bottom of beakers
            dilutionConcentrationNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 20,
                                                 waterBeakerNode.getFullBoundsReference().getMaxY() - dilutionConcentrationNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( dilutionConcentrationNode ) );
            // to right of M2, vertically aligned with ticks on Dilution beaker
            dilutionVolumeSliderNode.setOffset( dilutionConcentrationNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionVolumeSliderNode ) + 8,
                                                waterBeakerNode.getYOffset() );
            // to right of V2
            dilutionBeakerNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( dilutionBeakerNode ) + 15,
                                          waterBeakerNode.getYOffset() );
            // in the same coordinate frame as the Dilution beaker
            dilutionNode.setOffset( dilutionBeakerNode.getOffset() );
            // upper-right corner of stage
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - 50, 50 );
        }
        scaleRootNodeToFitStage();
        centerRootNodeOnStage();

        //TODO investigate ways to generalize this, or at least make it more integrated with BeakerNode
        // manage dynamic beaker labels, all have slightly different requirements
        {
            // Solution beaker
            SimpleObserver solutionLabelUpdater = new SimpleObserver() {
                public void update() {
                    String labelText;
                    if ( model.solution.volume.get() == 0 ) {
                        labelText = ""; // empty beaker
                    }
                    else if ( model.solution.getConcentration() == 0 ) {
                        labelText = Symbols.WATER; // no solute
                    }
                    else {
                        labelText = Strings.SOLUTION;
                    }
                    solutionBeakerNode.setLabelText( labelText );
                }
            };
            model.solution.addConcentrationObserver( solutionLabelUpdater );
            model.solution.volume.addObserver( solutionLabelUpdater );

            // Water beaker
            SimpleObserver waterLabelUpdater = new SimpleObserver() {
                public void update() {
                    String labelText;
                    if ( model.water.volume.get() == 0 ) {
                        labelText = ""; // empty beaker
                    }
                    else {
                        labelText = Symbols.WATER;
                    }
                    waterBeakerNode.setLabelText( labelText );
                }
            };
            model.water.volume.addObserver( waterLabelUpdater );

            // Dilution beaker
            SimpleObserver dilutionLabelUpdater = new SimpleObserver() {
                public void update() {
                    String labelText;
                    if ( model.dilution.volume.get() == 0 ) {
                        labelText = ""; // empty beaker
                    }
                    else if ( model.dilution.getConcentration() == 0 ) {
                        labelText = Symbols.WATER;  // no solute
                    }
                    else if ( model.dilution.volume.get() - model.solution.volume.get() == 0 ) {
                        labelText = Strings.SOLUTION; // not diluted
                    }
                    else {
                        labelText = Strings.DILUTION;
                    }
                    dilutionBeakerNode.setLabelText( labelText );
                }
            };
            model.dilution.addConcentrationObserver( dilutionLabelUpdater );
            model.dilution.volume.addObserver( dilutionLabelUpdater );
            model.solution.volume.addObserver( dilutionLabelUpdater );
        }

        // beaker concentrations
        {
            model.solution.addConcentrationObserver( new SimpleObserver() {
                public void update() {
                    solutionBeakerNode.setConcentration( model.solution.getConcentration() );
                }
            } );
            model.dilution.addConcentrationObserver( new SimpleObserver() {
                public void update() {
                    dilutionBeakerNode.setConcentration( model.dilution.getConcentration() );
                }
            } );
        }
    }
}
