// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows a shape as one of the selectable representations in the representation control panel.
 *
 * @author Sam Reid
 */
public class ShapeIcon extends PNode implements RepresentationIcon {
    //characteristic length
    public static double DIM = 20;
    public final Representation representation;

    public ShapeIcon( ArrayList<Shape> unfilled, ArrayList<Shape> filled, final Shape outline, final SettableProperty<Representation> chosenRepresentation, final Representation representation, Color color ) {
        this.representation = representation;
        for ( Shape shape : unfilled ) {
            addChild( new PhetPPath( shape, Color.white, new BasicStroke( 1 ), Color.gray ) );
        }
        for ( Shape shape : filled ) {
            addChild( new PhetPPath( shape, color, new BasicStroke( 1 ), Color.gray ) );
        }
        addChild( new PhetPPath( outline, new BasicStroke( 2 ), Color.black ) );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                chosenRepresentation.set( representation );
            }
        } );
    }

    public PNode getNode() {
        return this;
    }

    public Representation getRepresentation() {
        return representation;
    }
}
