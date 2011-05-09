// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
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

    private static final PhetFont FONT = new PhetFont( 30 );
    private static final Color SYMBOL_COLOR = Color.BLACK;

    private static final Color COEFFICIENT_COLOR = Color.BLACK;

    private final IntegerRange coefficientRange;
    private final HorizontalAligner aligner;
    private final ArrayList<TermNode> termNodes;

    private final SimpleObserver coefficientsObserver;
    private final RightArrowNode arrowNode;
    private final PNode termsParent;

    private Equation equation;
    private boolean editable;
    private boolean balancedHighlightEnabled;

    /**
     * Constructor.
     * @param equationProperty
     * @param coefficientRange
     * @param aligner specifies horizontal layout, for aligning with other user-interface components
     */
    public EquationNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, HorizontalAligner aligner ) {

        this.coefficientRange = coefficientRange;
        this.editable = true;
        this.aligner = aligner;
        this.termNodes = new ArrayList<TermNode>();
        this.balancedHighlightEnabled = true;

        // arrow node, in a fixed location
        arrowNode = new RightArrowNode( equationProperty.getValue().isBalanced() );
        addChild( arrowNode );
        double x = aligner.getCenterXOffset() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        double y = ( SymbolNode.getCapHeight() / 2 );
        arrowNode.setOffset( x, y );

        // the parent for all equation terms and the "+" signs
        termsParent = new PhetPNode();
        addChild( termsParent );

        // if the coefficient changes...
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                arrowNode.setHighlighted( equation.isBalanced() && balancedHighlightEnabled );
            }
        };

        // if the equation changes...
        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                EquationNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                EquationNode.this.equation = equationProperty.getValue();
                EquationNode.this.equation.addCoefficientsObserver( coefficientsObserver );
                updateNode();
            }
        } );
    }

    /**
     * Controls whether the coefficients are editable (spinner) or read-only (labels).
     * @param editable
     */
    public void setEditable( boolean editable ) {
        if ( editable != this.editable ) {
            this.editable = editable;
            for ( TermNode termNode : termNodes ) {
                termNode.setEditable( editable );
            }
        }
    }

    /**
     * Enables or disables the highlighting feature.
     * When enabled, the arrow between the left and right sides of the equation will light up when the equation is balanced.
     * This is enabled by default, but we want to disable in the Game until the user presses the "Check" button.
     * @param enabled
     */
    public void setBalancedHighlightEnabled( boolean enabled ) {
        if ( enabled != balancedHighlightEnabled ) {
            balancedHighlightEnabled = enabled;
            arrowNode.setHighlighted( equation.isBalanced() && balancedHighlightEnabled );
        }
    }

    /*
     * Rebuilds the left and right sides of the equation.
     */
    private void updateNode() {

        termsParent.removeAllChildren();

        for ( TermNode node : termNodes ) {
            node.cleanup();
        }
        termNodes.clear();

        updateSideOfEquation( equation.getReactants(), aligner.getReactantXOffsets( equation ) );
        updateSideOfEquation( equation.getProducts(), aligner.getProductXOffsets( equation ) );
    }

    /*
     * Updates one side of the equation.
     * This layout algorithm depends on the fact that all terms contain at least 1 capital letter.
     * This allows us to align the baselines of HTML-formatted text.
     */
    private void updateSideOfEquation( EquationTerm[] terms, double[] xOffsets ) {
        assert( terms.length == xOffsets.length );
        for ( int i = 0; i < terms.length; i++ ) {

            // term
            TermNode termNode = new TermNode( coefficientRange, terms[i], editable );
            termNodes.add( termNode );
            termsParent.addChild( termNode );
            termNode.setOffset( xOffsets[i] - ( termNode.getFullBoundsReference().getWidth() / 2 ), 0 );

            // plus sign, centered between 2 terms
            if ( terms.length > 1 && i < terms.length - 1 ) {

                PlusNode plusNode = new PlusNode();
                termsParent.addChild( plusNode );

                /*
                 * Make sure that the term doesn't get too close to the plus sign.
                 * If it does, then shift the plus sign a bit to the right.
                 */
                double x = xOffsets[i] + ( ( xOffsets[i + 1] - xOffsets[i] ) / 2 ) - ( plusNode.getFullBoundsReference().getWidth() / 2 ); // centered between 2 offsets
                final double minSeparation = 20;
                final double separation = x - termNode.getFullBoundsReference().getMaxX();
                if ( separation < minSeparation ) {
                    x += ( minSeparation - separation );
                }
                double y = ( SymbolNode.getCapHeight() / 2 ) - ( plusNode.getFullBoundsReference().getHeight() / 2 );
                plusNode.setOffset( x, y );
            }
        }
    }

    /*
     * A term in the equation, includes the coefficient and symbol.
     * The coefficient may or may not be editable.
     */
    private static class TermNode extends PhetPNode {

        private final CoefficientNode coefficientNode;

        public TermNode( IntegerRange coefficientRange, EquationTerm term, boolean editable ) {

            // coefficient
            coefficientNode = new CoefficientNode( coefficientRange, term.getUserCoefficientProperty(), editable );
            addChild( coefficientNode );

            // molecule symbol
            SymbolNode symbolNode = new SymbolNode( term.getMolecule().getSymbol() );
            addChild( symbolNode );

            // layout
            coefficientNode.setOffset( 0, 0 );
            symbolNode.setOffset( coefficientNode.getFullBoundsReference().getMaxX() + 5, 0 );
        }

        public void setEditable( boolean editable ) {
            coefficientNode.setEditable( editable );
        }

        public void cleanup() {
            coefficientNode.removeCoefficientObserver();
        }
    }

    /*
     * Coefficient node, can be read-only or editable.
     * Listens for changes to the coefficient property and updates accordingly.
     * When editable, sets the coefficient property.
     */
    public static class CoefficientNode extends PhetPNode {

        private final Property<Integer> coefficientProperty;
        private final SimpleObserver coefficientObserver;
        private final PText textNode;
        private final PSwing spinnerNode;

        public CoefficientNode( IntegerRange range, final Property<Integer> coefficientProperty, boolean editable ) {

            // read-only text
            textNode = new PText();
            textNode.setFont( FONT );
            textNode.setTextPaint( COEFFICIENT_COLOR );

            // editable spinner
            final IntegerSpinner spinner = new IntegerSpinner( range );
            spinner.setForeground( COEFFICIENT_COLOR );
            spinner.setFont( FONT );
            spinner.setValue( coefficientProperty.getValue() );
            spinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    coefficientProperty.setValue( spinner.getIntValue() );
                }
            } );
            spinnerNode = new PSwing( spinner );
            if ( PhetUtilities.isMacintosh() ) {
                spinnerNode.scale( 1.75 ); //WORKAROUND: JSpinner font changes are ignored on Mac
            }

            // rendering order
            addChild( spinnerNode );
            addChild( textNode );

            // visibility
            textNode.setVisible( !editable );
            spinnerNode.setVisible( editable );

            // coefficient observer
            this.coefficientProperty = coefficientProperty;
            coefficientObserver = new SimpleObserver() {
                public void update() {
                    spinner.setIntValue( coefficientProperty.getValue() );
                    textNode.setText( String.valueOf( coefficientProperty.getValue() ) );
                    textNode.setOffset( spinnerNode.getFullBoundsReference().getMaxX() - textNode.getFullBoundsReference().getWidth() - 12, 0 ); // right justified
                }
            };
            coefficientProperty.addObserver( coefficientObserver );
        }

        public void setEditable( boolean editable ) {
            textNode.setVisible( !editable );
            spinnerNode.setVisible( editable );
        }

        public void removeCoefficientObserver() {
            coefficientProperty.removeObserver( coefficientObserver );
        }
    }

    /*
     * Molecule symbol
     */
    private static class SymbolNode extends HTMLNode {

        public SymbolNode( String html ) {
            super( html, SYMBOL_COLOR, FONT );
        }

        /**
         * Gets the height of a capital letter, used for layout to align equation along a baseline
         * that is assumed to be at the bottom of the capital letters. Assumes that every molecule
         * symbol contains at least one capital letter (a very safe assumption).
         */
        public static double getCapHeight() {
            return new SymbolNode( "T" ).getFullBounds().getHeight();
        }
    }
}
