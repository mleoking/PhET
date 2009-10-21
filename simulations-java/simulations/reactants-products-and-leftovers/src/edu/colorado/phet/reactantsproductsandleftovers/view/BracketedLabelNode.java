package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
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
 * A label with a horizontal bracket.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BracketedLabelNode extends PComposite {
    
    private static final double BRACKET_END_HEIGHT = 5;
    private static final Stroke BRACK_STROKE = new BasicStroke( 0.75f );
    private static final Color BRACK_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final PhetFont TEXT_FONT = new PhetFont();

    private final BracketNode bracketNode;
    private final PText textNode;
    
    public BracketedLabelNode( String label, double width ) {
        super();
        
        bracketNode = new BracketNode( width );
        addChild( bracketNode );
        
        textNode = new PText( label );
        textNode.setFont( TEXT_FONT );
        textNode.setTextPaint( TEXT_COLOR );
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
        double x = 0;
        double y = 0;
        bracketNode.setOffset( x, y );
        x = bracketNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBounds().getWidth() / 2 );
        y = bracketNode.getFullBoundsReference().getMaxY() + 2;
        textNode.setOffset( x, y ); 
    }
    
    public void setBracketStroke( Stroke stroke ) {
        bracketNode.setStroke( stroke );
    }
    
    public void setBracketStrokePaint( Paint paint ) {
        bracketNode.setStrokePaint( paint );
    }
    
    public void setText( String text ) {
        textNode.setText( text );
    }
    
    public void setTextFont( PhetFont font ) {
        textNode.setFont( font );
    }
    
    public void setTextPaint( Paint paint ) {
        textNode.setTextPaint( paint );
    }
    
    private static class BracketNode extends PPath {
        
        public BracketNode( double width ) {
            super();
            setStroke( BRACK_STROKE );
            setStrokePaint( BRACK_COLOR );
            
            GeneralPath path = new GeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 0, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) width, (float) BRACKET_END_HEIGHT );
            path.lineTo( (float) width, 0 );
            setPathTo( path );
        }
    }
}
