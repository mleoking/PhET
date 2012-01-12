// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:31 PM
 */

public class ElectronSetNode extends PNode {
    public ElectronSetNode( JadeElectronSet set ) {
        set.addListener( new JadeElectronSet.Listener() {
            public void electronAdded( JadeElectron electron ) {
                //todo handled elsewhere currently
            }

            public void electronRemoved( JadeElectron electron ) {
                removeElectronNode( electron );
            }

        } );
    }

    private void removeElectronNode( JadeElectron electron ) {
        for( int i = 0; i < getNumElectrons(); i++ ) {
            if( getElectronNode( i ).getJadeElectron() == electron ) {
                removeChild( getElectronNode( i ) );
                i--;
            }
        }
    }

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
