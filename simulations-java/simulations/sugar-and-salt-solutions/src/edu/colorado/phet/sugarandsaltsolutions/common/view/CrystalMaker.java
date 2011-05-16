// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;
import edu.umd.cs.piccolo.PNode;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author Sam Reid
 */
public class CrystalMaker<T extends MacroCrystal> implements VoidFunction1<T> {
    private final ModelViewTransform transform;
    private final PNode layer;
    private final Function1<T, PNode> createNode;

    public CrystalMaker( ModelViewTransform transform, PNode layer, Function1<T, PNode> createNode ) {
        this.transform = transform;
        this.layer = layer;
        this.createNode = createNode;
    }

    public void apply( final T salt ) {
        //Create the node
        final PNode node = createNode.apply( salt );

        //Set up to remove the node and its listener when salt crystal removed from the model
        salt.addRemovalListener( new VoidFunction0() {
            public void apply() {
                layer.removeChild( node );

                //Store a reference to the removalListener instance, for use in the anonymous inner class below
                final VoidFunction0 removalListener = this;

                //invoke later to avoid concurrentmodificationexception, since this is called during Crystal.remove()
                //This code should be read with IntelliJ's closure folding
                invokeLater( new Runnable() {
                    public void run() {
                        salt.removeRemovalListener( removalListener );
                    }
                } );
            }
        } );
        layer.addChild( node );
    }
}