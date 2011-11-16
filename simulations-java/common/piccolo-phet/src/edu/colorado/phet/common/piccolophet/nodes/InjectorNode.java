// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

//REVIEW rather than copying, generalize ParticleInjectorNode and migrate to piccolo-phet

/**
 * Node that represents the thing with which the user interacts in order to
 * inject particles (generally ions) into the chamber.
 * <p/>
 * Copied from ParticleInjectorNode in membrane-channels on 12-9-2010
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class InjectorNode extends NonInteractiveInjectorNode {

    /*
     * Constructs a particle injection node.
     *
     * @param mvt           - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     */
    public InjectorNode( double rotationAngle, final SimpleObserver inject ) {
        super( rotationAngle, inject );
        buttonImageNode.addInputEventListener( new CursorHandler() );
        buttonImageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                buttonImageNode.setImage( pressedButtonImage );
                inject.update();
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                buttonImageNode.setImage( unpressedButtonImage );
            }
        } );
    }
}