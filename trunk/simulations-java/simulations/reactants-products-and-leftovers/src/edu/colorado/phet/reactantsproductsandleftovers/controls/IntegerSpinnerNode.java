// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.IntegerSpinner;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Spinner node, encapsulates the use of JSpinner in case we switch to a custom control.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerSpinnerNode extends PNode {

    private final IntegerSpinner spinner;
    private final EventListenerList listeners;

    public IntegerSpinnerNode( IUserComponent userComponent, IntegerRange range ) {
        super();

        addInputEventListener( new CursorHandler() );
        listeners = new EventListenerList();

        spinner = new IntegerSpinner( userComponent, range );
        addChild( new PSwing( spinner ) ); // addChild *after* making changes to the spinner or there will be problems, see #1824

        // propagate change events
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireStateChange();
            }
        } );
    }

    public void setEnabled( boolean enabled ) {
        spinner.setEnabled( enabled );
    }

    public void setValue( int value ) {
        spinner.setIntValue( value );
    }

    public int getValue() {
        return spinner.getIntValue();
    }

    public void addChangeListener( ChangeListener listener ) {
        spinner.addChangeListener( listener );
        listeners.add( ChangeListener.class, listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        spinner.removeChangeListener( listener );
        listeners.remove( ChangeListener.class, listener );
    }

    private void fireStateChange() {
        ChangeEvent e = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( e );
        }
    }
}
