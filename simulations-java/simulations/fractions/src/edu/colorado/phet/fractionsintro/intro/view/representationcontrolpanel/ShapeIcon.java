// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.view.ChosenRepresentation;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class ShapeIcon extends PNode implements RepresentationIcon {
    //characteristic length
    public static double DIM = 20;
    public final ChosenRepresentation representation;

    public ShapeIcon( ArrayList<Shape> unfilled, ArrayList<Shape> filled, final SettableProperty<ChosenRepresentation> chosenRepresentation, final ChosenRepresentation representation ) {
        this.representation = representation;
        for ( Shape shape : unfilled ) {
            addChild( new PhetPPath( shape, Color.white, new BasicStroke( 1 ), Color.gray ) );
        }
        for ( Shape shape : filled ) {
            addChild( new PhetPPath( shape, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 1 ), Color.gray ) );
        }

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

    public ChosenRepresentation getRepresentation() {
        return representation;
    }
}
