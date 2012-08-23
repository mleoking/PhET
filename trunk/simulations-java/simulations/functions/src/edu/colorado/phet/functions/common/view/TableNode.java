// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class TableNode extends PNode {
    public TableNode() {
        //draw lines
        //Let's have rounded edges for the border
        final int width = 200;
        final int rowHeight = 40;
        int numDataRows = 6;
        int numRows = numDataRows + 1;
        final int height = numRows * rowHeight;
        RoundRectangle2D.Double border = new RoundRectangle2D.Double( 0, 0, width, height, 20, 20 );
        addChild( new PhetPPath( border, Color.white ) );
        addChild( new PhetPPath( new Line2D.Double( width / 2, 0, width / 2, height ), new BasicStroke( 1 ), Color.black ) );

        Area topArea = new Area( border ) {{
            subtract( new Area( new Rectangle2D.Double( -width, rowHeight, width * 3, height ) ) );
        }};

        addChild( new PhetPPath( topArea, new Color( 207, 252, 217 ) ) );
        addChild( new PhetPPath( border, new BasicStroke( 2 ), Color.black ) );

        for ( int i = 1; i < numRows; i++ ) {
            addChild( new PhetPPath( new Line2D.Double( 0, i * rowHeight, width, i * rowHeight ), new BasicStroke( 1 ), Color.black ) );
        }

        final PhetFont textFont = new PhetFont( 24 );
        PText inText = new PhetPText( "in", textFont );
        PText outText = new PhetPText( "out", textFont );

        inText.setOffset( width * 1.0 / 4.0 - inText.getFullBounds().getWidth() / 2, rowHeight - inText.getFullBounds().getHeight() );
        outText.setOffset( width * 3.0 / 4.0 - outText.getFullBounds().getWidth() / 2, rowHeight - outText.getFullBounds().getHeight() );

        addChild( inText );
        addChild( outText );

    }
}