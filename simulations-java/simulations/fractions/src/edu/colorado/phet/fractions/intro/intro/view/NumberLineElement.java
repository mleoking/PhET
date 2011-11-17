// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class NumberLineElement extends NumberLineRootNode implements RepIcon {
    public NumberLineElement( final Property<ChosenRepresentation> chosenRepresentation ) {

        final PhetPPath child = new PhetPPath( getFullBounds(), new Color( 0, 0, 0, 0 ) );
        addChild( child );
        child.moveToBack();

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                chosenRepresentation.set( ChosenRepresentation.NUMBER_LINE );
            }
        } );
    }

    public PNode getNode() {
        return this;
    }

    public ChosenRepresentation getRepresentation() {
        return ChosenRepresentation.NUMBER_LINE;
    }
}
