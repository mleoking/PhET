/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.umd.cs.piccolo.PNode;


public class DrainControlNode extends PNode {

    private final ArrayList _listeners;
    private final FaucetControlNode _faucetControlNode;
    
    public DrainControlNode() {
        super();
        
        _listeners = new ArrayList();
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        
        addChild( _faucetControlNode );
    }
    
    public void setOn( boolean on ) {
        _faucetControlNode.setOn( on );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    public interface DrainControlListener {
        public void onOffChanged();
    }
    
    public void addDrainControlListener( DrainControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeDrainControlListener( DrainControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyOnOffChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (DrainControlListener) i.next() ).onOffChanged();
        }
    }
}
