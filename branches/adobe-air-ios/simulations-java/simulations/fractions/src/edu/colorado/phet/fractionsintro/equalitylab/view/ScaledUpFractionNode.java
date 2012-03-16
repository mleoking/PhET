// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.Times;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.view.SpinnerButtonPanelHBox;
import edu.colorado.phet.fractionsintro.intro.view.FractionNumberNode;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) as a multiple of another fraction.
 * Layout is not normalized (top left is not 0,0).
 * <p/>
 * This class is very similar to FractionControlNode
 *
 * @author Sam Reid
 */
public class ScaledUpFractionNode extends RichPNode {
    public ScaledUpFractionNode( final IntegerProperty numerator, final IntegerProperty denominator, final IntegerProperty scale ) {
        final Times scaledNumerator = scale.times( numerator );
        final Times scaledDenominator = scale.times( denominator );
        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 150, 0 ), new BasicStroke( 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), Color.black );
        addChild( line );

        //Center them over the fraction line
        final int offset = 34;
        addChild( new FractionNumberNode( scaledNumerator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2 - offset, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );
        addChild( new FractionNumberNode( scaledDenominator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2 - offset, line.getFullBounds().getY() );
        }} );

        final ZeroOffsetNode spinnerButtonPanel = new ZeroOffsetNode( new SpinnerButtonPanelHBox( new VoidFunction0() {
            public void apply() {
                scale.increment();
            }
        }, scale.lessThanOrEqualTo( 2 ), new VoidFunction0() {
            public void apply() {
                scale.decrement();
            }
        }, scale.greaterThanOrEqualTo( 2 )
        ) );

        spinnerButtonPanel.setOffset( getMaxX() + 12, getCenterY() / 2 - spinnerButtonPanel.getFullHeight() / 2 );
        addChild( spinnerButtonPanel );
    }
}