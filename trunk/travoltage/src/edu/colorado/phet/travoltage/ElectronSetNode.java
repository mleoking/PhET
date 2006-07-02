/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:31 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class ElectronSetNode extends PNode {
    public void addElectronNode( ElectronNodeJade electronNode ) {
        addChild( electronNode );
        notifyListeners( electronNode );
    }

    public int getNumElectrons() {
        return getChildrenCount();
    }

    public ElectronNodeJade getElectronNode( int i ) {
        return (ElectronNodeJade)getChild( i );
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void electronAdded( ElectronNodeJade electronNode );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners( ElectronNodeJade electronNode ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronAdded( electronNode );
        }
    }
}
