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
 * Reactants are on the left-hand size, products are on the right-hand side.
 * When coefficients are editable, they are displayed as editable spinners.
 * When coefficients are not editable, they are displayed as PText.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationNode extends PhetPNode  {

    private static final Font FONT = new PhetFont( 20 );
    private static final Color NAME_COLOR = Color.BLACK;

    private static final Color COEFFICIENT_COLOR = Color.BLACK;

    private Property<Equation> equationProperty;
    private final IntegerRange coefficientRange;
    private boolean editable;
    private final HorizontalAligner aligner;
    private ArrayList<ActualCoefficientNode> actualCoefficientNodes;

    /**
     * Constructor.
     * @param equationProperty
     * @param coefficientRange
     * @param editable
     * @param leftRightSideLength horizontal space alloted for each side of the equation
     * @param leftRightSeparation separation between the left and right sides of the equation
     */
    public EquationNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, boolean editable, double leftRightSideLength, double leftRightSeparation ) {
        super();

        this.equationProperty = equationProperty;
        this.coefficientRange = coefficientRange;
        this.editable = editable;
        this.aligner = new HorizontalAligner( leftRightSideLength, leftRightSeparation );
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

        updateSideOfEquation( equationProperty.getValue().getReactants(), aligner.getReactantXOffsets( equationProperty.getValue() ), capHeight );
        updateSideOfEquation( equationProperty.getValue().getProducts(), aligner.getProductXOffsets( equationProperty.getValue() ), capHeight );

        // right-pointing arrow
        {
            RightArrowNode arrowNode = new RightArrowNode();
            addChild( arrowNode );
            double x = aligner.getCenterXOffset() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
            double y = ( capHeight / 2 );
            arrowNode.setOffset( x, y );
        }
    }

    /*
     * Updates one side of the equation.
     */
    private void updateSideOfEquation( EquationTerm[] terms, double[] xOffsets, double capHeight ) {
        assert( terms.length == xOffsets.length );
        for ( int i = 0; i < terms.length; i++ ) {

            // term
            TermNode termNode = new TermNode( coefficientRange, terms[i], editable );
            addChild( termNode );
            termNode.setOffset( xOffsets[i] - ( termNode.getFullBoundsReference().getWidth() / 2 ), 0 );

            // plus sign, centered between 2 terms
            if ( i > 0 ) {
                PlusNode plusNode = new PlusNode();
                addChild( plusNode );
                double x =  xOffsets[i] - ( ( xOffsets[i] - xOffsets[i-1] ) / 2 ) - ( plusNode.getFullBoundsReference().getWidth() / 2 ); // centered between 2 offsets
                double y = ( capHeight / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }
        }
    }

    /*
     * A term in the equation, includes the coefficient and symbol.
     * The coefficient may or may not be editable.
     */
    private static class TermNode extends PhetPNode {

        public TermNode( IntegerRange coefficientRange, EquationTerm term, boolean editable ) {

            // coefficient
            PNode coefficientNode = null;
            if ( editable ) {
                coefficientNode = new ActualCoefficientNode( coefficientRange, term.getActualCoefficientProperty() );
            }
            else {
                coefficientNode = new BalancedCoefficientNode( term.getBalancedCoefficient() );

            }
            addChild( coefficientNode );

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( term.getMolecule().getSymbol() );
            addChild( symbolNode );

            // layout
            double x = 0;
            double y = 0;
            coefficientNode.setOffset( x, y );
            x = coefficientNode.getFullBoundsReference().getMaxX() + 5;
            y = 0;
            symbolNode.setOffset( x, y );
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
