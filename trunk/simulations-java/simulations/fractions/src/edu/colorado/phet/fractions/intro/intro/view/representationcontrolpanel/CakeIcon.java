// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view.representationcontrolpanel;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.intro.intro.model.Container;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.colorado.phet.fractions.intro.intro.view.CakeNode;
import edu.colorado.phet.fractions.intro.intro.view.ChosenRepresentation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Representation control panel icon for cake.
 *
 * @author Sam Reid
 */
public class CakeIcon extends PNode implements RepresentationIcon {

    public CakeIcon( final Property<ChosenRepresentation> selected ) {
        addChild( new CakeNode( 2, new int[] { 1, 2 }, new Property<ContainerState>( new ContainerState( 2, new Container[] { } ) ), 1, new int[] { 1 } ) );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                selected.set( getRepresentation() );
            }
        } );
        scale( 0.5 );
    }

    public PNode getNode() {
        return this;
    }

    public ChosenRepresentation getRepresentation() {
        return ChosenRepresentation.CAKE;
    }
}
