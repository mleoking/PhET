package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Electron;
import edu.colorado.phet.cck.model.ParticleSet;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 18, 2006
 * Time: 11:23:41 AM
 * Copyright (c) Sep 18, 2006 by Sam Reid
 */

public class ElectronSetNode extends PNode {
    private CircuitNode circuitNode;
    private CCKModel model;

    public ElectronSetNode( final CircuitNode circuitNode, CCKModel model ) {
        this.circuitNode = circuitNode;
        this.model = model;
        model.getParticleSet().addListener( new ParticleSet.Listener() {
            public void particlesRemoved( Electron[] electrons ) {
                for( int k = 0; k < electrons.length; k++ ) {
                    Electron electron = electrons[k];
                    for( int i = 0; i < getChildrenCount(); i++ ) {
                        PNode child = getChild( i );
                        if( child instanceof ElectronNode && ( (ElectronNode)child ).getElectron() == electron ) {
                            removeChild( child );
                            i--;
                        }
                    }
                }
            }

            public void particleAdded( Electron e ) {
                ElectronNode node = new ElectronNode( e, circuitNode.getClipFactory() );
                addChild( node );
            }
        } );
    }

}
