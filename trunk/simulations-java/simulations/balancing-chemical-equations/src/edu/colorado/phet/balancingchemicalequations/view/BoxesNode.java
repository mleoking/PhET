// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.BasicStroke;
import java.awt.Color;
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
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A pair of boxes that show the number of molecules indicated by the equation coefficients.
 * Left box is for the reactants, right box is for the products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxesNode extends PComposite {

    public BoxesNode( final Property<Equation> equationProperty, IntegerRange coefficientRange ) {

        // boxes
        final BoxOfMoleculesNode beforeBoxNode = new BoxOfMoleculesNode( equationProperty.getValue().getReactants(), coefficientRange );
        addChild( beforeBoxNode );
        final BoxOfMoleculesNode afterBoxNode = new BoxOfMoleculesNode( equationProperty.getValue().getProducts(), coefficientRange );
        addChild( afterBoxNode );

        // right-pointing arrow
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );

        // layout
        double x = 0;
        double y = 0;
        beforeBoxNode.setOffset( x, y );
        x = beforeBoxNode.getFullBoundsReference().getMaxX() + 10;
        y = beforeBoxNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 );
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() + 10;
        y = beforeBoxNode.getYOffset();
        afterBoxNode.setOffset( x, y );

        // update equation
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                beforeBoxNode.setEquationTerms( equationProperty.getValue().getReactants() );
                afterBoxNode.setEquationTerms( equationProperty.getValue().getProducts() );
            }
        } );
    }

    /**
     * A box that contains molecules.
     * The number of molecules corresponds to the coefficients of one or more equation terms.
     * Molecules for each term are arranged in columns.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    private static class BoxOfMoleculesNode extends PComposite {

        private static final PDimension BOX_SIZE = new PDimension( 300, 200 );

        private EquationTerm[] terms;
        private final IntegerRange coefficientRange;
        private final SimpleObserver coefficentObserver;
        private final PComposite moleculesParentNode;

        public BoxOfMoleculesNode( EquationTerm[] terms, IntegerRange coefficientRange ) {

            this.terms = terms;
            this.coefficientRange = coefficientRange;

            PPath boxNode = new PPath( new Rectangle2D.Double( 0, 0, BOX_SIZE.getWidth(), BOX_SIZE.getHeight() ) );
            boxNode.setPaint( BCEColors.BEFORE_AFTER_BOX_COLOR );
            boxNode.setStrokePaint( Color.BLACK );
            boxNode.setStroke( new BasicStroke( 1f ) );
            addChild( boxNode );

            moleculesParentNode = new PComposite();
            addChild( moleculesParentNode );

            coefficentObserver = new SimpleObserver() {
                public void update() {
                    updateNumberOfMolecules();
                }
            };
            addCoefficientObserver();
        }

        public void setEquationTerms( EquationTerm[] terms ) {
            if ( terms != this.terms ) {
                removeCoefficientObserver();
                this.terms = terms;
                addCoefficientObserver();
            }
        }

        private void updateNumberOfMolecules() {

            moleculesParentNode.removeAllChildren();

            final int numberOfTerms = terms.length;
            final double dx = BOX_SIZE.getWidth() / ( numberOfTerms + 1 );
            final double dy = BOX_SIZE.getHeight() / ( coefficientRange.getMax() + 1 );
            double x = dx;
            for ( EquationTerm term : terms ) {
                int numberOfMolecules = term.getActualCoefficient();
                Image moleculeImage = term.getMolecule().getImage();
                double y = dy;
                for ( int i = 0; i < numberOfMolecules; i++ ) {
                    PImage imageNode = new PImage( moleculeImage );
                    imageNode.scale( 0.75 );
                    moleculesParentNode.addChild( imageNode );
                    imageNode.setOffset( x - ( imageNode.getFullBoundsReference().getWidth() / 2 ), y - ( imageNode.getFullBoundsReference().getHeight()  / 2 ) );
                    y += dy;
                }
                x += dx;
            }
        }

        private void addCoefficientObserver() {
            for ( EquationTerm term : this.terms ) {
                term.getActualCoefficientProperty().addObserver( coefficentObserver );
            }
        }

        private void removeCoefficientObserver() {
            for ( EquationTerm term : this.terms ) {
                term.getActualCoefficientProperty().removeObserver( coefficentObserver );
            }
        }
    }
}
