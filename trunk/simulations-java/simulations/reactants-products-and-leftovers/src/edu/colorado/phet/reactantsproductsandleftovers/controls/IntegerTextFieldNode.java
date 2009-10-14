
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.umd.cs.piccolox.pswing.PSwing;


public class IntegerTextFieldNode extends PhetPNode {
    
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( 2, 2, 2, 2 ) );
    
    private final ArrayList<ChangeListener> listeners;
    private final IntegerRange range;
    private final JTextField textField;
    private int value;

    public IntegerTextFieldNode( IntegerRange range, PhetFont font ) {
        super();
        addInputEventListener( new CursorHandler() );
        
        this.range = new IntegerRange( range );
        value = range.getDefault();
        listeners = new ArrayList<ChangeListener>();
        
        textField = new JTextField( String.valueOf( value ) );
        textField.setFont( font );
        textField.setBorder( BORDER );
        textField.setHorizontalAlignment( JTextField.RIGHT );
        textField.setColumns( String.valueOf( range.getMax() ).length() );
        textField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                commit();
            }
        });
        textField.addFocusListener( new FocusListener() {
            /*
             * Workaround to select contents when textfield get focus.
             * See bug ID 4699955 at bugs.sun.com
             */
            public void focusGained( FocusEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        textField.selectAll();
                    }
                });
            }
            public void focusLost( FocusEvent e ) {
                commit();
            } 
        });
        
        addChild( new PSwing( textField ) );
    }
    
    public void setValue( int value ) {
        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }
        if ( value != this.value ) {
            this.value = value;
            textField.setText( String.valueOf( value ) );
        }
    }
    
    public int getValue() {
        return value;
    }
    
    /*
     * If we can't commit the value in the text field, then revert.
     */
    private void commit() {
        int revertValue = value;
        try {
            value = Integer.parseInt( textField.getText() );
            if ( range.contains( value ) ) {
                fireStateChanged();
            }
            else {
                revert( revertValue );
            }
        }
        catch ( NumberFormatException e ) {
            revert( revertValue );
        }
    }
    
    private void revert( int revertValue ) {
        value = revertValue;
        textField.setText( String.valueOf( value ) );
        Toolkit.getDefaultToolkit().beep();
        showInvalidValueDialog();
        textField.selectAll();
    }
    
    private void showInvalidValueDialog() {
        Object[] args = { new Integer( range.getMin() ), new Integer( range.getMax() ) };
        String message = MessageFormat.format( RPALStrings.MESSAGE_VALUE_OUT_OF_RANGE, args );
        PhetOptionPane.showErrorDialog( textField, message );
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( event );
        }
    }
}
