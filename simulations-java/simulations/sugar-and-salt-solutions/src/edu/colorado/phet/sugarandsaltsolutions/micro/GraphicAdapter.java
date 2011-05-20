// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;

/**
 * This class provides graphics for a model element of a given type, and also adds nodes for all pre-existing model elements, and observes creation and deletion of elements
 *
 * @author Sam Reid
 */
class GraphicAdapter<T extends Removable> {
    GraphicAdapter( final PNode rootNode, final Function1<T, PNode> newNode, ArrayList<T> preExisting, VoidFunction1<VoidFunction1<T>> newModelElementAdded ) {
        //Function which creates water nodes on initialization and when they are added to the model
        VoidFunction1<T> createNode = new VoidFunction1<T>() {
            public void apply( T modelElement ) {
                //Create the node using the rule provided by the client
                final PNode node = newNode.apply( modelElement );

                //Remove the node when it leaves the model
                modelElement.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        rootNode.removeChild( node );
                    }
                } );

                //Add the node to the scene graph
                rootNode.addChild( node );
            }
        };

        //Show the circles already in the model on startup
        for ( final T waterMolecule : preExisting ) {
            createNode.apply( waterMolecule );
        }

        //Listen for subsequent additions of water molecules
        newModelElementAdded.apply( createNode );
    }
}
