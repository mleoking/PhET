package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.util.ArrayList;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Spinner node, encapsulates the use of JSpinner in case we can to switch to some custom control.
 */
public class IntegerSpinnerNode extends PNode {
    
    private final ArrayList<ChangeListener> listeners;
    private final JSpinner spinner;
    
    public IntegerSpinnerNode( IntegerRange range ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        
        spinner = new JSpinner();
        addChild( new PSwing( spinner ) );
        spinner.setModel( new SpinnerNumberModel( range.getDefault(), range.getMin(), range.getMax(), 1 ) );
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireStateChange();
            }
        });
    }
    
    public void setValue( int value ) {
        spinner.setValue( new Integer( value ) );
    }

    public int getValue() {
        return ( (Integer) spinner.getValue() ).intValue();
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChange() {
        ChangeEvent e = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( e );
        }
    }

}
