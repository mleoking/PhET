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
    public void addElectronNode( JadeElectronNode electronNode ) {
        addChild( electronNode );
        notifyListeners( electronNode );
    }

    public int getNumElectrons() {
        return getChildrenCount();
    }

    public JadeElectronNode getElectronNode( int i ) {
        return (JadeElectronNode)getChild( i );
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void electronAdded( JadeElectronNode electronNode );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners( JadeElectronNode electronNode ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronAdded( electronNode );
        }
    }
}
