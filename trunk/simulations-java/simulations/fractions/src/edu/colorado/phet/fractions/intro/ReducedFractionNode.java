// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.view.ObservableFractionNumberNode;

/**
 * Shows a reduced fraction like "1/3" instead of "2/6"
 *
 * @author Sam Reid
 */
public class ReducedFractionNode extends VisibilityNode {

    public ReducedFractionNode( final ObservableProperty<Fraction> fraction, ObservableProperty<Boolean> visible ) {
        super( visible );

        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 67, 0 ), new BasicStroke( 4 ), Color.black );
        addChild( line );
        addChild( new ObservableFractionNumberNode( new CompositeProperty<Integer>( new Function0<Integer>() {
            public Integer apply() {
                return fraction.get().numerator;
            }
        }, fraction ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2 - 20, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );
        addChild( new ObservableFractionNumberNode( new CompositeProperty<Integer>( new Function0<Integer>() {
            public Integer apply() {
                return fraction.get().denominator;
            }
        }, fraction ) ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2 - 20, line.getFullBounds().getY() );
        }} );
    }
}
