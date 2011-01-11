// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

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
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a chemical equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationNode extends PhetPNode  {

    private static final boolean SHOW_COEFFICIENT_BORDERS = false; // puts a border around coefficients
    private static final boolean SHOW_ONE_COEFFICIENTS = true; // shows coefficient values that are 1

    private static final Font FONT = new PhetFont( 20 );
    private static final Color NAME_COLOR = Color.BLACK;

    private static final double COEFFICIENT_X_SPACING = 10;
    private static final double PLUS_X_SPACING = 30;
    private static final double ARROW_X_SPACING = 20;

    private static final Color COEFFICIENT_COLOR = Color.BLACK;
    private static final double COEFFICIENT_BORDER_MARGIN = 5;
    private static final Stroke COEFFICIENT_BORDER_STROKE = new BasicStroke( 0.5f );
    private static final Color COEFFICIENT_BORDER_COLOR = Color.BLACK;

    private final Equation equation;

    public EquationNode( Equation equation ) {
        super();
        this.equation = equation;
        update();
    }

    public void cleanup() {
        // nothing to do here
    }

    /*
     * Note:
     * This layout algorithm depends on the fact that all terms contain at least 1 uppercase letter.
     * This allows us to align the baselines of HTML-formatted text.
     */
    private void update() {

        removeAllChildren();

        // determine cap height of the font, using a char that has no descender
        final double capHeight = new SymbolNode( "T" ).getFullBounds().getHeight();

        // left-hand side of the formula (reactants)
        EquationTerm[] reactants = equation.getReactants();
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

            // balanced coefficient (not editable)
            BalancedCoefficientNode balancedCoefficientNode = null;
            if ( SHOW_ONE_COEFFICIENTS || reactants[i].getBalancedCoefficient() != 1 ) {
                balancedCoefficientNode = new BalancedCoefficientNode( reactants[i].getBalancedCoefficient() );
                addChild( balancedCoefficientNode );
                if ( plusNode == null ) {
                    balancedCoefficientNode.setOffset( 0, 0 );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    balancedCoefficientNode.setOffset( x, y );
                }
            }

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( reactants[i].getMolecule().getSymbol() );
            addChild( symbolNode );
            if ( balancedCoefficientNode == null ) {
                if ( plusNode == null ) {
                    symbolNode.setOffset( 0, 0 );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    symbolNode.setOffset( x, y );
                }
            }
            else {
                x = balancedCoefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
                y = 0;
                symbolNode.setOffset( x, y );
            }
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
        EquationTerm[] products = equation.getProducts();
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

            // balanced coefficient (not editable)
            BalancedCoefficientNode balancedCoefficientNode = null;
            if ( SHOW_ONE_COEFFICIENTS || reactants[i].getBalancedCoefficient() != 1 ) {
                balancedCoefficientNode = new BalancedCoefficientNode( products[i].getBalancedCoefficient() );
                addChild( balancedCoefficientNode );
                if ( plusNode == null ) {
                    x = arrowNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                    y = 0;
                    balancedCoefficientNode.setOffset( x, y );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    balancedCoefficientNode.setOffset( x, y );
                }
            }

            // actual coefficient (editable)

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( products[i].getMolecule().getSymbol() );
            addChild( symbolNode );
            if ( balancedCoefficientNode == null ) {
                if ( plusNode == null ) {
                    x = arrowNode.getFullBoundsReference().getMaxX() + ARROW_X_SPACING;
                    y = 0;
                    symbolNode.setOffset( x, y );
                }
                else {
                    x = plusNode.getFullBoundsReference().getMaxX() + PLUS_X_SPACING;
                    y = 0;
                    symbolNode.setOffset( x, y );
                }
            }
            else {
                x = balancedCoefficientNode.getFullBoundsReference().getMaxX() + COEFFICIENT_X_SPACING;
                y = 0;
                symbolNode.setOffset( x, y );
            }

            previousNode = symbolNode;
        }
    }

    /*
     * Balanced coefficient, a constant that cannot be edited.
     */
    private static class BalancedCoefficientNode extends PComposite {

        public BalancedCoefficientNode( int coefficient ) {
            super();

            PText textNode = new PText( String.valueOf( coefficient ) );
            textNode.setFont( FONT );
            textNode.setTextPaint( COEFFICIENT_COLOR );
            addChild( textNode );

            final double x = -COEFFICIENT_BORDER_MARGIN;
            final double y = -COEFFICIENT_BORDER_MARGIN;
            final double w = textNode.getFullBoundsReference().getWidth() + ( 2 * COEFFICIENT_BORDER_MARGIN );
            final double h = textNode.getFullBoundsReference().getHeight() + ( 2 * COEFFICIENT_BORDER_MARGIN );
            PPath borderNode = new PPath( new Rectangle2D.Double( x, y,w,h ) );
            borderNode.setStroke( COEFFICIENT_BORDER_STROKE );
            borderNode.setStrokePaint( COEFFICIENT_BORDER_COLOR );
            if ( SHOW_COEFFICIENT_BORDERS ) {
                addChild( borderNode );
            }

            // layout
            textNode.setOffset( 0, 0 );
            borderNode.setOffset( 0, 0 );
        }
    }

    /*
     * Actual coefficient, editable spinner.
     */
    private static class ActualCoefficientNode extends PhetPNode {

        public ActualCoefficientNode( IntegerRange range, final Property<Integer> coefficientProperty ) {
            final IntegerSpinner spinner = new IntegerSpinner( range );
            spinner.setValue( coefficientProperty.getValue() );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    coefficientProperty.setValue( spinner.getIntValue() );
                }
            } );
            coefficientProperty.addObserver( new SimpleObserver() {
                public void update() {
                    spinner.setIntValue( coefficientProperty.getValue() );
                }
            } );
            addChild( new PSwing( spinner ) );
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
