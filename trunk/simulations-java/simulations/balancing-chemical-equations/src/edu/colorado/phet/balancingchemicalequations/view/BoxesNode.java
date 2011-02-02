// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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

    private final IntegerRange coefficientRange;
    private final HorizontalAligner horizontalAligner;
    private final PComposite moleculesParentNode;
    private final SimpleObserver coefficientsObserver;
    private Equation equation;

    public BoxesNode( final Property<Equation> equationProperty, IntegerRange coefficientRange, final HorizontalAligner horizontalAligner ) {

        this.coefficientRange = coefficientRange;
        this.horizontalAligner = horizontalAligner;

        // boxes
        BoxNode reactantsBoxNode = new BoxNode( horizontalAligner.getBoxSizeReference() );
        addChild( reactantsBoxNode );
        BoxNode productsBoxNode = new BoxNode( horizontalAligner.getBoxSizeReference() );
        addChild( productsBoxNode );

        // right-pointing arrow
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );

        // molecules
        moleculesParentNode = new PComposite();
        addChild( moleculesParentNode );

        // layout
        double x = 0;
        double y = 0;
        reactantsBoxNode.setOffset( x, y );
        moleculesParentNode.setOffset( x, y );
        x = reactantsBoxNode.getFullBoundsReference().getMaxX() + ( horizontalAligner.getBoxSeparation() / 2 ) - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        y = reactantsBoxNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 );
        arrowNode.setOffset( x, y );
        x = reactantsBoxNode.getFullBoundsReference().getMaxX() + horizontalAligner.getBoxSeparation();
        y = reactantsBoxNode.getYOffset();
        productsBoxNode.setOffset( x, y );

        this.equation = equationProperty.getValue();
        // coefficient changes
        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateMolecules();
            }
        };
        // equation changes
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                removeCoefficientsObserver();
                BoxesNode.this.equation = equationProperty.getValue();
                addCoefficientsObserver();
            }
        } );
    }

    public void setMoleculesVisible( boolean moleculesVisible ) {
        moleculesParentNode.setVisible( moleculesVisible );
    }

    private void updateMolecules() {
        moleculesParentNode.removeAllChildren();
        updateMolecules( equation.getReactants(), horizontalAligner.getReactantXOffsets( equation ) );
        updateMolecules( equation.getProducts(), horizontalAligner.getProductXOffsets( equation ) );
    }

    private void updateMolecules( EquationTerm[] terms, double[] xOffsets ) {
        assert( terms.length == xOffsets.length );
        final double dy = horizontalAligner.getBoxSizeReference().getHeight() / ( coefficientRange.getMax() + 1 );
        for ( int i = 0; i < terms.length; i++ ) {
            int numberOfMolecules = terms[i].getActualCoefficient();
            Image moleculeImage = terms[i].getMolecule().getImage();
            double y = dy;
            for ( int j = 0; j < numberOfMolecules; j++ ) {
                PImage imageNode = new PImage( moleculeImage );
                imageNode.scale( 0.75 );
                moleculesParentNode.addChild( imageNode );
                imageNode.setOffset( xOffsets[i] - ( imageNode.getFullBoundsReference().getWidth() / 2 ), y - ( imageNode.getFullBoundsReference().getHeight()  / 2 ) );
                y += dy;
            }
        }
    }

    private void addCoefficientsObserver() {
        for ( EquationTerm term : equation.getReactants() ) {
            term.getActualCoefficientProperty().addObserver( coefficientsObserver );
        }
        for ( EquationTerm term : equation.getProducts() ) {
            term.getActualCoefficientProperty().addObserver( coefficientsObserver );
        }
    }

    private void removeCoefficientsObserver() {
        for ( EquationTerm term : equation.getReactants() ) {
            term.getActualCoefficientProperty().removeObserver( coefficientsObserver );
        }
        for ( EquationTerm term : equation.getProducts() ) {
            term.getActualCoefficientProperty().removeObserver( coefficientsObserver );
        }
    }

    /**
     * A simple box.
     */
    private static class BoxNode extends PPath {
        public BoxNode( Dimension boxSize ) {
            super( new Rectangle2D.Double( 0, 0, boxSize.getWidth(), boxSize.getHeight() ) );
            setPaint( BCEColors.BEFORE_AFTER_BOX_COLOR );
            setStrokePaint( Color.BLACK );
            setStroke( new BasicStroke( 1f ) );
        }
    }
}
