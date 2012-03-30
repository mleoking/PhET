// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A text label centered below a horizontal bracket.
 * Origin at upper-left of bounding box.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BracketLabelNode extends PComposite {

    private static final double BRACKET_END_HEIGHT = 5;
    private static final double BRACKET_TIP_WIDTH = 6;
    private static final double BRACKET_TIP_HEIGHT = 6;

    private static final double TEXT_Y_SPACING = 2;

    public BracketLabelNode( String label, double length, PhetFont font, Color textColor, Color bracketColor, Stroke bracketStroke ) {
        assert( length > 0 );

        PNode bracketNode = new BracketNode( length, bracketColor, bracketStroke );
        addChild( bracketNode );
        
        PText textNode = new PText( label );
        textNode.setFont( font );
        textNode.setTextPaint( textColor );
        addChild( textNode );

        // layout
        {
            // origin at upper-left of bracket
            double x = 0;
            double y = 0;
            bracketNode.setOffset( x, y );
            // text centered below bracket
            x = bracketNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBounds().getWidth() / 2 );
            y = bracketNode.getFullBoundsReference().getMaxY() + TEXT_Y_SPACING;
            textNode.setOffset( x, y );
        }
    }

    private static class BracketNode extends PPath {

        public BracketNode( double width, Paint paint, Stroke stroke ) {
            super();
            setStroke( stroke );
            setStrokePaint( paint );
            
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 0, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float)(( width - BRACKET_TIP_WIDTH ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float)( width / 2 ), (float)( BRACKET_END_HEIGHT + BRACKET_TIP_HEIGHT ) );
            path.lineTo( (float)(( width + BRACKET_TIP_WIDTH ) / 2 ), (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) width, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) width, 0 );
            setPathTo( path );
        }
    }
}
