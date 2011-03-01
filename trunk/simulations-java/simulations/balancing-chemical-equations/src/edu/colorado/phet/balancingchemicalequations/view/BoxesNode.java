// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A pair of boxes that show the number of molecules indicated by the equation coefficients.
 * Left box is for the reactants, right box is for the products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxesNode extends PComposite {

    /*
     * true = create molecule stacks from top down
     * false = create molecule stacks from bottom up
     */
    private static final boolean TOP_DOWN_STACKS = true;

    private final IntegerRange coefficientRange;
    private final HorizontalAligner aligner;
    private final PComposite moleculesParentNode;
    private final SimpleObserver coefficientsObserver;
    private final RightArrowNode arrowNode;
    private final HTMLNode moleculesHiddenHodeLeft, moleculesHiddenHodeRight;

    private Equation equation;
    private boolean balancedHighlightEnabled;

    public BoxesNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, HorizontalAligner aligner,
            final Property<Color> boxColorProperty, final Property<Boolean> moleculesVisibleProperty ) {

        this.coefficientRange = coefficientRange;
        this.aligner = aligner;
        balancedHighlightEnabled = true;

        // boxes
        final BoxNode reactantsBoxNode = new BoxNode( aligner.getBoxSizeReference() );
        addChild( reactantsBoxNode );
        final BoxNode productsBoxNode = new BoxNode( aligner.getBoxSizeReference() );
        addChild( productsBoxNode );

        // right-pointing arrow
        arrowNode = new RightArrowNode( equationProperty.getValue().isBalanced() );
        addChild( arrowNode );

        // molecules
        moleculesParentNode = new PComposite();
        addChild( moleculesParentNode );

        // "molecules are hidden" message
        moleculesHiddenHodeLeft = new HTMLNode( BCEStrings.MOLECULES_ARE_HIDDEN, Color.WHITE, new PhetFont( 18 ) );
        addChild( moleculesHiddenHodeLeft );
        moleculesHiddenHodeRight = new HTMLNode( BCEStrings.MOLECULES_ARE_HIDDEN, Color.WHITE, new PhetFont( 18 ) );
        addChild( moleculesHiddenHodeRight );

        // layout
        double x = 0;
        double y = 0;
        reactantsBoxNode.setOffset( x, y );
        moleculesParentNode.setOffset( x, y );
        x = aligner.getCenterXOffset() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        y = reactantsBoxNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 );
        arrowNode.setOffset( x, y );
        x = reactantsBoxNode.getFullBoundsReference().getMaxX() + aligner.getBoxSeparation();
        y = reactantsBoxNode.getYOffset();
        productsBoxNode.setOffset( x, y );
        x = reactantsBoxNode.getFullBoundsReference().getCenterX() - ( moleculesHiddenHodeLeft.getFullBoundsReference().getWidth() / 2 );
        y = reactantsBoxNode.getFullBoundsReference().getCenterY() - ( moleculesHiddenHodeLeft.getFullBoundsReference().getHeight() / 2 );
        moleculesHiddenHodeLeft.setOffset( x, y );
        x = productsBoxNode.getFullBoundsReference().getCenterX() - ( moleculesHiddenHodeRight.getFullBoundsReference().getWidth() / 2 );
        y = productsBoxNode.getFullBoundsReference().getCenterY() - ( moleculesHiddenHodeRight.getFullBoundsReference().getHeight() / 2 );
        moleculesHiddenHodeRight.setOffset( x, y );

        // coefficient changes
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };
        // equation changes
        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                BoxesNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                BoxesNode.this.equation = equationProperty.getValue();
                BoxesNode.this.equation.addCoefficientsObserver( coefficientsObserver );
            }
        } );
        // box color changes
        boxColorProperty.addObserver( new SimpleObserver() {
            public void update() {
                reactantsBoxNode.setPaint( boxColorProperty.getValue() );
                productsBoxNode.setPaint( boxColorProperty.getValue() );
            }
        } );
        // molecules visibility
        moleculesVisibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setMoleculesVisible( moleculesVisibleProperty.getValue() );
            }
        } );
    }

    private void setMoleculesVisible( boolean moleculesVisible ) {
        moleculesParentNode.setVisible( moleculesVisible );
        moleculesHiddenHodeLeft.setVisible( !moleculesVisible );
        moleculesHiddenHodeRight.setVisible( !moleculesVisible );
    }

    public void setBalancedHighlightEnabled( boolean enabled ) {
        if ( enabled != balancedHighlightEnabled ) {
            balancedHighlightEnabled = enabled;
            arrowNode.setHighlighted( equation.isBalanced() && balancedHighlightEnabled );
        }
    }

    private void updateNode() {
        moleculesParentNode.removeAllChildren();
        updateMolecules( equation.getReactants(), aligner.getReactantXOffsets( equation ) );
        updateMolecules( equation.getProducts(), aligner.getProductXOffsets( equation ) );
        arrowNode.setHighlighted( equation.isBalanced() && balancedHighlightEnabled );
    }

    private void updateMolecules( EquationTerm[] terms, double[] xOffsets ) {
        assert( terms.length == xOffsets.length );
        final double yMargin = 10;
        final double rowHeight = ( aligner.getBoxSizeReference().getHeight() - ( 2 * yMargin ) ) / ( coefficientRange.getMax() );
        for ( int i = 0; i < terms.length; i++ ) {
            int numberOfMolecules = terms[i].getActualCoefficient();
            Image moleculeImage = terms[i].getMolecule().getImage();
            double y = 0;
            if ( TOP_DOWN_STACKS ) {
                y = yMargin + ( rowHeight / 2 );
            }
            else {
                y = aligner.getBoxSizeReference().getHeight() - yMargin - ( rowHeight / 2 );
            }
            for ( int j = 0; j < numberOfMolecules; j++ ) {
                PImage imageNode = new PImage( moleculeImage );
                moleculesParentNode.addChild( imageNode );
                imageNode.setOffset( xOffsets[i] - ( imageNode.getFullBoundsReference().getWidth() / 2 ), y - ( imageNode.getFullBoundsReference().getHeight()  / 2 ) );
                if ( TOP_DOWN_STACKS ) {
                    y += rowHeight;
                }
                else {
                    y -= rowHeight;
                }
            }
        }
    }

    /**
     * A simple box.
     */
    private static class BoxNode extends PPath {
        public BoxNode( Dimension boxSize ) {
            super( new Rectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight() ) );
            setStrokePaint( Color.BLACK );
            setStroke( new BasicStroke( 1f ) );
        }
    }
}
