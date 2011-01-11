// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A pair of boxes that show the number of molecules indicated by the equation coefficients.
 * Left box is for the reactants, right box is for the products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeforeAfterBoxesNode extends PComposite {

    public BeforeAfterBoxesNode( final Property<Equation> equationProperty, IntegerRange coefficientRange ) {

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
}
