// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a chemical equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationNode extends PhetPNode  {

    private static final Font FONT = new PhetFont( 20 );
    private static final Color NAME_COLOR = Color.BLACK;

    private static final double COEFFICIENT_X_SPACING = 10;
    private static final double PLUS_X_SPACING = 30;
    private static final double ARROW_X_SPACING = 20;

    private static final Color COEFFICIENT_COLOR = Color.BLACK;

    private Property<Equation> equationProperty;
    private final IntegerRange coefficientRange;
    private boolean editable;
    private ArrayList<ActualCoefficientNode> actualCoefficientNodes;

    public EquationNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, boolean editable ) {
        super();

        this.equationProperty = equationProperty;
        this.coefficientRange = coefficientRange;
        this.editable = editable;
        this.actualCoefficientNodes = new ArrayList<ActualCoefficientNode>();

        this.equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateNode();
            }
        } );
    }

    public void setEditable( boolean editable ) {
        if ( editable != this.editable ) {
            this.editable = editable;
            updateNode();
        }
    }

    public boolean isEditable() {
        return editable;
    }

    /*
     * Note:
     * This layout algorithm depends on the fact that all terms contain at least 1 uppercase letter.
     * This allows us to align the baselines of HTML-formatted text.
     */
    private void updateNode() {

        removeAllChildren();
        for ( ActualCoefficientNode node : actualCoefficientNodes ) {
            node.removeObserver();
        }
        actualCoefficientNodes.clear();

        // determine cap height of the font, using a char that has no descender
        final double capHeight = new SymbolNode( "T" ).getFullBounds().getHeight();

        // left-hand side of the formula (reactants)
        EquationTerm[] reactants = equationProperty.getValue().getReactants();
        double x = 0;
        double y = 0;
        PNode previousNode = null;
        for ( int i = 0; i < reactants.length; i++ ) {

            // plus sign between terms
            PlusNode plusNode = null;
            if ( i > 0 ) {
                plusNode = new PlusNode();
                addChild( plusNode );
                x = previousNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = ( capHeight / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }

            // actual coefficient (editable)
            ActualCoefficientNode actualCoefficientNode = new ActualCoefficientNode( coefficientRange, reactants[i].getActualCoefficientProperty() );
            addChild( actualCoefficientNode );
            if ( plusNode == null ) {
                actualCoefficientNode.setOffset( 0, 0 );
            }
            else {
                x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = 0;
                actualCoefficientNode.setOffset( x, y );
            }
            actualCoefficientNode.setVisible( editable );

            // balanced coefficient (not editable)
            BalancedCoefficientNode balancedCoefficientNode = new BalancedCoefficientNode( reactants[i].getBalancedCoefficient() );
            addChild( balancedCoefficientNode );
            x = actualCoefficientNode.getFullBoundsReference().getMaxX() - balancedCoefficientNode.getFullBoundsReference().getWidth();
            y = actualCoefficientNode.getYOffset();
            balancedCoefficientNode.setOffset( x, y );
            balancedCoefficientNode.setVisible( !editable );

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( reactants[i].getMolecule().getSymbol() );
            addChild( symbolNode );
            x = actualCoefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
            y = 0;
            symbolNode.setOffset( x, y );

            previousNode = symbolNode;
        }

        // right-pointing arrow
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );
        x = previousNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
        y = ( capHeight / 2 );
        arrowNode.setOffset( x, y );
        previousNode = arrowNode;

        // right-hand side of the formula (products)
        EquationTerm[] products = equationProperty.getValue().getProducts();
        for ( int i = 0; i < products.length; i++ ) {

            // plus sign between terms
            PlusNode plusNode = null;
            if ( i > 0 ) {
                plusNode = new PlusNode();
                addChild( plusNode );
                x = previousNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = ( capHeight / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }

            // actual coefficient (editable)
            ActualCoefficientNode actualCoefficientNode = new ActualCoefficientNode( coefficientRange, products[i].getActualCoefficientProperty() );
            addChild( actualCoefficientNode );
            if ( plusNode == null ) {
                x = arrowNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                y = 0;
                actualCoefficientNode.setOffset( x, y );
            }
            else {
                x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                y = 0;
                actualCoefficientNode.setOffset( x, y );
            }
            actualCoefficientNode.setVisible( editable );

            // balanced coefficient (not editable)
            BalancedCoefficientNode balancedCoefficientNode = new BalancedCoefficientNode( reactants[i].getBalancedCoefficient() );
            addChild( balancedCoefficientNode );
            x = actualCoefficientNode.getFullBoundsReference().getMaxX() - balancedCoefficientNode.getFullBoundsReference().getWidth();
            y = actualCoefficientNode.getYOffset();
            balancedCoefficientNode.setOffset( x, y );
            balancedCoefficientNode.setVisible( !editable );

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( products[i].getMolecule().getSymbol() );
            addChild( symbolNode );
            x = actualCoefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
            y = 0;
            symbolNode.setOffset( x, y );

            previousNode = symbolNode;
        }
    }

    /*
     * Balanced coefficient, a constant that cannot be edited.
     */
    private static class BalancedCoefficientNode extends PText {

        public BalancedCoefficientNode( int coefficient ) {
            super( String.valueOf( coefficient ) );
            setFont( FONT );
            setTextPaint( COEFFICIENT_COLOR );
        }
    }

    /*
     * Actual coefficient, editable spinner.
     */
    private static class ActualCoefficientNode extends PhetPNode {

        private final Property<Integer> coefficientProperty;
        private final SimpleObserver observer;

        public ActualCoefficientNode( IntegerRange range, final Property<Integer> coefficientProperty ) {
            this.coefficientProperty = coefficientProperty;
            final IntegerSpinner spinner = new IntegerSpinner( range );
            spinner.setFont( FONT );
            spinner.setForeground( COEFFICIENT_COLOR );
            spinner.setValue( coefficientProperty.getValue() );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    coefficientProperty.setValue( spinner.getIntValue() );
                }
            } );
            observer = new SimpleObserver() {
                public void update() {
                    spinner.setIntValue( coefficientProperty.getValue() );
                }
            };
            coefficientProperty.addObserver( observer );
            addChild( new PSwing( spinner ) );
        }

        public void removeObserver() {
            coefficientProperty.removeObserver( observer );
        }
    }

    /*
     * Molecule symbol
     */
    private static class SymbolNode extends HTMLNode {

        public SymbolNode( String html ) {
            super( HTMLUtils.toHTMLString( html ), NAME_COLOR, FONT );
        }
    }
}
