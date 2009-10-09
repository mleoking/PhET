package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ProductQuantityNode extends PComposite {
    
    private final PText textNode;
    private int value;
    
    public ProductQuantityNode( int value ) {
        super();
        this.value = value;
        textNode = new PText( String.valueOf( value ) );
        textNode.setFont( new PhetFont() );
        addChild( textNode );
    }
    
    public void setValue( int value ) {
        if ( value != this.value ) {
            textNode.setText( String.valueOf( value ) );
            this.value = value;
        }
    }
    
    public int getValue() {
        return value;
    }
}
