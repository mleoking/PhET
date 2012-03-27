// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating the source-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationNode extends PhetPNode {

    private static final String PATTERN = "{0} = {1}{2} + {3}"; // eg, y = mx + b

    // y = mx + b
    private static String EQUATION = MessageFormat.format( PATTERN,
                                                         Strings.SYMBOL_VERTICAL_AXIS,
                                                         Strings.SYMBOL_SLOPE,
                                                         Strings.SYMBOL_HORIZONTAL_AXIS,
                                                         Strings.SYMBOL_INTERCEPT );

    public SlopeInterceptEquationNode() {
        addChild( new PPath( new Rectangle2D.Double( 0, 0, 350, 200 ) ) ); //TODO placeholder for layout
        addChild( new PText( EQUATION ) {{
            setOffset( 10, 10 );
        }} );
    }
}
