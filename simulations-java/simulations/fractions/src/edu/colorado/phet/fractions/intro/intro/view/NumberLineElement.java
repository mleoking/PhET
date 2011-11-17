// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class NumberLineElement extends PNode {
    public NumberLineElement() {
        double dx = 5;
        addChild( new PhetPPath( new Line2D.Double( 0, 0, dx * 10, 0 ) ) );

        for ( int i = 0; i <= 10; i++ ) {
            if ( i % 5 == 0 ) {
                final PhetPPath path = new PhetPPath( new Line2D.Double( i * dx, -10, i * dx, 10 ), new BasicStroke( 1 ), Color.black );
                addChild( path );
                if ( i == 0 ) {
                    addChild( new PhetPText( "0", new PhetFont( 10 ) ) {{
                        setOffset( path.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );
                    }} );
                }
            }
            else {
                addChild( new PhetPPath( new Line2D.Double( i * dx, -5, i * dx, 5 ), new BasicStroke( 1 ), Color.black ) );
            }
        }
    }
}
