// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.FractionsResources;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Representation control panel icon for cake.
 *
 * @author Sam Reid
 */
public class CakeIcon extends PNode implements RepresentationIcon {

    public CakeIcon( final SettableProperty<Representation> selected ) {
        addChild( new PImage( FractionsResources.RESOURCES.getImage( "cake/cake_1_1.png" ) ) );

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

    public Representation getRepresentation() {
        return Representation.CAKE;
    }
}
