// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A text label centered below a horizontal bracket.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BracketedLabelNode extends PComposite {
    
    private static final double BRACKET_END_HEIGHT = 5;
    private static final double BRACKET_TIP_WIDTH = 6;
    private static final double BRACKET_TIP_HEIGHT = 6;

    private static final double TEXT_Y_SPACING = 2;

    private final BracketNode bracketNode;
    private final PText textNode;
    
    public BracketedLabelNode( String label, double width, PhetFont font, Color textColor, Color bracketColor, Stroke bracketStroke ) {
        super();
        
        bracketNode = new BracketNode( width, bracketColor, bracketStroke );
        addChild( bracketNode );
        
        textNode = new PText( label );
        textNode.setFont( font );
        textNode.setTextPaint( textColor );
        addChild( textNode );
        
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
        
        updateLayout();
    }
    
    private void updateLayout() {
        // origin at upper-left of bracket
        double x = 0;
        double y = 0;
        bracketNode.setOffset( x, y );
        // text centered below bracket
        x = bracketNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBounds().getWidth() / 2 );
        y = bracketNode.getFullBoundsReference().getMaxY() + TEXT_Y_SPACING;
        textNode.setOffset( x, y ); 
    }
    
    public void setBracketStrokePaint( Paint paint ) {
        bracketNode.setStrokePaint( paint );
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
