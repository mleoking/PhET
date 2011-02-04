// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCEColors;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;
import edu.colorado.phet.balancingchemicalequations.model.AtomCount;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of an equation as a pair of bar charts, for left and right side of equation.
 * An indicator between the charts (equals or not equals) indicates whether they are balanced.
 * <p>
 * This implementation is very brute force, just about everything is recreated each time
 * a coefficient is changed in the equations.  But we have a small number of coefficients,
 * and nothing else is happening in the sim.  So we're trading efficiency for simplcity of
 * implementation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarChartsNode extends PComposite {

    private final HorizontalAligner aligner;
    private final EqualsSignNode equalsSignNode;
    private final NotEqualsSignNode notEqualsSignNode;
    private final PNode reactantsChartParent, productsChartParent;
    private final SimpleObserver coefficientsObserver;

    private Equation equation;

    public BarChartsNode( final Property<Equation> equationProperty, HorizontalAligner aligner ) {

        this.aligner = aligner;

        reactantsChartParent = new PComposite();
        addChild( reactantsChartParent );

        productsChartParent = new PComposite();
        addChild( productsChartParent );

        equalsSignNode = new EqualsSignNode();
        addChild( equalsSignNode );

        notEqualsSignNode = new NotEqualsSignNode();
        addChild( notEqualsSignNode );

        coefficientsObserver = new SimpleObserver() {
            public void update() {
                updateNode();
            }
        };

        this.equation = equationProperty.getValue();
        equationProperty.addObserver( new SimpleObserver() {
            public void update() {
                BarChartsNode.this.equation.removeCoefficientsObserver( coefficientsObserver );
                BarChartsNode.this.equation = equationProperty.getValue();
                BarChartsNode.this.equation.addCoefficientsObserver( coefficientsObserver );
            }
        } );
    }

    /*
     * Updates this node's entire geometry and layout
     */
    private void updateNode() {
        updateChart( reactantsChartParent, equation.getReactants(), true /* isReactants */ );
        updateChart( productsChartParent, equation.getProducts(), false /* isReactants */ );
        updateEqualitySign();
        updateLayout();
    }

    /*
     * Creates a bar chart under a parent node.
     */
    private void updateChart( PNode parent, EquationTerm[] terms, boolean isReactants ) {
        parent.removeAllChildren();
        double x = 0;
        ArrayList<AtomCount> atomCounts = equation.getAtomCounts();
        for ( AtomCount atomCount : atomCounts ) {
            int count = ( isReactants ? atomCount.getReactantsCount() : atomCount.getProductsCount() );
            BarNode barNode = new BarNode( atomCount.getAtom(), count );
            barNode.setOffset( x, 0 );
            parent.addChild( barNode );
            x = barNode.getFullBoundsReference().getMaxX() + 60;
        }
    }

    /*
     * Updates the equality sign.
     */
    private void updateEqualitySign() {
        // visibility
        equalsSignNode.setVisible( equation.isAllCoefficientsZero() || equation.isBalanced() );
        notEqualsSignNode.setVisible( !equalsSignNode.getVisible() );
        // color
        equalsSignNode.setPaint( equation.isBalanced() ? BCEColors.BALANCED_ARROW_COLOR : BCEColors.UNBALANCED_ARROW_COLOR );
    }

    /*
     * Updates the layout.
     */
    private void updateLayout() {

        final double xSpacing = 80;

        // equals sign at center
        double x = aligner.getCenterXOffset() - ( equalsSignNode.getFullBoundsReference().getWidth() / 2 );
        double y = -equalsSignNode.getFullBoundsReference().getHeight() / 2;
        equalsSignNode.setOffset( x, y );

        // not-equals sign at center
        x = aligner.getCenterXOffset() - ( notEqualsSignNode.getFullBoundsReference().getWidth() / 2 );
        y = -notEqualsSignNode.getFullBoundsReference().getHeight() / 2;
        notEqualsSignNode.setOffset( x, y );

        // reactants chart to the left
        x = equalsSignNode.getFullBoundsReference().getMinX() - reactantsChartParent.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( reactantsChartParent ) - xSpacing;
        y = 0;
        reactantsChartParent.setOffset( x, y );

        // products chart to the right
        x = equalsSignNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( productsChartParent ) + xSpacing;
        y = 0;
        productsChartParent.setOffset( x, y );
    }

    private static class EqualsSignNode extends PComposite {

        private final PPath topNode, bottomNode;

        public EqualsSignNode() {

            Rectangle2D shape = new Rectangle2D.Double( 0, 0, 35, 6 );
            Stroke stroke = new BasicStroke( 1f );
            Color strokeColor = Color.BLACK;

            topNode = new PPath( shape );
            topNode.setStroke( stroke );
            topNode.setStrokePaint( strokeColor );
            addChild( topNode );

            bottomNode = new PPath( shape );
            bottomNode.setStroke( stroke );
            bottomNode.setStrokePaint( strokeColor );
            addChild( bottomNode );

            // layout
            topNode.setOffset( 0, 0 );
            bottomNode.setOffset( 0, topNode.getFullBoundsReference().getHeight() + 6 );
        }

        public void setPaint( Paint paint ) {
            super.setPaint( paint );
            topNode.setPaint( paint );
            bottomNode.setPaint( paint );
        }
    }

    private static class NotEqualsSignNode extends PText {
        public NotEqualsSignNode() {
            super( BCESymbols.NOT_EQUALS );
            setFont( new PhetFont( Font.BOLD, 60 ) );
            setTextPaint( BCEColors.UNBALANCED_ARROW_COLOR );
        }
    }
}
