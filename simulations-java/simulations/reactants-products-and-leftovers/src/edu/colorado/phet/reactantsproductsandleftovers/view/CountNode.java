package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class CountNode extends PComposite {
    
    private final PText textNode;
    private final PNode imageNode;
    private int value;
    
    public CountNode( int value, PNode imageNode, double imageScale ) {
        super();
        
        this.value = value;
        this.imageNode = imageNode;
        
        textNode = new PText( String.valueOf( value ) );
        textNode.setFont( new PhetFont() );
        addChild( textNode );
        
        addChild( imageNode );
        imageNode.scale( imageScale );
        imageNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( evt.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        });
       
        updateLayout();
    }
    
    public void setValue( int value ) {
        if ( value != this.value ) {
            textNode.setText( String.valueOf( value ) );
            this.value = value;
            updateLayout();
        }
    }
    
    public int getValue() {
        return value;
    }
    
    private void updateLayout() {
        double x = -textNode.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        textNode.setOffset( x, y );
        x = -imageNode.getFullBoundsReference().getWidth() / 2;
        y = textNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( imageNode ) + 10;
        imageNode.setOffset( x, y );
    }
}
