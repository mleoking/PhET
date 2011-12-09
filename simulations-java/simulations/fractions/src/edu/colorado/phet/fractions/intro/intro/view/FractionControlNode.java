// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that shows a fraction (numerator and denominator and dividing line) along with controls to change the values.
 * Layout is not normalized (top left is not 0,0)
 *
 * @author Sam Reid
 */
public class FractionControlNode extends PNode {
    public FractionControlNode( final IntegerProperty numerator, final IntegerProperty denominator ) {
        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 150, 0 ), new BasicStroke( 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), Color.black );
        addChild( line );
        addChild( new NumeratorSpinner( numerator, denominator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() - getFullBounds().getHeight() );
        }} );
        addChild( new DenominatorSpinner( denominator ) {{
            setOffset( line.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, line.getFullBounds().getY() );
        }} );
    }
}