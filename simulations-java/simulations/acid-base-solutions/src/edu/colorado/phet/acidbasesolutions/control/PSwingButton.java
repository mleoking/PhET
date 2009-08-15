package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;


public class PSwingButton extends PhetPNode {
    
    private final JButton jButton;
    
    public PSwingButton() {
        this( "" );
    }
    
    public PSwingButton( String text ) {
        super();
        jButton = new JButton( text );
        jButton.setOpaque( false ); // Mac OS 10.4
        addChild( new PSwing( jButton ) );
        addInputEventListener( new CursorHandler() );
    }
    
    public void setText( String text ) {
        jButton.setText( text );
    }
    
    public void setFont( Font font ) {
        jButton.setFont( font );
    }
    
    public void setEnabled( boolean enabled ) {
        jButton.setEnabled( enabled );
    }
    
    public void addActionListener( ActionListener listener ) {
        jButton.addActionListener( listener );
    }
    
    public void removeActionListener( ActionListener listener ) {
        jButton.removeActionListener( listener );
    }

}
