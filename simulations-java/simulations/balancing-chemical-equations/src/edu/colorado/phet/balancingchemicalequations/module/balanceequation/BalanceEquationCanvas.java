// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.balanceequation;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode;
import edu.colorado.phet.balancingchemicalequations.control.BalanceChoiceNode.BalanceChoice;
import edu.colorado.phet.balancingchemicalequations.control.EquationChoiceNode;
import edu.colorado.phet.balancingchemicalequations.view.*;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Balance Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceEquationCanvas extends BCECanvas {

    private static final Dimension BOX_SIZE = new Dimension( 300, 140 );
    private static final double BOX_SEPARATION = 60;

    private final Property<BalanceChoice> balanceChoiceProperty; // determines the visual representation of "balanced"
    private final BoxesNode boxesNode;

    public BalanceEquationCanvas( final BalanceEquationModel model, final BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        HorizontalAligner aligner = new HorizontalAligner( BOX_SIZE, BOX_SEPARATION );

        balanceChoiceProperty = new Property<BalanceChoice>( BalanceChoice.NONE );

        // control for choosing an equation
        EquationChoiceNode equationChoiceNode = new EquationChoiceNode( model.getEquations(), model.getCurrentEquationProperty(), globalProperties.getCanvasColorProperty() );
        addChild( equationChoiceNode );

        // equation, in formula format
        final EquationNode equationNode = new EquationNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), true /* editable */, aligner );
        addChild( equationNode );

        // boxes that show molecules corresponding to the equation coefficients
        boxesNode = new BoxesNode( model.getCurrentEquationProperty(), model.getCoefficientsRange(), aligner, globalProperties.getBoxColorProperty() );
        addChild( boxesNode );

        // control for choosing the visual representation of "balanced"
        BalanceChoiceNode balanceChoiceNode = new BalanceChoiceNode( balanceChoiceProperty, globalProperties.getCanvasColorProperty() );
        addChild( balanceChoiceNode );

        // bar charts
        final BarChartsNode barChartsNode = new BarChartsNode( model.getCurrentEquationProperty(), aligner );
        addChild( barChartsNode );

        // balance scales
        final BalanceScalesNode balanceScalesNode = new BalanceScalesNode( model.getCurrentEquationProperty(), aligner );
        addChild( balanceScalesNode );

        // Reset All button
        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( resettable, globalProperties.getFrame(), 12, Color.BLACK, Color.WHITE );
        resetAllButtonNode.addResettable( globalProperties );
        addChild( resetAllButtonNode );

        // Dev, shows balanced coefficients
        final BalancedEquationNode balancedEquationNode = new BalancedEquationNode( model.getCurrentEquationProperty() );
        if ( globalProperties.isDev() ) {
            addChild( balancedEquationNode );
        }

        /*
         * Layout - all of the major visual representations have x-offset=0,
         * and handle their own horizontal alignment via HorizontalAligner.
         */
        {
            double x, y;

            // equation choices above equation, left justified
            equationChoiceNode.setOffset( 0, 0 );

            // equation below choices
            y = equationChoiceNode.getFullBoundsReference().getMaxY() + 25;
            equationNode.setOffset( 0, y );

            // boxes below equation
            y = equationNode.getFullBoundsReference().getMaxY() + 15;
            boxesNode.setOffset( 0, y );

            // bar charts and balance scales below boxes (centering is handle by nodes themselves)
            y = boxesNode.getFullBoundsReference().getMaxY() + 110;
            barChartsNode.setOffset( 0, y );
            y = barChartsNode.getYOffset() - 10;
            balanceScalesNode.setOffset( 0, y );

            // balance choices below bar charts and balance scales, left justified
            y = Math.max( barChartsNode.getFullBoundsReference().getMaxY(), balanceScalesNode.getFullBoundsReference().getMaxY() ) + 25;
            balanceChoiceNode.setOffset( 0, y );

            // Reset All button at bottom right
            x = boxesNode.getFullBoundsReference().getMaxX() - resetAllButtonNode.getFullBoundsReference().getWidth();
            y = balanceChoiceNode.getFullBoundsReference().getMinY();
            resetAllButtonNode.setOffset( x, y );

            // answer right-justified below Reset All button
            x = resetAllButtonNode.getFullBoundsReference().getMaxX() - balancedEquationNode.getFullBoundsReference().getWidth();
            y = resetAllButtonNode.getFullBoundsReference().getMaxY() + 2;
            balancedEquationNode.setOffset( x, y );
            balancedEquationNode.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    if ( evt.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                        double x = resetAllButtonNode.getFullBoundsReference().getMaxX() - balancedEquationNode.getFullBoundsReference().getWidth();
                        double y = resetAllButtonNode.getFullBoundsReference().getMaxY() + 2;
                        balancedEquationNode.setOffset( x, y );
                    }
                }
            } );
        }

        // observers
        balanceChoiceProperty.addObserver( new SimpleObserver() {
            public void update() {
                barChartsNode.setVisible( balanceChoiceProperty.getValue().equals( BalanceChoice.BAR_CHARTS ) );
                balanceScalesNode.setVisible( balanceChoiceProperty.getValue().equals( BalanceChoice.BALANCE_SCALES ) );
            }
        } );

        globalProperties.getMoleculesVisibleProperty().addObserver( new SimpleObserver() {
            public void update() {
                boxesNode.setMoleculesVisible( globalProperties.getMoleculesVisibleProperty().getValue() );
            }
        } );

    }

    public void reset() {
        balanceChoiceProperty.reset();
    }
}
