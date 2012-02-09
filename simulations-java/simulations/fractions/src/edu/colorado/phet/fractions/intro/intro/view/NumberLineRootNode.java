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
public class NumberLineRootNode extends PNode {
    public NumberLineRootNode() {
        double dx = 5 * 10;
        addChild( new PhetPPath( new Line2D.Double( 0, 0, dx, 0 ) ) );
        {
            final PhetPPath zeroLine = new PhetPPath( new Line2D.Double( 0 * dx, -10, 0 * dx, 10 ), new BasicStroke( 1 ), Color.black );
            addChild( zeroLine );
            addChild( new PhetPText( "0", new PhetFont( 10 ) ) {{
                setOffset( zeroLine.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, zeroLine.getFullBounds().getMaxY() );
            }} );
        }

        final PhetPPath oneLine = new PhetPPath( new Line2D.Double( 1 * dx, -10, 1 * dx, 10 ), new BasicStroke( 1 ), Color.black );
        addChild( oneLine );

        addChild( new PhetPText( "1", new PhetFont( 10 ) ) {{
            setOffset( oneLine.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, oneLine.getFullBounds().getMaxY() );
        }} );
    }
}
