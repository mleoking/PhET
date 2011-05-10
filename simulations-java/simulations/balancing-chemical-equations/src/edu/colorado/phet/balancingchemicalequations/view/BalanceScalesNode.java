// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.AtomCount;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a set of balance scales, one for each atom type.
 * The left side of each scale is the reactants, the right side is the products.
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplicity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalanceScalesNode extends PComposite {

    private final SimpleObserver coefficientsObserver;
    private final HorizontalAligner aligner;

    private Equation equation;

    /**
     * Constructor.
     * @param equationProperty the equation that the scales are representing
     * @param aligner provides layout information to ensure horizontal alignment with other user-interface elements
     */
    public BalanceScalesNode( final Property<Equation> equationProperty, HorizontalAligner aligner ) {

        this.aligner = aligner;

        coefficientsObserver = new SimpleObserver() {
            public void update() {
               updateNode();
            }
        };
        this.equation = equationProperty.get();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                BalanceScalesNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                BalanceScalesNode.this.equation = equationProperty.get();
                BalanceScalesNode.this.equation.addCoefficientsObserver( coefficientsObserver );
            }
        } );
    }

    /*
     * Updates this node's entire geometry and layout
     */
    private void updateNode() {

        removeAllChildren();

        ArrayList<AtomCount> atomCounts = equation.getAtomCounts();
        final double xSpacing = 32;
        final double dx = BalanceScaleNode.getBeamLength() + xSpacing;
        double x = aligner.getCenterXOffset() - ( ( atomCounts.size() - 1 ) * BalanceScaleNode.getBeamLength() / 2 ) - ( ( atomCounts.size() - 1 ) * xSpacing / 2 );
        for ( AtomCount atomCount : atomCounts ) {
            boolean highlighted = equation.isBalanced();
            BalanceScaleNode scaleNode = new BalanceScaleNode( atomCount.getAtom(), atomCount.getReactantsCount(), atomCount.getProductsCount(), highlighted );
            addChild( scaleNode );
            scaleNode.setOffset( x, 0 );
            x += dx;
        }
    }
}
