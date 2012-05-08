package edu.colorado.phet.fractionsintro.buildafraction.model;

import edu.colorado.phet.fractionsintro.buildafraction.view.ObjectID;

/**
 * @author Sam Reid
 */
public class DraggableNumberID extends ObjectID {
    public DraggableNumberID( final int id ) { super( id ); }

    public static int count = 0;

    public static DraggableNumberID nextID() { return new DraggableNumberID( count++ ); }
}